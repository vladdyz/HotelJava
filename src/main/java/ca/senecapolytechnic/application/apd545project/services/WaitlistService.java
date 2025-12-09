package ca.senecapolytechnic.application.apd545project.services;

import ca.senecapolytechnic.application.apd545project.models.Reservation;
import ca.senecapolytechnic.application.apd545project.models.RoomType;
import ca.senecapolytechnic.application.apd545project.models.Waitlist;

import java.time.LocalDateTime;
import java.util.List;

public interface WaitlistService {
    boolean isRoomTypeAvailableForRange(RoomType type, LocalDateTime start, LocalDateTime end);
    List<Waitlist> findMatchingWaitlistEntries();
    void markNotified(Waitlist w);
    void convertWaitlistToReservation(Waitlist w);
}
