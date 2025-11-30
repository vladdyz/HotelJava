package ca.senecapolytechnic.application.apd545project.config;


import com.google.inject.Provider;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerProvider implements Provider<EntityManager> {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("hotelPU");
    // Make sure this matches persistence.xml

    @Override
    public EntityManager get() {
        return emf.createEntityManager();
    }
}
