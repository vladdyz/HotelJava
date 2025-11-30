package ca.senecapolytechnic.application.apd545project.repositories;

import ca.senecapolytechnic.application.apd545project.models.AuditLog;
import ca.senecapolytechnic.application.apd545project.repositories.AuditLogRepository;
import com.google.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class AuditLogRepositoryImpl implements AuditLogRepository {

    private final EntityManager em;

    @Inject
    public AuditLogRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public AuditLog save(AuditLog log) {
        em.getTransaction().begin();
        if (log.getId() == null) {
            em.persist(log);
        } else {
            em.merge(log);
        }
        em.getTransaction().commit();
        return log;
    }

    @Override
    public AuditLog findById(Long id) {
        return em.find(AuditLog.class, id);
    }

    @Override
    public List<AuditLog> findAll() {
        TypedQuery<AuditLog> q = em.createQuery("SELECT a FROM AuditLog a", AuditLog.class);
        return q.getResultList();
    }

    @Override
    public List<AuditLog> findByActor(String actor) {
        TypedQuery<AuditLog> q = em.createQuery(
                "SELECT a FROM AuditLog a WHERE a.actor = :actor",
                AuditLog.class
        );
        q.setParameter("actor", actor);
        return q.getResultList();
    }

    @Override
    public List<AuditLog> findByEntity(String entityType, int entityId) {
        TypedQuery<AuditLog> q = em.createQuery(
                "SELECT a FROM AuditLog a WHERE a.entityType = :type AND a.entityId = :id",
                AuditLog.class
        );
        q.setParameter("type", entityType);
        q.setParameter("id", entityId);
        return q.getResultList();
    }
}