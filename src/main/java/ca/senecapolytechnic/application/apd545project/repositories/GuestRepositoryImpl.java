package ca.senecapolytechnic.application.apd545project.repositories;

import ca.senecapolytechnic.application.apd545project.AppConfig;
import ca.senecapolytechnic.application.apd545project.models.Guest;
import com.google.inject.Inject;

import javax.persistence.EntityManager;
import java.util.List;

public class GuestRepositoryImpl implements GuestRepository{


    @Override
    public Guest save(Guest guest) {
       EntityManager em = AppConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            if (guest.getId() == null) {
                em.persist(guest);
            } else {
                guest = em.merge(guest);
            }
            em.getTransaction().commit();
            return guest;
        } finally {
            em.close();
        }
    }

    @Override
    public Guest findById(Long id) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            return em.find(Guest.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Guest> findAll() {
        EntityManager em = AppConfig.getEntityManager();
        try {
            return em.createQuery("SELECT g FROM Guest g", Guest.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Guest findByPhone(String phone) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            List<Guest> result = em.createQuery(
                            "SELECT g FROM Guest g WHERE g.phone = :phone", Guest.class)
                    .setParameter("phone", phone)
                    .getResultList();

            return result.isEmpty() ? null : result.get(0);
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Guest guest) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            Guest merged = em.merge(guest);
            em.remove(merged);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public Guest findByName(String name) {
        EntityManager em = AppConfig.getEntityManager();
        try {
            List<Guest> result = em.createQuery(
                            "SELECT g FROM Guest g WHERE g.name = :name", Guest.class)
                    .setParameter("name", name)
                    .getResultList();

            return result.isEmpty() ? null : result.get(0);
        } finally {
            em.close();
        }
    }
    @Override
    public Guest findByLoyaltyNumber(Integer loyaltyNumber) {
       EntityManager em = AppConfig.getEntityManager();
        try {
            List<Guest> result = em.createQuery(
                            "SELECT g FROM Guest g WHERE g.loyaltyNumber = :loyaltyNumber", Guest.class)
                    .setParameter("loyaltyNumber", loyaltyNumber)
                    .getResultList();

            return result.isEmpty() ? null : result.get(0);
        } finally {
            em.close();
        }

    }
}
