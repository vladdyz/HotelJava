package ca.senecapolytechnic.application.apd545project.repositories;

import ca.senecapolytechnic.application.apd545project.AppConfig;
import ca.senecapolytechnic.application.apd545project.models.Reservation;
import ca.senecapolytechnic.application.apd545project.models.ReservationStatus;

import javax.persistence.EntityManager;
import java.util.List;

public class ReservationRepositoryImpl implements ReservationRepository {
    @Override
    public Reservation save(Reservation reservation) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            if (reservation.getId() == null) {
                em.persist(reservation);
            } else {
                reservation = em.merge(reservation);
            }
            em.getTransaction().commit();
            return reservation;
        } finally {
            em.close();
        }
    }

    @Override
    public Reservation findById(Long id) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            return em.find(Reservation.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Reservation> findAll() {
        EntityManager em = AppConfig.getEntityManager();
        try {
            return em.createQuery("SELECT r FROM Reservation r", Reservation.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Reservation> findByStatus(ReservationStatus status) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT r FROM Reservation r WHERE r.reservationStatus = :status",
                            Reservation.class
                    )
                    .setParameter("status", status)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Reservation reservation) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            Reservation merged = em.merge(reservation);
            em.remove(merged);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
