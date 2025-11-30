package ca.senecapolytechnic.application.apd545project.repositories;

// same as ReservationRoom, i gave this bridge its own repository to store a list of reservationaddons
// sepasrately from reservation

import ca.senecapolytechnic.application.apd545project.models.ReservationAddon;

import java.util.List;

public interface ReservationAddonRepository {
    ReservationAddon save(ReservationAddon ra);
    List<ReservationAddon> findByReservation(Long reservationId);
    void delete(ReservationAddon ra);
}
