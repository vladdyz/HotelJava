package ca.senecapolytechnic.application.apd545project.repositories;


import ca.senecapolytechnic.application.apd545project.models.AdminUser;



public interface AdminUserRepository {
    AdminUser findByUsername(String username);
    void save(AdminUser user);
    void update(AdminUser user);
}