package ca.senecapolytechnic.application.apd545project.repositories;

import ca.senecapolytechnic.application.apd545project.AppConfig;
import ca.senecapolytechnic.application.apd545project.models.AuditLog;
import ca.senecapolytechnic.application.apd545project.repositories.AuditLogRepository;
import com.google.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class AuditLogRepositoryImpl implements AuditLogRepository {


    @Override
    public AuditLog save(AuditLog log) {
        EntityManager em = AppConfig.getEntityManager();
        try {
        em.getTransaction().begin();
        if (log.getId() == null) {
            em.persist(log);
        } else {
            em.merge(log);
        }
        em.getTransaction().commit();
        return log;
        } finally {
            em.close();
        }
    }

    @Override
    public AuditLog findById(Long id) {
        EntityManager em = AppConfig.getEntityManager();
        return em.find(AuditLog.class, id);
    }

    @Override
    public List<AuditLog> findAll() {
        EntityManager em = AppConfig.getEntityManager();
        TypedQuery<AuditLog> q = em.createQuery("SELECT a FROM AuditLog a", AuditLog.class);
        return q.getResultList();
    }

    @Override
    public List<AuditLog> findByActor(String actor) {
        EntityManager em = AppConfig.getEntityManager();
        TypedQuery<AuditLog> q = em.createQuery(
                "SELECT a FROM AuditLog a WHERE a.actor = :actor",
                AuditLog.class
        );
        q.setParameter("actor", actor);
        return q.getResultList();
    }

    @Override
    public List<AuditLog> findByEntity(String entityType, int entityId) {
        EntityManager em = AppConfig.getEntityManager();
        TypedQuery<AuditLog> q = em.createQuery(
                "SELECT a FROM AuditLog a WHERE a.entityType = :type AND a.entityId = :id",
                AuditLog.class
        );
        q.setParameter("type", entityType);
        q.setParameter("id", entityId);
        return q.getResultList();
    }
}