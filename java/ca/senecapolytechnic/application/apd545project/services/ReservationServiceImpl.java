package ca.senecapolytechnic.application.apd545project.services;

import ca.senecapolytechnic.application.apd545project.models.*;
import ca.senecapolytechnic.application.apd545project.repositories.*;
import ca.senecapolytechnic.application.apd545project.utils.ReservationObj;
import ca.senecapolytechnic.application.apd545project.utils.Validator;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class ReservationServiceImpl implements ReservationService {

    private final RoomRepository roomRepo;
    private final GuestRepository guestRepo;
    private final ReservationRepository reservationRepo;
    private final ReservationRoomRepository reservationRoomRepo;
    private final ReservationAddonRepository reservationAddonRepo;
    private final AddonRepository addonRepo;
    private final BillingRepository billingRepo;
    private final LoyaltyService loyaltyService;
    private final EntityManager em;

    // ontario tax rate
    private static final double TAX_RATE = 0.13;

    @Inject
    public ReservationServiceImpl(RoomRepository roomRepo,
                                  GuestRepository guestRepo,
                                  ReservationRepository reservationRepo,
                                  ReservationRoomRepository reservationRoomRepo,
                                  ReservationAddonRepository reservationAddonRepo,
                                  AddonRepository addonRepo,
                                  BillingRepository billingRepo,
                                  LoyaltyService loyaltyService,
                                  EntityManager em) {
        this.roomRepo = roomRepo;
        this.guestRepo = guestRepo;
        this.reservationRepo = reservationRepo;
        this.reservationRoomRepo = reservationRoomRepo;
        this.reservationAddonRepo = reservationAddonRepo;
        this.addonRepo = addonRepo;
        this.billingRepo = billingRepo;
        this.loyaltyService = loyaltyService;
        this.em = em;
    }

    @Override
    public Reservation createReservation(ReservationObj req) throws RuntimeException {
        // first check all good
        try {
            Validator.validateDates(req.checkIn, req.checkOut);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
        int nights = req.totalNights();
        if (nights <= 0) throw new RuntimeException("Reservation must be for at least 1 night");

        int totalPeople = req.numAdults + req.numChildren;

        // validate requested room counts and capacity constraints
        int roomsRequested = req.requestedRooms.values().stream().mapToInt(Integer::intValue).sum();
        if (roomsRequested <= 0) throw new RuntimeException("Must request at least one room");

        // check capacity: compute potential capacity from requested rooms
        int capacity = req.requestedRooms.entrySet().stream().mapToInt(e -> {
            RoomType type = e.getKey();
            int count = e.getValue();
            int cap = 0;
            switch (type) {
                case SINGLE:
                case DELUXE:
                case PENTHOUSE:
                    cap = 2;
                    break;
                case DOUBLE:
                    cap = 4;
                    break;
            };
            return cap * count;
        }).sum();

        if (totalPeople > capacity) {
            throw new RuntimeException("Selected rooms cannot accommodate the requested number of people");
        }

        // check room availability for each requested RoomType for the date range
        LocalDate checkIn = req.checkIn;
        LocalDate checkOut = req.checkOut;

        // check roomRepo for available rooms per type and date-range.
        // RoomRepository method: List<Room> findAvailable(RoomType type, LocalDate from, LocalDate to)
        Map<RoomType, List<Room>> availableByType = new HashMap<>();
        for (Map.Entry<RoomType, Integer> e : req.requestedRooms.entrySet()) {
            RoomType type = e.getKey();
            int needed = e.getValue();
            if (needed <= 0) continue;
            List<Room> available = roomRepo.findAvailable(type, checkIn, checkOut);
            if (available.size() < needed) {
                throw new RuntimeException("Not enough " + type + " rooms available. Needed " + needed + " found " + available.size());
            }
            availableByType.put(type, available);
        }

        // if all validations passed then create entities in a single transaction
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // we create a new Guest if lookup by phone fails to find an existing one
            Guest guest = guestRepo.findByPhone(req.phone);
            if (guest == null) {
                String fullName = (req.firstName == null ? "" : req.firstName.trim())
                        + (req.lastName == null ? "" : " " + req.lastName.trim());
                guest = new Guest(fullName, req.phone, req.email, req.address, 0, 0, true);
                //guestRepo.save(guest); // can cause multi em transactions
                em.persist(guest); // persist with the same em
                em.flush();
            }

            // create a reservation
            Reservation reservation = new Reservation(
                    checkIn.atStartOfDay(),
                    checkOut.atStartOfDay(),
                    req.numAdults,
                    req.numChildren,
                    ReservationStatus.CONFIRMED,
                    guest // this now stores a guest object so i can identify who made the reservation

            );
            //reservationRepo.save(reservation); // can cause multi em transactions
            em.persist(reservation);
            em.flush();

            // assign rooms to reservation = just pick first available rooms from each type
            List<Room> assignedRooms = new ArrayList<>();
            for (Map.Entry<RoomType, Integer> e : req.requestedRooms.entrySet()) {
                RoomType type = e.getKey();
                int needed = e.getValue();
                if (needed <= 0) continue;
                List<Room> available = availableByType.get(type);
                for (int i = 0; i < needed; i++) {
                    Room room = available.get(i);
                    // IMPORTANT: re-attach room to EM/session
                    Room managedRoom = em.find(Room.class, room.getId());
                    if (managedRoom == null) managedRoom = em.merge(room);
                    assignedRooms.add(room);
                    //ReservationRoom rr = new ReservationRoom(reservation, room);
                    ReservationRoom rr = new ReservationRoom(reservation, managedRoom);
                    //reservationRoomRepo.save(rr);  // can cause multi em transactions
                    em.persist(rr);
                    // update room status
                    //room.setRoomStatus(RoomStatus.OCCUPIED);
                    // DO NOT MARK ROOM OCCUPIED if reservation starts in future, only mark if currently in-range
                    LocalDate today = LocalDate.now();
                    if (!(reservation.getCheckOut().toLocalDate().isBefore(today) ||
                            reservation.getCheckIn().toLocalDate().isAfter(today))) {
                        // reservation is active today = set realtime OCCUPIED
                        managedRoom.setRoomStatus(RoomStatus.OCCUPIED);
                        em.merge(managedRoom);
                    }
                }
            }

            // services
            double addonsTotal = 0.0;
            if (req.addons != null && !req.addons.isEmpty()) {
                for (Map.Entry<Long, Integer> e : req.addons.entrySet()) {
                    Long addonId = e.getKey();
                    int qty = e.getValue();
                    ServiceAddon addon = addonRepo.findById(addonId);
                    if (addon == null) throw new RuntimeException("Addon id " + addonId + " not found");

                    int effectiveQty = qty; // for per-person or per-night multiply below
                    double itemTotal;
                    if (addon.getPricingModel() == PricingModel.PER_RESERVATION) {
                        // one-time
                        itemTotal = addon.getPrice() * qty;
                    } else { // PER_NIGHT
                        // breakfast/spa require per person, otherwise it's qty * nights
                        itemTotal = addon.getPrice() * qty * nights;
                    }
                    addonsTotal += itemTotal;
                    ReservationAddon ra = new ReservationAddon(reservation, addon, qty);
                    // reservationAddonRepo.save(ra); // can cause multi em transactions
                    em.persist(ra);
                }
            }

            // rooms
            double roomCharges = 0.0;
            for (Room r : assignedRooms) {
                roomCharges += r.getBasePrice() * nights;
            }

            double subTotal = roomCharges + addonsTotal;
            double tax = subTotal * TAX_RATE;
            double total = subTotal + tax;

            Billing billing = new Billing(reservation, subTotal, TAX_RATE, tax, 0.0, 0, total, 0.0, total, "UNPAID");
            // billingRepo.save(billing); // can cause multi em transactions
            em.persist(billing);
            // only happens if it doesnt exist already
            loyaltyService.assignLoyaltyNumber(guest);
            loyaltyService.addPoints(guest, total);
            em.merge(guest); // if loyaltyService updates guest, forgot to merge

            tx.commit();
            return reservation;
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Failed to create reservation: " + ex.getMessage(), ex);
        }
    }

    @Override
    public int lookupGuestLoyaltyByPhone(String phone) {
        // this function is not currently used
        Guest guest = guestRepo.findByPhone(phone);
        if (guest.getLoyaltyNumber() > 0) {
            return guest.getLoyaltyNumber();
        }
        return 0;
    }
}