package ca.senecapolytechnic.application.apd545project.repositories;


import ca.senecapolytechnic.application.apd545project.models.Payment;
import ca.senecapolytechnic.application.apd545project.repositories.PaymentRepository;
import com.google.inject.Inject;


import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class PaymentRepositoryImpl implements PaymentRepository {

    private final EntityManager em;

    @Inject
    public PaymentRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Payment save(Payment payment) {
        if (payment.getId() == null) {
            em.persist(payment);
        } else {
            payment = em.merge(payment);
        }
        return payment;
    }

    @Override
    public Payment findById(Long id) {
        return em.find(Payment.class, id);
    }

    @Override
    public Payment findByBillingId(Long billingId) {
        TypedQuery<Payment> q = em.createQuery(
                "SELECT p FROM Payment p WHERE p.billing.id = :billingId",
                Payment.class
        );
        q.setParameter("billingId", billingId);

        List<Payment> results = q.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<Payment> findAll() {
        return em.createQuery("SELECT p FROM Payment p", Payment.class)
                .getResultList();
    }

    @Override
    public void delete(Long id) {
        Payment payment = findById(id);
        if (payment != null) {
            em.remove(payment);
        }
    }
}