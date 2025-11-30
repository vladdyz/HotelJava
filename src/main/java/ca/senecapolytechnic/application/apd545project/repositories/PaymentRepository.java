package ca.senecapolytechnic.application.apd545project.repositories;

import ca.senecapolytechnic.application.apd545project.models.Payment;

import java.util.List;

public interface PaymentRepository {
    Payment save(Payment payment);
    Payment findById(Long id);
    Payment findByBillingId(Long billingId);
    List<Payment> findAll();
    void delete(Long id);
}
