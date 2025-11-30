package ca.senecapolytechnic.application.apd545project.repositories;

import ca.senecapolytechnic.application.apd545project.models.Room;
import ca.senecapolytechnic.application.apd545project.models.RoomType;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository {
    void save(Room room);
    Room findById(Long id);
    Room findByRoomNumber(int roomNumber);
    List<Room> findAll();
    void update(Room room);
    void delete(Long id);
    Room findByRoomType(RoomType roomType);
    List<Room> findAvailable(RoomType type, LocalDate from, LocalDate to);
    int countAvailableRooms(RoomType type);

}
