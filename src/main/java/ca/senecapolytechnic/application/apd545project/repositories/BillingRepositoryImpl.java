package ca.senecapolytechnic.application.apd545project.repositories;
import ca.senecapolytechnic.application.apd545project.AppConfig;
import ca.senecapolytechnic.application.apd545project.models.Billing;
import ca.senecapolytechnic.application.apd545project.repositories.BillingRepository;
import com.google.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class BillingRepositoryImpl implements BillingRepository {


    @Override
    public Billing save(Billing billing) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            if (billing.getId() == null) {
                em.persist(billing);
            } else {
                billing = em.merge(billing);
            }
            em.getTransaction().commit();
            return billing;
        } finally {
            em.close();
        }

    }

    @Override
    public Billing findById(Long id) {
        EntityManager em = AppConfig.getEntityManager();
        return em.find(Billing.class, id);
    }

    @Override
    public Billing findByReservationId(Long reservationId) {
        EntityManager em = AppConfig.getEntityManager();
        TypedQuery<Billing> q = em.createQuery(
                "SELECT b FROM Billing b WHERE b.reservation.id = :reservationId",
                Billing.class
        );
        q.setParameter("reservationId", reservationId);

        List<Billing> results = q.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<Billing> findAll() {
        EntityManager em = AppConfig.getEntityManager();
        return em.createQuery("SELECT b FROM Billing b", Billing.class)
                .getResultList();
    }

    @Override
    public void delete(Long id) {
        EntityManager em = AppConfig.getEntityManager();
        Billing billing = findById(id);
        if (billing != null) {
            em.remove(billing);
        }
    }
}