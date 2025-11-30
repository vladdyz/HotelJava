package ca.senecapolytechnic.application.apd545project.services;

import ca.senecapolytechnic.application.apd545project.models.Guest;

public interface LoyaltyService {
    void assignLoyaltyNumber(Guest guest);
    void addPoints(Guest guest, double totalPaid);
}
