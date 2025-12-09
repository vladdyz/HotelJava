package ca.senecapolytechnic.application.apd545project.repositories;

import ca.senecapolytechnic.application.apd545project.AppConfig;
import ca.senecapolytechnic.application.apd545project.models.RoomType;
import ca.senecapolytechnic.application.apd545project.models.Waitlist;
import ca.senecapolytechnic.application.apd545project.repositories.WaitlistRepository;
import com.google.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class WaitlistRepositoryImpl implements WaitlistRepository {


    @Override
    public Waitlist save(Waitlist waitlist) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            if (waitlist.getId() == null) {
                em.persist(waitlist);
            } else {
                em.merge(waitlist);
            }
            em.getTransaction().commit();
            return waitlist;
        } finally {
            em.close();
        }
    }

    @Override
    public Waitlist findById(Long id) {
        EntityManager em = AppConfig.getEntityManager();
        return em.find(Waitlist.class, id);
    }

    @Override
    public List<Waitlist> findAll() {
        EntityManager em = AppConfig.getEntityManager();
        try {
            TypedQuery<Waitlist> q = em.createQuery("SELECT w FROM Waitlist w", Waitlist.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Waitlist> findByGuestId(Long guestId) {
        EntityManager em = AppConfig.getEntityManager();
        TypedQuery<Waitlist> q = em.createQuery(
                "SELECT w FROM Waitlist w WHERE w.guest.id = :guestId",
                Waitlist.class
        );
        q.setParameter("guestId", guestId);
        return q.getResultList();
    }

    @Override
    public List<Waitlist> findByRoomType(RoomType type) {
        EntityManager em = AppConfig.getEntityManager();
        TypedQuery<Waitlist> q = em.createQuery(
                "SELECT w FROM Waitlist w WHERE w.requestedType = :type",
                Waitlist.class
        );
        q.setParameter("type", type);
        return q.getResultList();
    }

    @Override
    public List<Waitlist> findByStatus(String status) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            TypedQuery<Waitlist> q = em.createQuery("SELECT w FROM Waitlist w WHERE w.status = :s", Waitlist.class);
            q.setParameter("s", status);
            return q.getResultList();
        } finally { em.close(); }
    }
    @Override
    public void delete(Long id) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            Waitlist w = em.find(Waitlist.class, id);
            if (w != null) em.remove(w);
            em.getTransaction().commit();
        } finally { em.close(); }
    }
}