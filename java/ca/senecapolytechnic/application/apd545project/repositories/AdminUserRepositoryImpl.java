package ca.senecapolytechnic.application.apd545project.repositories;

import ca.senecapolytechnic.application.apd545project.models.AdminUser;
import com.google.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

public class AdminUserRepositoryImpl implements AdminUserRepository{

    private final EntityManager em;


    @Inject
    public AdminUserRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    public AdminUser findByUsername(String username) {
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
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
    }

    public void update(AdminUser user) {
        em.getTransaction().begin();
        em.merge(user);
        em.getTransaction().commit();
    }
}