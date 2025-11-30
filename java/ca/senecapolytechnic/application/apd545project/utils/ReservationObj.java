package ca.senecapolytechnic.application.apd545project.utils;

import ca.senecapolytechnic.application.apd545project.models.RoomType;

import java.time.LocalDate;
import java.util.Map;

public class ReservationObj {
    public LocalDate checkIn;
    public LocalDate checkOut;
    public int numAdults;
    public int numChildren;

    // requestedRooms: map RoomType -> count
    public Map<RoomType, Integer> requestedRooms;

    // Guest fields
    public String title;
    public String firstName;
    public String lastName;
    public String phone;
    public String email;
    public String address;

    // addons: map addonId -> quantity (or addon name -> quantity)
    public Map<Long, Integer> addons; // if you use ids; alter if you want names instead

    // convenience:
    public int totalNights() {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
    }
    public int totalGuests() {
        return numAdults + numChildren;
    }

    public int totalRequestedRooms() {
        return requestedRooms.values().stream().mapToInt(i -> i).sum();
    }
}
