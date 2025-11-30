package ca.senecapolytechnic.application.apd545project.repositories;

// this is a bridge table but it needs its own repository, after consideration i decided to separate it from Reservation

import ca.senecapolytechnic.application.apd545project.models.ReservationRoom;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRoomRepository {
    ReservationRoom save(ReservationRoom rr);
    List<ReservationRoom> findByReservation(Long reservationId);
    void delete(ReservationRoom rr);
    boolean existsOverlapForRoom(Long roomId, LocalDate from, LocalDate to);
}