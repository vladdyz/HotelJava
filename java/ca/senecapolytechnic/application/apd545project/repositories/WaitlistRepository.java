package ca.senecapolytechnic.application.apd545project.repositories;

import ca.senecapolytechnic.application.apd545project.models.RoomType;
import ca.senecapolytechnic.application.apd545project.models.Waitlist;

import java.util.List;

public interface WaitlistRepository {
    Waitlist save(Waitlist waitlist);
    Waitlist findById(Long id);
    List<Waitlist> findAll();
    List<Waitlist> findByGuestId(Long guestId);
    List<Waitlist> findByRoomType(RoomType type);
}
