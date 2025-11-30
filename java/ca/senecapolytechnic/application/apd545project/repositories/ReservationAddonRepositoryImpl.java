package ca.senecapolytechnic.application.apd545project.repositories;

import ca.senecapolytechnic.application.apd545project.AppConfig;
import ca.senecapolytechnic.application.apd545project.models.ReservationAddon;

import javax.persistence.EntityManager;
import java.util.List;

// same as ReservationRoom, i gave this bridge its own repository to store a list of reservationaddons
// sepasrately from reservation
public class ReservationAddonRepositoryImpl implements ReservationAddonRepository {

    @Override
    public ReservationAddon save(ReservationAddon ra) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            if (ra.getId() == null) {
                em.persist(ra);
            } else {
                ra = em.merge(ra);
            }
            em.getTransaction().commit();
            return ra;
        } finally {
            em.close();
        }
    }

    @Override
    public List<ReservationAddon> findByReservation(Long reservationId) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT ra FROM ReservationAddon ra WHERE ra.reservation.id = :id",
                            ReservationAddon.class)
                    .setParameter("id", reservationId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(ReservationAddon ra) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            ra = em.merge(ra);
            em.remove(ra);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}