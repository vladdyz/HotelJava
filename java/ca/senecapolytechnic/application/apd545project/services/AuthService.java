package ca.senecapolytechnic.application.apd545project.services;

import ca.senecapolytechnic.application.apd545project.models.AdminUser;

public interface AuthService {
    AdminUser authenticate(String username, String password);
    void setActive(AdminUser user, boolean active);
}