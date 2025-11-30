package ca.senecapolytechnic.application.apd545project;

import ca.senecapolytechnic.application.apd545project.controllers.AdminController;
import ca.senecapolytechnic.application.apd545project.models.*;
import ca.senecapolytechnic.application.apd545project.security.BCryptPasswordHasher;
import ca.senecapolytechnic.application.apd545project.utils.LoggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class AppConfig {
    private static Logger Logger = LoggerFactory.getLogger(AppConfig.class);
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("hotelPU");

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    static {
        LoggerService.configure(); // for logging
        initializeDatabase();

    }


    private static void initializeDatabase() {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            // check if an admin user exists
            long count = em.createQuery(
                    "SELECT COUNT(a) FROM AdminUser a", Long.class
            ).getSingleResult();

            if (count == 0) {
                AdminUser admin = new AdminUser(
                        "admin",
                        BCryptPasswordHasher.hash("password"),
                        Role.ADMIN,
                        false
                );
                em.persist(admin);
                em.flush(); // force an INSERT now so we can read generated id
                Logger.info("Database pre-population successful, admin id = {}, username = {}, passwordHash = {}",
                        admin.getId(), admin.getUsername(), admin.getPasswordHash());
            }
            // log out all admins when an app is relaunched
            em.createQuery("UPDATE AdminUser a SET a.active = false")
                    .executeUpdate();

            // room and hotel initializations, add only if these are empty
            // dont want to repopulate with duplicates!!
            long hotelCount = em.createQuery(
                    "SELECT COUNT(h) FROM Hotel h", Long.class
            ).getSingleResult();

            if (hotelCount == 0) {
                Hotel hotel = new Hotel(null, "Come On Inn", "Toronto");
                em.persist(hotel);
                Logger.info("Inserted initial hotel: {}", hotel);
            } else {
                Logger.info("Hotel table already populated, skipping initialization.");
            }
            // rooms have a lot of records to populate, adding a new function for this
            initializeRooms(em);

            // services
            long addonCount = em.createQuery(
                    "SELECT COUNT(s) FROM ServiceAddon s", Long.class
            ).getSingleResult();

            if (addonCount == 0) {
                Logger.info("No Services found. Pre-populating ServiceAddon table...");

                ServiceAddon wifi = new ServiceAddon("Wi-Fi", 10.0, PricingModel.PER_NIGHT);
                ServiceAddon breakfast = new ServiceAddon("Breakfast", 5.0, PricingModel.PER_NIGHT);
                ServiceAddon parking = new ServiceAddon("Parking", 20.0, PricingModel.PER_NIGHT);
                ServiceAddon spaAccess = new ServiceAddon("Spa Access", 40.0, PricingModel.PER_RESERVATION);

                em.persist(wifi);
                em.persist(breakfast);
                em.persist(parking);
                em.persist(spaAccess);

                Logger.info("ServiceAddons pre-populated successfully (Wi-Fi, Breakfast, Parking, Spa Access)");
            } else {
                Logger.info("ServiceAddons table already populated, skipping initialization.");
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private static void initializeRooms(EntityManager em) {
        long roomCount = em.createQuery(
                "SELECT COUNT(r) FROM Room r", Long.class
        ).getSingleResult();

        if (roomCount > 0) {
            Logger.info("Rooms table already populated, skipping room initialization.");
            return;
        }

        Logger.info("Rooms table is empty. Inserting initial room data...");

        // Helper lambda to persist rooms cleanly
        java.util.function.Consumer<Room> addRoom = room -> {
            em.persist(room);
            Logger.debug("Inserted room {}", room);
        };

        // Single Rooms - $50 per night, 1 bed, max. occupancy 2 people
        // Double Rooms - $50 per night, 2 beds, max. occupancy 4 people
        // Deluxe Rooms - $100 per night, 1 bed, max. occupancy 2 people
        // Penthouse Rooms - $400 per night, 1 bed, max. occupancy 2 people
        // Hotel has 3 floors, 12 rooms per floor. 3rd floor is the roof with 2 penthouse suites.
        // doing this in loops because its a lot of repetition

        // single
        int[] singles = {101,102,103,104,105, 201,202,203,204,205};
        for (int num : singles) {
            addRoom.accept(new Room(num, RoomType.SINGLE, 1, 50.0, RoomStatus.AVAILABLE));
        }

        // double
        int[] doubles = {106,107,108,109, 206,207,208,209};
        for (int num : doubles) {
            addRoom.accept(new Room(num, RoomType.DOUBLE, 2, 50.0, RoomStatus.AVAILABLE));
        }

        // deluxe
        int[] deluxe = {110,111,112, 210,211,212};
        for (int num : deluxe) {
            addRoom.accept(new Room(num, RoomType.DELUXE, 1, 100.0, RoomStatus.AVAILABLE));
        }

        // penthouse
        int[] penthouse = {301,302};
        for (int num : penthouse) {
            addRoom.accept(new Room(num, RoomType.PENTHOUSE, 1, 400.0, RoomStatus.AVAILABLE));
        }

        Logger.info("Room initialization complete.");
    }
}
