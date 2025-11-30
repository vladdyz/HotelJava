package ca.senecapolytechnic.application.apd545project.utils;

import ca.senecapolytechnic.application.apd545project.models.RoomType;
import ca.senecapolytechnic.application.apd545project.repositories.RoomRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

// this mostly just checks that any non-nullable fields should be filled out

public class Validator {
    private Validator() {}
    public static void requireNotNull(Object o, String message) {
        if (o == null) throw new IllegalArgumentException(message);
    }

    public static void requirePositive(int n, String message) {
        if (n < 0) throw new IllegalArgumentException(message);
    }

    public static void validateDates(LocalDate checkIn, LocalDate checkOut) {
        requireNotNull(checkIn, "Check-in required");
        requireNotNull(checkOut, "Check-out required");
        if (!checkOut.isAfter(checkIn)) throw new IllegalArgumentException("Check-out must be after check-in");
    }

    public static void validateRoomAvailability(
            Map<RoomType, Integer> requestedRooms,
            RoomRepository roomRepo
    ) {
        for (Map.Entry<RoomType, Integer> entry : requestedRooms.entrySet()) {
            RoomType type = entry.getKey();
            int requested = entry.getValue();

            if (requested <= 0) continue;

            // how many avaiable rooms exist for this type
            int available = roomRepo.countAvailableRooms(type);

            if (requested > available) {
                throw new IllegalArgumentException(
                        "Not enough " + type + " rooms available. " +
                                "Requested: " + requested + ", Available: " + available
                );
            }
        }
    }

    public static void validateRoomCapacity(
            int adults,
            int children,
            Map<RoomType, Integer> requestedRooms
    ) {
        int totalPeople = adults + children;

        int totalCapacity =
                requestedRooms.get(RoomType.SINGLE)    * 2 +
                        requestedRooms.get(RoomType.DOUBLE)    * 4 +
                        requestedRooms.get(RoomType.DELUXE)    * 2 +
                        requestedRooms.get(RoomType.PENTHOUSE) * 2;

        if (totalCapacity < totalPeople) {
            throw new IllegalArgumentException(
                    "Selected rooms cannot accommodate " + totalPeople + " people."
            );
        }
    }

}
