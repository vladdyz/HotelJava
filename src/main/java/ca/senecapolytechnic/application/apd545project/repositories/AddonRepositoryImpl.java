package ca.senecapolytechnic.application.apd545project.repositories;
import ca.senecapolytechnic.application.apd545project.AppConfig;
import ca.senecapolytechnic.application.apd545project.models.ServiceAddon;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

public class AddonRepositoryImpl implements AddonRepository{
    @Override
    public void save(ServiceAddon addon) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(addon);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public ServiceAddon findById(Long id) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            return em.find(ServiceAddon.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public ServiceAddon findByName(String name) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT a FROM ServiceAddon a WHERE a.name = :name",
                            ServiceAddon.class
                    ).setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public List<ServiceAddon> findAll() {
        EntityManager em = AppConfig.getEntityManager();
        try {
            return em.createQuery("SELECT a FROM ServiceAddon a", ServiceAddon.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(ServiceAddon addon) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(addon);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Long id) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            ServiceAddon addon = em.find(ServiceAddon.class, id);
            if (addon != null) {
                em.getTransaction().begin();
                em.remove(addon);
                em.getTransaction().commit();
            }
        } finally {
            em.close();
        }
    }
}
