package ca.senecapolytechnic.application.apd545project.services;

import ca.senecapolytechnic.application.apd545project.models.AuditLog;
import ca.senecapolytechnic.application.apd545project.repositories.AuditLogRepository;
import com.google.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;

/*
 *  What actually gets tracked?
 * - Admin Authentication (logging in, logging out)
 * - Confirming a payment (pts/cash/card)
 * - Cancelling/Modifying a reservation
 * - Applying a discount to a billing
 * - Updating room availability
 * - Changing the loyalty points earning rate
 * - Changing the loyalty points redemption cap
 * - Cancelling the loyalty membership of a guest
 * - Activating the loyalty membership of a guest
 * -
 */

public class ActivityLogServiceImpl implements ActivityLogService {
    private final AuditLogRepository auditLogRepository;

    @Inject
    public ActivityLogServiceImpl(AuditLogRepository auditLogRepo) {
        this.auditLogRepository = auditLogRepo;
    }
    @Override
    public void log(String actor, String action, String entityType, int entityId, String message) {
        auditLogRepository.save(
                new AuditLog(LocalDateTime.now(), actor, action, entityType, entityId, message)
        );
    }
    @Override
    public List<AuditLog> getLogs() {
        return auditLogRepository.findAll();
    }
}

