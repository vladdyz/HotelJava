package ca.senecapolytechnic.application.apd545project.controllers;

import ca.senecapolytechnic.application.apd545project.AppConfig;
import ca.senecapolytechnic.application.apd545project.MainApplication;
import ca.senecapolytechnic.application.apd545project.models.*;
import ca.senecapolytechnic.application.apd545project.repositories.*;
import ca.senecapolytechnic.application.apd545project.security.BCryptPasswordHasher;
import ca.senecapolytechnic.application.apd545project.services.AuthServiceImpl;
import ca.senecapolytechnic.application.apd545project.services.BillingService;
import ca.senecapolytechnic.application.apd545project.services.ReservationService;
import ca.senecapolytechnic.application.apd545project.utils.GuiceFXMLLoader;
import ca.senecapolytechnic.application.apd545project.utils.ReservationObj;
import ca.senecapolytechnic.application.apd545project.utils.Validator;
import com.google.inject.Inject;
import com.google.inject.Injector;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.time.temporal.ChronoUnit;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

public class KioskController {
    // for welcome-view.fxml (the entrypoint to the app) and customer-view.fxml (the kiosk)

    // welcome UI
    @FXML
    private Button  btnCustomer;
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
    private Label labelWiFiTotal;
    @FXML
    private Label labelBreakfastTotal;
    @FXML
    private Label labelParkingTotal;
    @FXML
    private Label labelSpaAccessTotal;
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

    @Inject
    private GuestRepository guestRepo;

    @Inject
    private RoomRepository roomRepository;


    @Inject
    private AddonRepository addonRepository;

    @Inject
    private ReservationRepository reservationRepository;


    @Inject
    private BillingService billingService;

    @Inject
    private ReservationService reservationService;

    // same as admin interface, theres lots of visibility toggled components here
    // this time theyre gridpanes not border panes
    private List<GridPane> customerPages;
    private final GuiceFXMLLoader guiceLoader;
    //private final ReservationService reservationService;
    private static Logger Logger= LoggerFactory.getLogger(KioskController.class);

    private ReservationObj currentDto;

    @Inject
    public KioskController(GuiceFXMLLoader loader, ReservationService reservationService) {
        this.guiceLoader = loader;
        this.reservationService = reservationService;
    }


