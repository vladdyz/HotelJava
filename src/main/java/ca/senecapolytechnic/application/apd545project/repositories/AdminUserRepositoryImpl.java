package ca.senecapolytechnic.application.apd545project.repositories;

import ca.senecapolytechnic.application.apd545project.AppConfig;
import ca.senecapolytechnic.application.apd545project.models.AdminUser;
import com.google.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

public class AdminUserRepositoryImpl implements AdminUserRepository{


    public AdminUser findByUsername(String username) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            TypedQuery<AdminUser> q = em.createQuery(
                    "SELECT a FROM AdminUser a WHERE a.username = :u",
                    AdminUser.class
            );
            q.setParameter("u", username);
            return q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    public void save(AdminUser user) {
        EntityManager em = AppConfig.getEntityManager();
        try {
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void update(AdminUser user) {
        EntityManager em = AppConfig.getEntityManager();
        em.getTransaction().begin();
        em.merge(user);
        em.getTransaction().commit();
    }
}