package ca.senecapolytechnic.application.apd545project.repositories;

import ca.senecapolytechnic.application.apd545project.AppConfig;
import ca.senecapolytechnic.application.apd545project.models.Feedback;
import ca.senecapolytechnic.application.apd545project.repositories.FeedbackRepository;
import com.google.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class FeedbackRepositoryImpl implements FeedbackRepository {


    @Override
    public Feedback save(Feedback feedback) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            if (feedback.getId() == null) {
                em.persist(feedback);
            } else {
                em.merge(feedback);
            }
            em.getTransaction().commit();
            return feedback;
        } finally {
            em.close();
        }

    }

    @Override
    public Feedback findById(Long id) {
        EntityManager em = AppConfig.getEntityManager();
        return em.find(Feedback.class, id);
    }

    @Override
    public List<Feedback> findAll() {
        EntityManager em = AppConfig.getEntityManager();
        TypedQuery<Feedback> query = em.createQuery("SELECT f FROM Feedback f", Feedback.class);
        return query.getResultList();
    }

    @Override
    public void delete(Long id) {
        EntityManager em = AppConfig.getEntityManager();
        Feedback feedback = findById(id);
        if (feedback != null) {
            em.getTransaction().begin();
            em.remove(feedback);
            em.getTransaction().commit();
        }
    }

    @Override
    public List<Feedback> findByGuestId(Long guestId) {
        EntityManager em = AppConfig.getEntityManager();
        TypedQuery<Feedback> query = em.createQuery(
                "SELECT f FROM Feedback f WHERE f.guest.id = :guestId",
                Feedback.class
        );
        query.setParameter("guestId", guestId);
        return query.getResultList();
    }

    @Override
    public List<Feedback> findByReservationId(Long reservationId) {
        EntityManager em = AppConfig.getEntityManager();
        TypedQuery<Feedback> query = em.createQuery(
                "SELECT f FROM Feedback f WHERE f.reservation.id = :reservationId",
                Feedback.class
        );
        query.setParameter("reservationId", reservationId);
        return query.getResultList();
    }
}