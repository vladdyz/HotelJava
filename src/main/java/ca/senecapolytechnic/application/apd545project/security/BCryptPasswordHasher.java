package ca.senecapolytechnic.application.apd545project.security;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptPasswordHasher {

    public static String hash(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt());
    }

    public static boolean verify(String plain, String hashed) {
        return BCrypt.checkpw(plain, hashed);
    }
}