package ca.senecapolytechnic.application.apd545project.services;

import ca.senecapolytechnic.application.apd545project.models.Billing;
import ca.senecapolytechnic.application.apd545project.models.Guest;
import ca.senecapolytechnic.application.apd545project.models.Reservation;

public interface ReportingService {
    void setSelectedReservation(Reservation reservation);
    Reservation getSelectedReservation();
    void setSelectedBilling(Billing billing);
    Billing getSelectedBilling();
    void clearSelection();
    void setSelectedGuest(Guest guest);
    Guest getSelectedGuest();


}
