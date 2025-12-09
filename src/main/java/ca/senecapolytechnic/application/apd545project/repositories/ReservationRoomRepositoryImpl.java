package ca.senecapolytechnic.application.apd545project.repositories;

import ca.senecapolytechnic.application.apd545project.AppConfig;
import ca.senecapolytechnic.application.apd545project.models.ReservationRoom;
import com.google.inject.Inject;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

// this is a bridge table but it needs its own repository, after consideration i decided to separate it from Reservation

public class ReservationRoomRepositoryImpl implements ReservationRoomRepository {




    @Override
    public ReservationRoom save(ReservationRoom rr) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            if (rr.getId() == null) {
                em.persist(rr);
            } else {
                rr = em.merge(rr);
            }
            em.getTransaction().commit();
            return rr;
        } finally {
            em.close();
        }
    }

    @Override
    public List<ReservationRoom> findByReservation(Long reservationId) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT rr FROM ReservationRoom rr WHERE rr.reservation.id = :id",
                            ReservationRoom.class)
                    .setParameter("id", reservationId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(ReservationRoom rr) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            rr = em.merge(rr);
            em.remove(rr);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    // need to check if room is occupied for a given date in admin ui
    @Override
    public boolean existsOverlapForRoom(Long roomId, LocalDate from, LocalDate to) {
       EntityManager em = AppConfig.getEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(rr) FROM ReservationRoom rr " +
                                    "WHERE rr.room.id = :roomId " +
                                    "AND NOT (rr.reservation.checkOut <= :from OR rr.reservation.checkIn >= :to)",
                            Long.class)
                    .setParameter("roomId", roomId)
                    .setParameter("from", from.atStartOfDay())
                    .setParameter("to", to.atStartOfDay())
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
}