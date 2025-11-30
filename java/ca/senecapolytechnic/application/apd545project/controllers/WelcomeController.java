package ca.senecapolytechnic.application.apd545project.controllers;


import ca.senecapolytechnic.application.apd545project.AppConfig;
import ca.senecapolytechnic.application.apd545project.MainApplication;
import ca.senecapolytechnic.application.apd545project.models.AdminUser;
import ca.senecapolytechnic.application.apd545project.repositories.AdminUserRepository;
import ca.senecapolytechnic.application.apd545project.repositories.AdminUserRepositoryImpl;
import ca.senecapolytechnic.application.apd545project.security.BCryptPasswordHasher;
import ca.senecapolytechnic.application.apd545project.services.AuthServiceImpl;
import ca.senecapolytechnic.application.apd545project.utils.GuiceFXMLLoader;
import com.google.inject.Inject;
import com.google.inject.Injector;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import java.io.IOException;

public class WelcomeController {
    // for welcome-view.fxml (the entrypoint to the app) and customer-view.fxml (the kiosk)

    // welcome UI
    @FXML
    private Button btnCustomer;
    @FXML
    private Button btnAdminLogin;
    @FXML
    private Button btnFeedback;
    @FXML
    private Button btnRegulations;

    // customer-view

    // stack pane for the reservation booking process (5 sections)
    @FXML
    private StackPane stackPaneReservationBook;

    // section 1
    @FXML
    private GridPane gridPaneReservationBookStepOne;
    @FXML
    private TextField numAdultsInput;
    @FXML
    private TextField numChildrenInput;
    @FXML
    private DatePicker checkInDateInput;
    @FXML
    private DatePicker checkOutDateInput;
    @FXML
    private Button btnStepOneNextStep;
    // section 2
    @FXML
    private GridPane gridPaneReservationBookStepTwo;
    @FXML
    private Spinner singleRoomSpinner;
    //singleRoomSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 50));
    @FXML
    private Spinner doubleRoomSpinner;
    @FXML
    private Spinner deluxeRoomSpinner;
    @FXML
    private Spinner penthouseRoomSpinner;
    @FXML
    private Button btnStepTwoNextStep;
    @FXML
    private Button btnStepTwoPreviousStep;
    // section 3
    @FXML
    private GridPane gridPaneReservationBookStepThree;
    @FXML
    private ChoiceBox choiceBoxTitle; // options set in xml
    @FXML
    private TextField firstNameInput;
    @FXML
    private TextField lastNameInput;
    @FXML
    private TextField phoneInput;
    @FXML
    private TextField emailInput;
    @FXML
    private TextField addressStreetInput;
    @FXML
    private TextField addressPostalCodeInput;
    @FXML
    private TextField addressCityInput;
    @FXML
    private ChoiceBox choiceBoxCountry; // defined in the xml
    @FXML
    private ChoiceBox choiceBoxStateProvince; // this needs to be set on initialize bc it either loads states or provinces
    @FXML
    private Button btnStepThreeNextStep;
    @FXML
    private Button btnStepThreePreviousStep;
    // section 4
    @FXML
    private GridPane gridPaneReservationBookStepFour;
    @FXML
    private CheckBox checkBoxWiFi;
    @FXML
    private CheckBox checkBoxBreakfast;
    @FXML
    private CheckBox checkBoxParking;
    @FXML
    private CheckBox checkBoxSpa;
    @FXML
    private Button btnStepFourNextStep;
    @FXML
    private Button btnStepFourPreviousStep;
    // section 5
    @FXML
    private GridPane gridPaneReservationBookStepFive;
    @FXML
    private Label labelGuestName;
    @FXML
    private Label labelNumPeople;
    @FXML
    private Label labelDuration;
    @FXML
    private Label labelAmenities;
    @FXML
    private Label labelSubTotal;
    @FXML
    private Label labelTax;
    @FXML
    private Label labelTotal;
    @FXML
    private Label labelLoyaltyPoints;
    @FXML
    private Button btnConfirmBooking;
    @FXML
    private Button btnStepFivePreviousStep;
    // left panel
    @FXML
    private Button btnBookReservation;
    @FXML
    private Button btnCheckout;
    @FXML
    private Button btnRegulationsCustView;
    @FXML
    private Button btnLogOut;

    // Login window
    @FXML
    private TextField loginUsernameInput;
    @FXML
    private PasswordField loginPasswordInput;
    @FXML
    private Button btnLogin;
    @FXML
    private Button btnCancelLogin;

    private Injector injector;
    private Stage primaryStage;

    private final GuiceFXMLLoader guiceLoader;

    @Inject
    public WelcomeController(GuiceFXMLLoader loader) {
        this.guiceLoader = loader;
    }


    @FXML
    private void initialize() {
        //btnLogin.setOnAction(e -> handleLogin());
        //btnCancelLogin.setOnAction(e -> handleCancel());
        btnAdminLogin.setOnAction(e->adminLogin());
        btnCustomer.setOnAction(e->customerView());
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
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("admin-view.fxml")
            );

            Parent root = loader.load();

            AdminController adminController = loader.getController();
            adminController.init(user);  // <-- pass the logged-in admin

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to open admin interface.");
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
        Stage stage = (Stage) btnCancelLogin.getScene().getWindow();
        stage.close();

    }
    private void adminLogin() {
        try {
            Parent root = guiceLoader.load("/ca/senecapolytechnic/application/apd545project/login-view.fxml");
            //fxmlLoader.setControllerFactory(injector::getInstance);
            //KioskController controller = injector.getInstance(KioskController.class);
            //fxmlLoader.setController(controller);
            //Parent root = fxmlLoader.load();
            Stage stage = (Stage) btnAdminLogin.getScene().getWindow();
            stage.setTitle("Administrator Login");
            //stage.initModality(Modality.APPLICATION_MODAL);

            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void customerView() {
        try {
            //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ca/senecapolytechnic/application/apd545project/customer-view.fxml"));
            //fxmlLoader.setControllerFactory(injector::getInstance);
            //KioskController controller = injector.getInstance(KioskController.class);
            //fxmlLoader.setController(controller);
            //Parent root = fxmlLoader.load();
            Parent root = guiceLoader.load("/ca/senecapolytechnic/application/apd545project/customer-view.fxml");
            Stage stage = (Stage) btnCustomer.getScene().getWindow();
            stage.setTitle("Customer Kiosk");

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
