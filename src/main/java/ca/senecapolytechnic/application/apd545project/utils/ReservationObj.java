package ca.senecapolytechnic.application.apd545project.utils;

import ca.senecapolytechnic.application.apd545project.models.RoomType;

import java.time.LocalDate;
import java.util.Map;

public class ReservationObj {
    public LocalDate checkIn;
    public LocalDate checkOut;
    public int numAdults;
    public int numChildren;

    // mapped
    public Map<RoomType, Integer> requestedRooms;

    public String title;
    public String firstName;
    public String lastName;
    public String phone;
    public String email;
    public String address;

    public Map<Long, Integer> addons;

    // need to do this for billing
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
