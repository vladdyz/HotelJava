package ca.senecapolytechnic.application.apd545project.repositories;

import ca.senecapolytechnic.application.apd545project.models.RoomType;
import ca.senecapolytechnic.application.apd545project.models.Waitlist;
import ca.senecapolytechnic.application.apd545project.repositories.WaitlistRepository;
import com.google.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class WaitlistRepositoryImpl implements WaitlistRepository {

    private final EntityManager em;

    @Inject
    public WaitlistRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Waitlist save(Waitlist waitlist) {
        em.getTransaction().begin();
        if (waitlist.getId() == null) {
            em.persist(waitlist);
        } else {
            em.merge(waitlist);
        }
        em.getTransaction().commit();
        return waitlist;
    }

    @Override
    public Waitlist findById(Long id) {
        return em.find(Waitlist.class, id);
    }

    @Override
    public List<Waitlist> findAll() {
        TypedQuery<Waitlist> q = em.createQuery("SELECT w FROM Waitlist w", Waitlist.class);
        return q.getResultList();
    }

    @Override
    public List<Waitlist> findByGuestId(Long guestId) {
        TypedQuery<Waitlist> q = em.createQuery(
                "SELECT w FROM Waitlist w WHERE w.guest.id = :guestId",
                Waitlist.class
        );
        q.setParameter("guestId", guestId);
        return q.getResultList();
    }

    @Override
    public List<Waitlist> findByRoomType(RoomType type) {
        TypedQuery<Waitlist> q = em.createQuery(
                "SELECT w FROM Waitlist w WHERE w.requestedType = :type",
                Waitlist.class
        );
        q.setParameter("type", type);
        return q.getResultList();
    }
}