package ca.senecapolytechnic.application.apd545project.repositories;

import ca.senecapolytechnic.application.apd545project.AppConfig;
import ca.senecapolytechnic.application.apd545project.models.Reservation;
import ca.senecapolytechnic.application.apd545project.models.ReservationStatus;
import com.google.inject.Inject;


import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Override
    public Reservation findRyGuestCheckout(String phone, LocalDate checkoutDate) {
        LocalDateTime start = checkoutDate.atStartOfDay(); //need to convert (LocalDate v LocalDateTime)...
        EntityManager em = AppConfig.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT r FROM Reservation r WHERE r.guest.phone = :phone AND r.checkOut = :checkout",
                    Reservation.class
            ).setParameter("phone", phone)
            .setParameter("checkout", start).getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Reservation> findByGuestId(Long guestId) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT r FROM Reservation r WHERE r.guest.id = :gid",
                            Reservation.class
                    )
                    .setParameter("gid", guestId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
