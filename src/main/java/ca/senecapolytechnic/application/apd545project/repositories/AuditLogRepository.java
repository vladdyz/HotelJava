package ca.senecapolytechnic.application.apd545project.repositories;

import ca.senecapolytechnic.application.apd545project.models.AuditLog;

import java.util.List;

public interface AuditLogRepository {
    AuditLog save(AuditLog log);
    AuditLog findById(Long id);
    List<AuditLog> findAll();
    List<AuditLog> findByActor(String actor);
    List<AuditLog> findByEntity(String entityType, int entityId);
}