    @FXML
    private void initialize() {
        //btnLogin.setOnAction(e -> handleLogin());
        //btnCancelLogin.setOnAction(e -> handleCancel());
        //btnAdminLogin.setOnAction(e->adminLogin());
        customerPages = List.of(
                gridPaneReservationBookStepOne,
                gridPaneReservationBookStepTwo,
                gridPaneReservationBookStepThree,
                gridPaneReservationBookStepFour,
                gridPaneReservationBookStepFive

        );
        // should always load on the first page
        showPage(gridPaneReservationBookStepOne);

        btnStepOneNextStep.setOnAction(e -> {
            if (validateStepOne()) showPage(gridPaneReservationBookStepTwo);
        });
        btnStepTwoNextStep.setOnAction(e -> {
            if (validateStepTwo()) showPage(gridPaneReservationBookStepThree);
        });
        btnStepThreeNextStep.setOnAction(e -> {
            if (validateStepThree()) showPage(gridPaneReservationBookStepFour);
        });
        btnStepFourNextStep.setOnAction(e -> {
                updateAddonTotals(); // NEW
                updateSummaryPage(); // NEW
                showPage(gridPaneReservationBookStepFive);
        });
        btnStepFivePreviousStep.setOnAction(e->showPage(gridPaneReservationBookStepFour));
        btnStepFourPreviousStep.setOnAction(e->showPage(gridPaneReservationBookStepThree));
        btnStepThreePreviousStep.setOnAction(e->showPage(gridPaneReservationBookStepTwo));
        btnStepTwoPreviousStep.setOnAction(e->showPage(gridPaneReservationBookStepOne));
        btnLogOut.setOnAction(e->custLogOut());
        btnConfirmBooking.setOnAction(e->handleConfirm());
        checkBoxWiFi.selectedProperty().addListener((obs, oldV, newV) -> updateAddonLabels());
        checkBoxBreakfast.selectedProperty().addListener((obs, oldV, newV) -> updateAddonLabels());
        checkBoxParking.selectedProperty().addListener((obs, oldV, newV) -> updateAddonLabels());
        checkBoxSpa.selectedProperty().addListener((obs, oldV, newV) -> updateAddonLabels());
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ca/senecapolytechnic/application/apd545project/login-view.fxml"));
            //fxmlLoader.setControllerFactory(injector::getInstance);
            //KioskController controller = injector.getInstance(KioskController.class);
            //fxmlLoader.setController(controller);
            Parent root = fxmlLoader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Administrator Login");
            dialog.initModality(Modality.APPLICATION_MODAL);

            dialog.setScene(new Scene(root));
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // repurposed from admin controller
    private void showPage(GridPane paneToShow) {
        for (GridPane pane : customerPages) {
            pane.setVisible(false);
            pane.setManaged(false);  // prevents empty spacing
        }

        // only show the specific param pane
        paneToShow.setVisible(true);
        paneToShow.setManaged(true);
    }

    private void custLogOut() {
            try {
                //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ca/senecapolytechnic/application/apd545project/customer-view.fxml"));
                //fxmlLoader.setControllerFactory(injector::getInstance);
                //KioskController controller = injector.getInstance(KioskController.class);
                //fxmlLoader.setController(controller);
                //Parent root = fxmlLoader.load();
                Parent root = guiceLoader.load("/ca/senecapolytechnic/application/apd545project/welcome-view.fxml");
                Stage stage = (Stage) btnLogOut.getScene().getWindow();
                stage.setTitle("Come On Inn!!");

                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }
    @FXML
    private void handleConfirm() {
        ReservationObj req = newReservationReq();
        try {
            Reservation res = reservationService.createReservation(req);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Your reservation was created successfully! Please see the front desk when you arrive.");
            Logger.info("Success! Reservation #{} created", res.getId());
            // redirecting to home page should clear all fields
            Parent root = guiceLoader.load("/ca/senecapolytechnic/application/apd545project/welcome-view.fxml");
            Stage stage = (Stage) btnLogOut.getScene().getWindow();
            stage.setTitle("Come On Inn!!");

            stage.setScene(new Scene(root));
            stage.show();
        } catch (RuntimeException ex) {
            showAlert(Alert.AlertType.ERROR, "Cannot create reservation", ex.getMessage());
            Logger.info("Error creating reservation: {}", ex.getMessage());
        }
        catch (IOException e) {
        e.printStackTrace();
        }
    }
    private ReservationObj newReservationReq() {


        // first page extract

        String adultsStr = numAdultsInput.getText();
        String childrenStr = numChildrenInput.getText();

        Validator.requireNotNull(adultsStr, "Number of adults required");
        Validator.requireNotNull(childrenStr, "Number of children required");

        int adults;
        int children;

        try {
            adults = Integer.parseInt(adultsStr);
            children = Integer.parseInt(childrenStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Adults/Children must be valid numbers.");
        }

        Validator.requirePositive(adults, "At least 1 adult is required");
        Validator.requirePositive(children, "Children count must be positive or zero");

        LocalDate checkIn = checkInDateInput.getValue();
        LocalDate checkOut = checkOutDateInput.getValue();
        Validator.validateDates(checkIn, checkOut);

        Logger.info("First page extraction completed");

        // second page extract

        int singleCt     = (int) singleRoomSpinner.getValue();
        int doubleCt     = (int) doubleRoomSpinner.getValue();
        int deluxeCt     = (int) deluxeRoomSpinner.getValue();
        int penthouseCt  = (int) penthouseRoomSpinner.getValue();

        Map<RoomType, Integer> requestedRooms = Map.of(
                RoomType.SINGLE, singleCt,
                RoomType.DOUBLE, doubleCt,
                RoomType.DELUXE, deluxeCt,
                RoomType.PENTHOUSE, penthouseCt
        );

        // Validate that some room was selected
        int totalRooms = singleCt + doubleCt + deluxeCt + penthouseCt;
        Validator.requirePositive(totalRooms, "You must select at least 1 room to continue");
        Validator.validateRoomAvailability(requestedRooms, roomRepository);
        Logger.info("Room availability validated");

        Logger.info("Second page extraction completed");



        // third page extract

        String title = (String) choiceBoxTitle.getValue();
        String fname = firstNameInput.getText();
        String lname = lastNameInput.getText();
        String phone = phoneInput.getText();
        String email = emailInput.getText();

        Validator.requireNotNull(title, "Title required");
        Validator.requireNotNull(fname, "First name required");
        Validator.requireNotNull(lname, "Last name required");
        Validator.requireNotNull(phone, "Phone number required");
        Validator.requireNotNull(email, "Email required");

        // create a concatenated string (after validation!) of all the address fields...
        String street = addressStreetInput.getText();
        String postal = addressPostalCodeInput.getText();
        String city   = addressCityInput.getText();
        String country = (String) choiceBoxCountry.getValue();
        String stateProv = (String) choiceBoxStateProvince.getValue();

        Validator.requireNotNull(street, "Street address required");
        Validator.requireNotNull(postal, "Postal code required");
        Validator.requireNotNull(city, "City required");
        Validator.requireNotNull(country, "Country required");
        //Validator.requireNotNull(stateProv, "State/Province if Canada/US");
        Guest existing = guestRepo.findByPhone(phone);

        if (existing != null) {
            Logger.info("Existing guest found: {}", existing);
        }

        String address = street + ", " + city + ", "  + /*stateProv + ", " +*/ country + " " + postal;

        Logger.info("Third page extraction completed");

        // page 4 extract

        Map<Long, Integer> addons = new java.util.HashMap<>();

        // WiFi = id 1, Breakfast = 2, Parking = 3, Spa = 4
        if (checkBoxWiFi.isSelected())      addons.put(1L, 1);
        if (checkBoxBreakfast.isSelected()) addons.put(2L, adults + children);
        if (checkBoxParking.isSelected())   addons.put(3L, 1);
        if (checkBoxSpa.isSelected())       addons.put(4L, adults + children);

        Logger.info("Final page extraction completed");


        // if we made it this far, all good
        // build the object
        // also set labels

        ReservationObj dto = new ReservationObj();
        dto.checkIn = checkIn;
        dto.checkOut = checkOut;
        dto.numAdults = adults;
        dto.numChildren = children;
        dto.requestedRooms = requestedRooms;
        dto.title = title;
        dto.firstName = fname;
        dto.lastName = lname;
        dto.phone = phone;
        dto.email = email;
        dto.address = address;
        dto.addons = addons;
        Logger.info("Reservation object created: {}", dto);
        currentDto = dto;
        return dto;
    }

    @SuppressWarnings("unchecked")
    private int spinnerValue(Spinner spinner) {
        if (spinner == null) return 0;
        Object value = spinner.getValue();
        if (value instanceof Integer) return (Integer) value;
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception e) {
            return 0;
        }
    }

    private void updateSummaryPage() {

        int adults = Integer.parseInt(numAdultsInput.getText());
        int children = Integer.parseInt(numChildrenInput.getText());
        int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(checkInDateInput.getValue(), checkOutDateInput.getValue());

        // Guest
        labelGuestName.setText(choiceBoxTitle.getValue() + " " + firstNameInput.getText() + " " + lastNameInput.getText());
        labelNumPeople.setText(adults + " Adults, " + children + " Children");
        labelDuration.setText(checkInDateInput.getValue() + " - " + checkOutDateInput.getValue() + " : " + nights + " Nights");

        // Amenities summary
        double wifi = parseMoney(labelWiFiTotal.getText());
        double breakfast = parseMoney(labelBreakfastTotal.getText());
        double parking = parseMoney(labelParkingTotal.getText());
        double spa = parseMoney(labelSpaAccessTotal.getText());

        double addonTotal = wifi + breakfast + parking + spa;
        labelAmenities.setText(String.format("$%.2f", addonTotal));

        Map<RoomType, Integer> selectedRooms = Map.of(
                RoomType.SINGLE, (int)singleRoomSpinner.getValue(),
                RoomType.DOUBLE, (int)doubleRoomSpinner.getValue(),
                RoomType.DELUXE, (int)deluxeRoomSpinner.getValue(),
                RoomType.PENTHOUSE, (int)penthouseRoomSpinner.getValue()
        );


        double roomSubtotal = // billingService.calculateRoomSubtotal(currentDto.requestedRooms, nights);
                billingService.calculateRoomSubtotal(selectedRooms, nights);

        double subtotal = roomSubtotal + addonTotal;
        double tax = subtotal * 0.13;
        double total = subtotal + tax;

        labelSubTotal.setText(String.format("$%.2f", subtotal));
        labelTax.setText(String.format("$%.2f", tax));
        labelTotal.setText(String.format("$%.2f", total));

        // look up loyalty points of guest by phone number, 0 default
       /* labelLoyaltyPoints.setText(String.valueOf(
               guestRepo.findByPhone(phoneInput.getText())
        ));*/
        Guest foundGuest = guestRepo.findByPhone(phoneInput.getText().trim());
        labelLoyaltyPoints.setText(foundGuest != null
                ? String.valueOf(foundGuest.getLoyaltyNumber())
                : "0"
        );
    }

    private double parseMoney(String text) {
        if (text == null || text.isBlank()) return 0.0;
        return Double.parseDouble(text.replaceAll("[^0-9.]", ""));
    }
    private Map<RoomType, Integer> buildRequestedRooms() {
        return Map.of(
                RoomType.SINGLE, (Integer) singleRoomSpinner.getValue(),
                RoomType.DOUBLE, (Integer) doubleRoomSpinner.getValue(),
                RoomType.DELUXE, (Integer) deluxeRoomSpinner.getValue(),
                RoomType.PENTHOUSE, (Integer) penthouseRoomSpinner.getValue()
        );
    }

    private void updateAddonTotals() {
        int adults = Integer.parseInt(numAdultsInput.getText().trim());
        int children = Integer.parseInt(numChildrenInput.getText().trim());
        int people = adults + children;
        int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(checkInDateInput.getValue(), checkOutDateInput.getValue());


        double wifiPricePerNight = 10.0;
        double breakfastPerPersonPerNight = 5.0;
        double parkingPerNight = 20.0;
        double spaFlat = 40.0;

        labelWiFiTotal.setText(checkBoxWiFi.isSelected() ? String.format("$%.2f", wifiPricePerNight * nights) : "$0.00");

        labelBreakfastTotal.setText(checkBoxBreakfast.isSelected() ?
                String.format("$%.2f", breakfastPerPersonPerNight * people * nights) : "$0.00");

        labelParkingTotal.setText(checkBoxParking.isSelected() ?
                String.format("$%.2f", parkingPerNight * nights) : "$0.00");

        labelSpaAccessTotal.setText(checkBoxSpa.isSelected() ?
                String.format("$%.2f", spaFlat) : "$0.00");
    }

    private void validateRoomType(RoomType type, int requested, LocalDate in, LocalDate out) {
        if (requested <= 0) return;

        List<Room> available = roomRepository.findAvailable(type, in, out);
        if (available.size() < requested) {
            throw new IllegalArgumentException(
                    "Not enough " + type + " rooms available (" +
                            "Requested: " + requested + ", Available: " + available.size() + ")"
            );
        }
    }

    private boolean validateStepOne() {
        String message = "";
        try {
            int adults = Integer.parseInt(numAdultsInput.getText().trim());
            int children = Integer.parseInt(numChildrenInput.getText().trim());

            Validator.requirePositive(adults, "At least 1 adult is required");
            if (children < 0) {
                message = "Please specify the number of adults and children";
                throw new IllegalArgumentException("Children must be zero or more");
            }

            Validator.validateDates(checkInDateInput.getValue(), checkOutDateInput.getValue());
            return true;
        } catch (Exception ex) {
            if (message.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Invalid Step 1", "Please specify the number of adults and children");
            }
            else {
                showAlert(Alert.AlertType.ERROR, "Invalid Step 1", ex.getMessage());
            }
            //showAlert(Alert.AlertType.ERROR, "Invalid Step 1", "Please specify the number of adults and children");
            Logger.info("Validation step 1 fail: {}", ex.getMessage());
            return false;
        }
    }
    private boolean validateStepTwo() {

        try {
            int singleCt = (int) singleRoomSpinner.getValue();
            int doubleCt = (int) doubleRoomSpinner.getValue();
            int deluxeCt = (int) deluxeRoomSpinner.getValue();
            int penthouseCt = (int) penthouseRoomSpinner.getValue();

            Map<RoomType, Integer> requested = Map.of(
                    RoomType.SINGLE, singleCt,
                    RoomType.DOUBLE, doubleCt,
                    RoomType.DELUXE, deluxeCt,
                    RoomType.PENTHOUSE, penthouseCt
            );

            int total = singleCt + doubleCt + deluxeCt + penthouseCt;
            if (total <= 0)
                throw new IllegalArgumentException("Select at least one room");

            // Check availability via repository
            for (Map.Entry<RoomType, Integer> e : requested.entrySet()) {
                if (e.getValue() <= 0) continue;

                int required = e.getValue();

                int available = roomRepository
                        .findAvailable(e.getKey(),
                                checkInDateInput.getValue(),
                                checkOutDateInput.getValue())
                        .size();

                if (available < required) {
                    throw new IllegalArgumentException(
                            "Not enough " + e.getKey() + " rooms available. " +
                                    "Required: " + required + ", Available: " + available);
                }
            }
            Validator.validateRoomCapacity(Integer.parseInt(numAdultsInput.getText()), Integer.parseInt(numChildrenInput.getText()), requested);

            return true;

        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Invalid Step 2", ex.getMessage());
            return false;
        }
    }
    private boolean validateStepThree() {
        try {
            Validator.requireNotNull(choiceBoxTitle.getValue(), "Title required");
            if (firstNameInput.getText().isBlank()) throw new IllegalArgumentException("First name required");
            if (lastNameInput.getText().isBlank()) throw new IllegalArgumentException("Last name required");

            if (!phoneInput.getText().matches("\\d{10}"))
                throw new IllegalArgumentException("Phone must be 10 digits");

            if (!emailInput.getText().contains("@"))
                throw new IllegalArgumentException("Email must be valid");

            if (addressStreetInput.getText().isBlank()) throw new IllegalArgumentException("Street required");
            if (addressPostalCodeInput.getText().isBlank()) throw new IllegalArgumentException("Postal code required");
            if (addressCityInput.getText().isBlank()) throw new IllegalArgumentException("City required");

            Validator.requireNotNull(choiceBoxCountry.getValue(), "Country required");
            //Validator.requireNotNull(choiceBoxStateProvince.getValue(), "Province/State required");


            return true;
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Invalid Step 3", ex.getMessage());
            return false;
        }
    }

    private void updateAddonLabels() {
        int people = Integer.parseInt(numAdultsInput.getText()) +
                Integer.parseInt(numChildrenInput.getText());

        int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(checkInDateInput.getValue(), checkOutDateInput.getValue());
        double wifi = checkBoxWiFi.isSelected() ? 10 * nights: 0;
        double breakfast = checkBoxBreakfast.isSelected() ? 5 * nights *  people : 0;
        double parking = checkBoxParking.isSelected() ? 20 * nights : 0;
        double spa = checkBoxSpa.isSelected() ? 40 * people : 0;

        labelWiFiTotal.setText(String.format("$%.2f", wifi));
        labelBreakfastTotal.setText(String.format("$%.2f", breakfast));
        labelParkingTotal.setText(String.format("$%.2f", parking));
        labelSpaAccessTotal.setText(String.format("$%.2f", spa));


    }
}
