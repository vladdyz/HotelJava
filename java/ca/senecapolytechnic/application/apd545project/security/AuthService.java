package ca.senecapolytechnic.application.apd545project.security;

import ca.senecapolytechnic.application.apd545project.models.AdminUser;

public class AuthService {
    private static AdminUser currentAdmin;

    public static void login(AdminUser admin) {
        currentAdmin = admin;
    }

    public static AdminUser getCurrentAdmin() {
        return currentAdmin;
    }

    public static void logout() {
        currentAdmin = null;
    }

    public static boolean isLoggedIn() {
        return currentAdmin != null;
    }
}
