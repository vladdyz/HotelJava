package ca.senecapolytechnic.application.apd545project.config;
import ca.senecapolytechnic.application.apd545project.repositories.*;
import ca.senecapolytechnic.application.apd545project.services.*;
import ca.senecapolytechnic.application.apd545project.utils.GuiceFXMLLoader;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.hibernate.SessionFactory;

import javax.persistence.EntityManager;

public class GuiceModule extends AbstractModule {

    @Override
    protected void configure() {

        /* Wrong: Must not be a singleton! Each transaction will have its own EM and be created per use
        bind(EntityManager.class)
                .toProvider(EntityManagerProvider.class)
                .asEagerSingleton();*/

        // Guice DI
        bind(GuiceFXMLLoader.class).in(Singleton.class);

        // EM
        bind(EntityManager.class).toProvider(EntityManagerProvider.class);

        // Repository bindings
        bind(AddonRepository.class).to(AddonRepositoryImpl.class).in(Singleton.class);
        bind(AdminUserRepository.class).to(AdminUserRepositoryImpl.class).in(Singleton.class);
        bind(AuditLogRepository.class).to(AuditLogRepositoryImpl.class).in(Singleton.class);
        bind(BillingRepository.class).to(BillingRepositoryImpl.class).in(Singleton.class);
        bind(FeedbackRepository.class).to(FeedbackRepositoryImpl.class).in(Singleton.class);
        bind(GuestRepository.class).to(GuestRepositoryImpl.class).in(Singleton.class);
        bind(PaymentRepository.class).to(PaymentRepositoryImpl.class).in(Singleton.class);
        bind(ReservationRepository.class).to(ReservationRepositoryImpl.class).in(Singleton.class);
        bind(RoomRepository.class).to(RoomRepositoryImpl.class).in(Singleton.class);
        bind(WaitlistRepository.class).to(WaitlistRepositoryImpl.class).in(Singleton.class);
        bind(ReservationRoomRepository.class).to(ReservationRoomRepositoryImpl.class).in(Singleton.class);
        bind(ReservationAddonRepository.class).to(ReservationAddonRepositoryImpl.class).in(Singleton.class);


        // Services
        bind(AuthService.class).to(AuthServiceImpl.class).in(Singleton.class);
        bind(ReservationService.class).to(ReservationServiceImpl.class).in(Singleton.class);
        bind(BillingService.class).to(BillingServiceImpl.class).in(Singleton.class);
        bind(LoyaltyService.class).to(LoyaltyServiceImpl.class).in(Singleton.class);


    }
}
