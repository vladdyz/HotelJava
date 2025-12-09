package ca.senecapolytechnic.application.apd545project.repositories;


import ca.senecapolytechnic.application.apd545project.AppConfig;
import ca.senecapolytechnic.application.apd545project.models.Payment;
import ca.senecapolytechnic.application.apd545project.repositories.PaymentRepository;
import com.google.inject.Inject;


import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class PaymentRepositoryImpl implements PaymentRepository {



    @Override
    public Payment save(Payment payment) {
        EntityManager em = AppConfig.getEntityManager();
        try {

            em.getTransaction().begin();

            if (payment.getId() == null) {
                em.persist(payment);
            } else {
                payment = em.merge(payment);
            }
            em.getTransaction().commit();
            return payment;
        } finally {
            em.close();
        }

    }

    @Override
    public Payment findById(Long id) {
        EntityManager em = AppConfig.getEntityManager();

        return em.find(Payment.class, id);
    }

    @Override
    public List<Payment> findByBillingId(Long billingId) {
        EntityManager em = AppConfig.getEntityManager();
        TypedQuery<Payment> q = em.createQuery(
                "SELECT p FROM Payment p WHERE p.billing.id = :billingId",
                Payment.class
        );
        q.setParameter("billingId", billingId);

        List<Payment> results = q.getResultList();
        return results; // dont want to crash if no payments found
    }

    @Override
    public List<Payment> findAll() {
        EntityManager em = AppConfig.getEntityManager();
        return em.createQuery("SELECT p FROM Payment p", Payment.class)
                .getResultList();
    }

    @Override
    public void delete(Long id) {
        EntityManager em = AppConfig.getEntityManager();
        Payment payment = findById(id);
        if (payment != null) {
            em.remove(payment);
        }
    }
}