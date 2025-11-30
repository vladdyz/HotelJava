package ca.senecapolytechnic.application.apd545project.services;

import ca.senecapolytechnic.application.apd545project.models.Reservation;
import ca.senecapolytechnic.application.apd545project.utils.ReservationObj;

public interface ReservationService {
    Reservation createReservation(ReservationObj request) throws RuntimeException;
    int lookupGuestLoyaltyByPhone(String phone);
}
