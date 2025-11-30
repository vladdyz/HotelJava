package ca.senecapolytechnic.application.apd545project.repositories;

import ca.senecapolytechnic.application.apd545project.models.Guest;

import java.util.List;

public interface GuestRepository {
    Guest save(Guest guest);
    Guest findById(Long id);
    List<Guest> findAll();
    Guest findByPhone(String phone);
    void delete(Guest guest);
    Guest findByName(String name);
    Guest findByLoyaltyNumber(Integer number);
}
