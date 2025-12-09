package ca.senecapolytechnic.application.apd545project.services;

import ca.senecapolytechnic.application.apd545project.models.Billing;
import ca.senecapolytechnic.application.apd545project.models.Guest;
import ca.senecapolytechnic.application.apd545project.models.Reservation;

public class ReportingServiceImpl implements ReportingService{
    private Reservation selectedReservation;
    private Billing selectedBilling;
    private Guest selectedGuest;

    public void setSelectedReservation(Reservation reservation) {
        this.selectedReservation = reservation;
    }

    public Reservation getSelectedReservation() {
        return selectedReservation;
    }

    public void setSelectedBilling(Billing billing) {
        this.selectedBilling = billing;
    }

    public Billing getSelectedBilling() {
        return selectedBilling;
    }

    public void clearSelection() {
        selectedReservation = null;
        selectedBilling = null;
        selectedGuest = null;
    }
    public void setSelectedGuest(Guest guest) {
        this.selectedGuest = guest;
    }
    public Guest getSelectedGuest() {
        return selectedGuest;
    }
}
