package ca.senecapolytechnic.application.apd545project.repositories;

import ca.senecapolytechnic.application.apd545project.models.Feedback;

import java.util.List;

public interface FeedbackRepository {
    Feedback save(Feedback feedback);
    Feedback findById(Long id);
    List<Feedback> findAll();
    void delete(Long id);
    List<Feedback> findByGuestId(Long guestId);
    List<Feedback> findByReservationId(Long reservationId);
}
