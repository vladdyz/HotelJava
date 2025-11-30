package ca.senecapolytechnic.application.apd545project.controllers;

import ca.senecapolytechnic.application.apd545project.AppConfig;
import ca.senecapolytechnic.application.apd545project.models.AdminUser;
import ca.senecapolytechnic.application.apd545project.repositories.AdminUserRepositoryImpl;
import ca.senecapolytechnic.application.apd545project.security.AuthService;
import ca.senecapolytechnic.application.apd545project.security.BCryptPasswordHasher;
import ca.senecapolytechnic.application.apd545project.services.AuthServiceImpl;
import ca.senecapolytechnic.application.apd545project.utils.GuiceFXMLLoader;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;

public class LoginController {
    // moved to a separate controller so i dont have to juggle 2 FXMLs

    // Login window
    @FXML
    private TextField loginUsernameInput;
    @FXML
    private PasswordField loginPasswordInput;
    @FXML
    private Button btnLogin;
    @FXML
    private Button btnCancelLogin;

    private final GuiceFXMLLoader guiceLoader;
    private static Logger Logger = LoggerFactory.getLogger(LoginController.class);

    @Inject
    public LoginController(GuiceFXMLLoader loader) {
        this.guiceLoader = loader;
    }

    @FXML
    private void initialize() {
        btnLogin.setOnAction(e -> handleLogin());
        btnCancelLogin.setOnAction(e -> handleCancel());
    }

    // admin login window
    private void handleLogin() {
        String username = loginUsernameInput.getText().trim();
        String password = loginPasswordInput.getText().trim();

        AuthServiceImpl authServiceImpl = getAuthService();

        AdminUser user = authServiceImpl.authenticate(username, password);

        if (user != null) {
            // Mark user as logged in
            //user.setActive(true);
            authServiceImpl.setActive(user, true);
            authServiceImpl.getRepository().update(user);
            AuthService.login(user);

            // before opening the admin ui, set active bool in authenticated user to true
            // this identifies which admin user is logged in
            activateAdmin(user);

            openAdminInterface(user);
        } else {
            showAlert(Alert.AlertType.ERROR, "Invalid Credentials", "Username or Password incorrect.");
        }

        // on logout dont forget to set active to false!!
        // authService.setActive(currentUser, false); <-this will probably be in admin interface
    }

    private AuthServiceImpl getAuthService() {
        EntityManager em = AppConfig.getEntityManager();
        AdminUserRepositoryImpl repo = new AdminUserRepositoryImpl(em);
        BCryptPasswordHasher hasher = new BCryptPasswordHasher();
        return new AuthServiceImpl(repo, hasher);
    }

    private void openAdminInterface(AdminUser user) {
        try {
            Parent root = guiceLoader.load(
                    "/ca/senecapolytechnic/application/apd545project/admin-view.fxml"
            );

            Stage stage = (Stage) btnLogin.getScene().getWindow();

            //AdminController adminController = loader.getController();
            //adminController.init(user);  // <-- pass the logged-in admin

            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to open admin interface.");
        }
    }

    private void activateAdmin(AdminUser user) {

        EntityManager em = AppConfig.getEntityManager();
        try {
            em.getTransaction().begin();

            AdminUser dbUser = em.find(AdminUser.class, user.getId());
            dbUser.setActive(true);

            em.getTransaction().commit();

            //Logger.info("Admin '{}' logged in and activated.", dbUser.getUsername());
            Logger.info("Logged in as: " + dbUser.getUsername());
            Logger.info("Role: " + dbUser.getRole());

        } catch (Exception e) {
            em.getTransaction().rollback();
            Logger.error("Failed to activate admin", e);
        } finally {
            em.close();
        }
    }


    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type, msg);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    private void handleCancel() {
        // just return to the main screen
        //Stage stage = (Stage) btnCancelLogin.getScene().getWindow();
        //stage.close();
        try {
            Parent root = guiceLoader.load(
                    "/ca/senecapolytechnic/application/apd545project/welcome-view.fxml"
            );

            Stage stage = (Stage) btnLogin.getScene().getWindow();

            //AdminController adminController = loader.getController();
            //adminController.init(user);  // <-- pass the logged-in admin

            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to open welcome interface.");
        }

    }

}
