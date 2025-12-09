package ca.senecapolytechnic.application.apd545project.services;

import ca.senecapolytechnic.application.apd545project.AppConfig;
import ca.senecapolytechnic.application.apd545project.config.PricingPolicy;
import ca.senecapolytechnic.application.apd545project.models.*;
import ca.senecapolytechnic.application.apd545project.repositories.*;
import ca.senecapolytechnic.application.apd545project.security.AuthService;
import com.google.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WaitlistServiceImpl implements WaitlistService{
    private final WaitlistRepository waitlistRepository;
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final AuditLogRepository auditLogRepository;
    private final ReservationRoomRepository reservationRoomRepository;
    @Inject
    public WaitlistServiceImpl(WaitlistRepository waitlistRepository, ReservationRepository reservationRepository,
                               RoomRepository roomRepository, AuditLogRepository auditLogRepository,
                               ReservationRoomRepository reservationRoomRepository) {
        this.waitlistRepository = waitlistRepository;
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.auditLogRepository = auditLogRepository;
        this.reservationRoomRepository = reservationRoomRepository;

    }
    @Inject
    private ActivityLogService activityLogService;
    @Inject
    private AuthService authService;
    @Inject
    private PricingPolicy pricingPolicy;
    @Inject
    private LoyaltyService loyaltyService;

    public boolean isRoomTypeAvailableForRange(RoomType type, LocalDateTime start, LocalDateTime end) {
        List<Room> roomsOfType = roomRepository.findAvailable(type, start.toLocalDate(), end.toLocalDate()); // you should implement this repo method
        if (roomsOfType.isEmpty()) return false;

        for (Room room : roomsOfType) {
            boolean overlapping = reservationRoomRepository.existsOverlapForRoom(room.getId(), start.toLocalDate(), end.toLocalDate());
            if (!overlapping) return true;
        }
        return false;
    }

    public List<Waitlist> findMatchingWaitlistEntries() {
        List<Waitlist> pending = waitlistRepository.findByStatus("PENDING");
        List<Waitlist> matches = new ArrayList<>();
        for (Waitlist w : pending) {
            if (isRoomTypeAvailableForRange(w.getRequestedType(), w.getDateRangeStart(), w.getDateRangeEnd())) {
                matches.add(w);
            }
        }
        return matches;
    }

    public void markNotified(Waitlist w) {
        AdminUser loggedInAdmin = authService.getCurrentAdmin();
        w.setStatus("NOTIFIED");
        waitlistRepository.save(w);
        activityLogService.log(loggedInAdmin.getUsername() + " (" + loggedInAdmin.getRole() + ")",
                "UPDATED_ROOM_AVAILABILITY", "Waitlist", w.getId().intValue(),
                "Guest " + w.getGuest().getName() + " on waitlist has been notified by admin of room availability");
    }

    public void convertWaitlistToReservation(Waitlist w) {
        EntityManager em = AppConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            List<Room> availableRoom = roomRepository.findAvailable(
                    w.getRequestedType(),
                    w.getDateRangeStart().toLocalDate(),
                    w.getDateRangeEnd().toLocalDate()
            );

            if (availableRoom.isEmpty()) {
                throw new IllegalStateException("No available room found for conversion.");
            }
            tx.begin();
            Reservation r = new Reservation();
            r.setCheckIn(w.getDateRangeStart());
            r.setCheckOut(w.getDateRangeEnd());
            r.setGuest(w.getGuest());
            r.setNumAdults(1); // i am setting defaults here since waitlist does not do this
            r.setNumChildren(0);
            r.setReservationStatus(ReservationStatus.CONFIRMED);
            em.persist(r);
            em.flush();

            // rooms
            ReservationRoom rr = new ReservationRoom();
            rr.setReservation(r);
            rr.setRoom(availableRoom.get(0)); // the firsdt available room

            em.persist(rr);
            // calculating the bill
            int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(r.getCheckIn(), r.getCheckOut()); // reuse this from ReservationObj
            double roomCharges = rr.getRoom().getBasePrice() + nights;

            double subTotal = roomCharges + 0; // just a formality - we dont have any addons for this
            double tax = subTotal * pricingPolicy.getTaxRate();
            double total = subTotal + tax;

            Billing billing = new Billing(r, subTotal, pricingPolicy.getTaxRate(), tax, 0.0, 0, total, 0.0, total, "UNPAID");
            em.persist(billing);
            // finally accrue the loyalty points
            loyaltyService.assignLoyaltyNumber(w.getGuest());
            loyaltyService.addPoints(w.getGuest(), total);
            em.merge(w.getGuest());

            tx.commit();
            em.close();

        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Failed to create reservation: " + ex.getMessage(), ex);
        }

    }


}
