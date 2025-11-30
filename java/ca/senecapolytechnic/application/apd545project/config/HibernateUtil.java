package ca.senecapolytechnic.application.apd545project.config;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.InputStream;
import java.util.Properties;
// update this to make sure logger logs in a file instead of console

public final class HibernateUtil {
    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    private static SessionFactory sessionFactory;

    private HibernateUtil(){}
    public static synchronized SessionFactory buildSessionFactory(){
        if(sessionFactory != null){
            return sessionFactory;
        }

        StandardServiceRegistry registry = null;
        try{
            //load properties from application.properties if present
            Properties settings = new Properties();
            try(InputStream in = HibernateUtil.class.getClassLoader()
                    .getResourceAsStream("application.properties")){
                if(in!=null){
                    settings.load(in);
                    logger.info("Loaded Hibernate settings from application.properties");
                }else{
                    logger.info("No properties found on class path, using defaultS");
                }
            }catch (Exception e){
                logger.warn("Failed to load properties, using defaults",e);
            }

            //provide safe defaults if properties are missing
            settings.putIfAbsent("hibernate.connection.driver_class", "org.h2.Driver");
            settings.putIfAbsent("hibernate.connection.url", "jdbc:h2:./database/mydb;AUTO_SERVER=TRUE;MODE=MySQL");
            settings.putIfAbsent("hibernate.connection.username", "sa");
            settings.putIfAbsent("hibernate.connection.password", "");
            settings.putIfAbsent("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
            settings.putIfAbsent("hibernate.hbm2ddl.auto", "update");
            settings.putIfAbsent("hibernate.show_sql", "true");
            settings.putIfAbsent("hibernate.format_sql", "true");
            settings.putIfAbsent("hibernate.current_session_context_class", "thread");
            settings.putIfAbsent("hibernate.connection.pool_size", "5");
            settings.putIfAbsent("hibernate.temp.use_jdbc_metadata_defaults", "false");
            settings.putIfAbsent("org.jboss.logging.provider", "slf4j");

            //build registry
            StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();
            registryBuilder.applySettings(settings);
            registry = registryBuilder.build();

            //Register annotate entity classes
            MetadataSources sources = new MetadataSources(registry);
            //Ensure these fully qualified classes match with our project packages
            //sources.addAnnotatedClass(ca.senecacollege.application.wk_11_javafx_with_hibernate.models.Student.class);
            //sources.addAnnotatedClass(ca.senecacollege.application.wk_11_javafx_with_hibernate.models.User.class);

            //Build metadata and SessionFactory
            Metadata metadata = sources.getMetadataBuilder().build();
            sessionFactory = metadata.getSessionFactoryBuilder().build();

            logger.info("Hibernate SessionFactory built Successfully");
            return sessionFactory;
        }catch (Exception e){
            logger.error("Initial SessionFactory creation failed", e);
            if(registry !=null){
                try{
                    StandardServiceRegistryBuilder.destroy(registry);
                }catch (Exception ex){
                    logger.warn("Error while destroying register after failure", ex);
                }
            }
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory(){
        return buildSessionFactory();
    }
    @Provides
    @Singleton // Ensure only one EntityManager instance per request or thread, if appropriate
    public EntityManager provideEntityManager(EntityManagerFactory emf) {
        return emf.createEntityManager();
    }
    public static void  shutdown(){
        if(sessionFactory != null){
            try{
                sessionFactory.close();
                logger.info("hibernate SessionFactory closed");
            }catch (Exception ex){
                logger.warn("hibernate failure on closing the sessionfactory",ex);
            }
            finally {
                sessionFactory = null;
            }
        }
    }
}
