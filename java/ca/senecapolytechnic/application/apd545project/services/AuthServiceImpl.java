package ca.senecapolytechnic.application.apd545project.services;
import ca.senecapolytechnic.application.apd545project.config.GuiceModule;
import ca.senecapolytechnic.application.apd545project.models.AdminUser;
import ca.senecapolytechnic.application.apd545project.repositories.AdminUserRepository;
import ca.senecapolytechnic.application.apd545project.security.BCryptPasswordHasher;
import ca.senecapolytechnic.application.apd545project.services.AuthService;
import com.google.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;

public class AuthServiceImpl implements AuthService {
    private final AdminUserRepository repo;
    private final BCryptPasswordHasher hasher;
    @Inject
    public AuthServiceImpl(AdminUserRepository repo, BCryptPasswordHasher hasher) {
        this.repo = repo;
        this.hasher = hasher;
    }

    public AdminUser authenticate(String username, String password) {
        AdminUser user = repo.findByUsername(username);

        if (user == null /*|| !user.getActive()*/) {
            return null;
        }

        // dont check for this here, put it in the bcrpyt module

        /*if (BCrypt.checkpw(password, user.getPasswordHash())) {
            return user;
        }*/

        return hasher.verify(password, user.getPasswordHash()) ? user : null;
    }
    public AdminUserRepository getRepository() { return repo; }

    // an admin is marked as active when theyre logged in, need to update
    public void setActive(AdminUser user, boolean active) {
        user.setActive(active);
        repo.update(user);
    }


}

