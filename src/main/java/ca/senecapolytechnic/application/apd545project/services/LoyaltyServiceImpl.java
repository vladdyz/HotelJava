package ca.senecapolytechnic.application.apd545project.services;

import ca.senecapolytechnic.application.apd545project.config.LoyaltyPolicy;
import ca.senecapolytechnic.application.apd545project.models.Guest;
import ca.senecapolytechnic.application.apd545project.repositories.GuestRepository;
import com.google.inject.Inject;

import java.util.concurrent.ThreadLocalRandom;

public class LoyaltyServiceImpl implements LoyaltyService {
    private final GuestRepository guestRepo;

    @Inject
    public LoyaltyServiceImpl(GuestRepository guestRepo) {
        this.guestRepo = guestRepo;
    }

    @Override
    public void assignLoyaltyNumber(Guest guest) {
        if (guest.getLoyaltyNumber() == 0) {
            //long loyaltyNo = System.currentTimeMillis();
            int loyaltyNo = ThreadLocalRandom.current().nextInt(100000, 999999);
            // in case the random gen seeds a taken value
            while (guestRepo.findByLoyaltyNumber(loyaltyNo) != null) {
                loyaltyNo = ThreadLocalRandom.current().nextInt(100000, 999999);
            }

            guest.setLoyaltyNumber(loyaltyNo);
            //guestRepo.save(guest);  // BAD!
        }
    }

    @Override
    public void addPoints(Guest guest, double totalPaid) {
        LoyaltyPolicy policy = LoyaltyPolicy.getInstance();
        double rate = policy.getEarningRate();
        int earned = (int) (totalPaid * rate);
        guest.setLoyaltyPoints(guest.getLoyaltyPoints() + earned);
        // guestRepo.save(guest); // BAD!
    }
}

