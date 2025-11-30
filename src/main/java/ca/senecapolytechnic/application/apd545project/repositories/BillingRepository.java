package ca.senecapolytechnic.application.apd545project.repositories;

import ca.senecapolytechnic.application.apd545project.models.Billing;

import java.util.List;

public interface BillingRepository {
    Billing save(Billing billing);
    Billing findById(Long id);
    Billing findByReservationId(Long reservationId);
    List<Billing> findAll();
    void delete(Long id);
}
