package ca.senecapolytechnic.application.apd545project.repositories;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.util.List;
import ca.senecapolytechnic.application.apd545project.AppConfig;
import ca.senecapolytechnic.application.apd545project.models.Room;
import ca.senecapolytechnic.application.apd545project.models.RoomStatus;
import ca.senecapolytechnic.application.apd545project.models.RoomType;
import com.google.inject.Inject;

public class RoomRepositoryImpl implements RoomRepository{



    @Override
    public void save(Room room) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(room);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public Room findById(Long id) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            return em.find(Room.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public Room findByRoomNumber(int roomNumber) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT r FROM Room r WHERE r.roomNumber = :roomNumber",
                            Room.class
                    ).setParameter("roomNumber", roomNumber)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Room> findAll() {
       EntityManager em = AppConfig.getEntityManager();
        try {
            return em.createQuery("SELECT r FROM Room r", Room.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Room room) {
       EntityManager em = AppConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(room);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Long id) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            Room room = em.find(Room.class, id);
            if (room != null) {
                em.getTransaction().begin();
                em.remove(room);
                em.getTransaction().commit();
            }
        } finally {
            em.close();
        }
    }
    @Override
    public Room findByRoomType(RoomType roomType) {
      EntityManager em = AppConfig.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT r FROM Room r WHERE r.roomType = :roomType",
                            Room.class
                    ).setParameter("roomType", roomType)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        } finally {
            em.close();
        }
    }
    public List<Room> findAvailable(RoomType type, LocalDate from, LocalDate to) {
      EntityManager em = AppConfig.getEntityManager();
        String hql = "select r from Room r where r.roomType = :type and r.id not in (" +
                "select rr.room.id from ReservationRoom rr " +
                "where not (rr.reservation.checkOut <= :from or rr.reservation.checkIn >= :to)" +
                ")";
        return em.createQuery(hql, Room.class)
                .setParameter("type", type)
                .setParameter("from", from.atStartOfDay())
                .setParameter("to", to.atStartOfDay())
                .getResultList();
    }

    @Override
    public int countAvailableRooms(RoomType type) {
       EntityManager em = AppConfig.getEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(r) FROM Room r WHERE r.roomType = :t AND r.roomStatus = :status"
                            ,
                            Long.class
                    )
                    .setParameter("t", type)
                    .setParameter("status", RoomStatus.AVAILABLE)
                    .getSingleResult();

            return count.intValue();
        } finally {
            em.close();
        }
    }
}
