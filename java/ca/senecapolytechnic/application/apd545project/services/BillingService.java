package ca.senecapolytechnic.application.apd545project.services;

import ca.senecapolytechnic.application.apd545project.models.RoomType;
import ca.senecapolytechnic.application.apd545project.models.ServiceAddon;
import ca.senecapolytechnic.application.apd545project.utils.ReservationObj;

import java.util.Map;

public interface BillingService {
    double computeAddonCost(ReservationObj dto, ServiceAddon addon);
    double calculateRoomSubtotal(Map<RoomType, Integer> requestedRooms, int nights);
}
