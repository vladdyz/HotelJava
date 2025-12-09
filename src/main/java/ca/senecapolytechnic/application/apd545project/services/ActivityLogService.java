package ca.senecapolytechnic.application.apd545project.services;

import ca.senecapolytechnic.application.apd545project.models.AuditLog;

import java.util.List;

public interface ActivityLogService {
    void log(String actor, String action, String entityType, int entityId, String message);
    List<AuditLog> getLogs();
}
