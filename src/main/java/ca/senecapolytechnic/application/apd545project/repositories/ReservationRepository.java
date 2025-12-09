package ca.senecapolytechnic.application.apd545project.repositories;

import ca.senecapolytechnic.application.apd545project.models.Reservation;
import ca.senecapolytechnic.application.apd545project.models.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository {
    Reservation save(Reservation reservation);
    Reservation findById(Long id);
    List<Reservation> findAll();
    List<Reservation> findByStatus(ReservationStatus status);
    void delete(Reservation reservation);
    // for the feedback
    Reservation findRyGuestCheckout(String phone, LocalDate checkoutDate);
    List<Reservation> findByGuestId(Long guestId);
}
