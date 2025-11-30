package ca.senecapolytechnic.application.apd545project.services;

import ca.senecapolytechnic.application.apd545project.models.RoomType;
import ca.senecapolytechnic.application.apd545project.models.ServiceAddon;
import ca.senecapolytechnic.application.apd545project.utils.ReservationObj;

import java.util.Map;

public class BillingServiceImpl implements BillingService {
    public double computeAddonCost(ReservationObj dto, ServiceAddon addon) {

        double base = addon.getPrice();

        switch (addon.getPricingModel()) {

            case PER_NIGHT:
                return base * dto.totalNights();

            case PER_RESERVATION:
                return base;

            default:
                throw new IllegalArgumentException("Unknown pricing model");
        }
    }

    public double calculateRoomSubtotal(Map<RoomType, Integer> requestedRooms, int nights) {
        double total = 0.0;

        for (Map.Entry<RoomType, Integer> entry : requestedRooms.entrySet()) {
            RoomType type = entry.getKey();
            int qty = entry.getValue();

            if (qty > 0) {
                double pricePerNight = 0;
                switch (type) {
                    case SINGLE:
                    case DOUBLE:
                        pricePerNight = 50.0;
                        break;
                    case DELUXE:
                        pricePerNight = 100.0;
                        break;
                    case PENTHOUSE:
                        pricePerNight = 400.0;
                        break;
                };

                total += qty * pricePerNight * nights;
            }
        }

        return total;
    }
}
