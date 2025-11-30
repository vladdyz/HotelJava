package ca.senecapolytechnic.application.apd545project.controllers;

import ca.senecapolytechnic.application.apd545project.AppConfig;
import ca.senecapolytechnic.application.apd545project.config.LoyaltyPolicy;
import ca.senecapolytechnic.application.apd545project.models.*;
import ca.senecapolytechnic.application.apd545project.repositories.GuestRepository;
import ca.senecapolytechnic.application.apd545project.repositories.ReservationRepository;
import ca.senecapolytechnic.application.apd545project.repositories.ReservationRoomRepository;
import ca.senecapolytechnic.application.apd545project.repositories.RoomRepository;
import ca.senecapolytechnic.application.apd545project.security.AuthService;
import ca.senecapolytechnic.application.apd545project.utils.GuiceFXMLLoader;
import com.google.inject.Inject;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.logging.LogManager;
import java.util.stream.Collectors;

public class AdminController {

    // another view with a bunch of panes and controls....

    // side panel buttons
    @FXML
    private Button btnAdminReservations;
    @FXML
    private Button btnAdminGuests;
    @FXML
    private Button btnAdminRooms;
    @FXML
    private Button btnAdminLoyaltyPts;
    @FXML
    private Button btnAdminFeedback;
    @FXML
    private Button btnAdminLogs;
    @FXML
    private Button btnAdminLogOut;

    // stackpane bindings

    // section 1 - Reservations
    @FXML
    private BorderPane borderPaneReservations;
    @FXML
    private Button btnEditReservation;
    @FXML
    private Button btnNewReservation;
    @FXML
    private Button btnCancelReservation;
    @FXML
    private TableView<Reservation> tableReservations;
    @FXML
    private TableColumn<Reservation, Long>  reservationIdCol;
    @FXML
    private TableColumn<Reservation, String>  reservationGuestNameCol;
    @FXML
    private TableColumn<Reservation, LocalDate> reservationCheckInCol;
    @FXML
    private TableColumn<Reservation, LocalDate> reservationCheckOutCol;
    @FXML
    private TableColumn<Reservation, Integer> reservationAdultsCol;
    @FXML
    private TableColumn<Reservation, Integer> reservationChildrenCol;
    @FXML
    private TableColumn<Reservation, String> reservationStatusCol;
    @FXML
    private TextField searchGuestsInput;
    @FXML
    private DatePicker searchReservationsDateStartInput;
    @FXML
    private DatePicker searchReservationsDateEndInput;
    @FXML
    private ChoiceBox<ReservationStatus> choiceBoxReservationStatus;
    @FXML
    private Button btnReservationsCheckout;

    // section 2 - Guests
    @FXML
    private BorderPane borderPaneGuests;
    @FXML
    private TableView<Guest> tableGuests;
    @FXML
    private TableColumn<Guest, Long> guestsIdCol;
    @FXML
    private TableColumn<Guest, String> guestsNameCol;
    @FXML
    private TableColumn<Guest, String> guestsPhoneCol;
    @FXML
    private TableColumn<Guest, String> guestsEmailCol;
    @FXML
    private TableColumn<Guest, String> guestsAddressCol;
    @FXML
    private TableColumn<Guest, Integer> guestsLoyaltyPtsCol;
    @FXML
    private TextField searchGuestsInput2;
    @FXML
    private ToggleGroup guestsSearchFilter;
    @FXML
    private RadioButton btnRadioSearchEmail;
    @FXML
    private RadioButton btnRadioSearchName;
    @FXML
    private RadioButton btnRadioSearchPhone;

    // section 3 - room management
    @FXML
    private BorderPane borderPaneRoomMgmt;
    @FXML
    private Button btnViewWaitlist;
    @FXML
    private Button btnUpdateAvailability;
    @FXML
    private TableView<Room> tableRoomMgmt;
    @FXML
    private TableColumn<Room, Long> roomMgmtIdCol;
    @FXML
    private TableColumn<Room, Integer> roomMgmtRoomNoCol;
    @FXML
    private TableColumn<Room, String> roomMgmtTypeCol;
    @FXML
    private TableColumn<Room, Integer> roomMgmtBedsCol;
    @FXML
    private TableColumn<Room, Double> roomMgmtBasePriceCol;
    @FXML
    private TableColumn<Room, String> roomMgmtStatusCol;
    @FXML
    private TextField btnSearchRoomNoInput;
    @FXML
    private ChoiceBox<RoomType> choiceBoxRoomTypeFilter;
    @FXML
    private ChoiceBox<RoomStatus> choiceBoxRoomStatusFilter;
    @FXML
    private DatePicker roomDateFilterInput;
    @FXML
    private TextField occupancyPercentage;
    @FXML
    private Button btnExportRoomMgmt;

    // section 4 - feedback submissions
    @FXML
    private BorderPane borderPaneFeedback;
    @FXML
    private Button btnExportFeedback;
    @FXML
    private TableView<Feedback> tableFeedback;
    @FXML
    private TableColumn<Feedback, Integer> feedbackIdCol;
    @FXML
    private TableColumn<Feedback, String> feedbackGuestCol;
    @FXML
    private TableColumn<Feedback, Integer> feedbackReservationIdCol;
    @FXML
    private TableColumn<Feedback, Integer> feedbackRatingCol;
    @FXML
    private TableColumn<Feedback, String> feedbackCommentsCol;
    @FXML
    private TableColumn<Feedback, String> feedbackSentimentCol;
    @FXML
    private TableColumn<Feedback, Date> feedbackCreatedCol;
    @FXML
    private TextField searchGuestInput3;
    @FXML
    private ChoiceBox choiceBoxFeedbackType;
    @FXML
    private ChoiceBox choiceBoxFeedbackRating;
    @FXML
    private ChoiceBox choiceBoxFeedbackSentiment;
    @FXML
    private DatePicker searchFeedbackDateStartInput;
    @FXML
    private DatePicker searchFeedbackDateEndInput;

    // section 5 - audit logs
    @FXML
    private BorderPane borderPaneAuditLogs;
    @FXML
    private Button btnExportAuditLogs;
    @FXML
    private TableView<AuditLog> tableAudit;
    @FXML
    private TableColumn<AuditLog, Integer> auditIdCol;
    @FXML
    private TableColumn<AuditLog, Date> auditTimestampCol;
    @FXML
    private TableColumn<AuditLog, String> auditActorCol;
    @FXML
    private TableColumn<AuditLog, String> auditActionCol;
    @FXML
    private TableColumn<AuditLog, String> auditEntityTypeCol;
    @FXML
    private TableColumn<AuditLog, Integer> auditEntityIdCol;
    @FXML
    private TableColumn<AuditLog, String> auditMessageCol;
    @FXML
    private TextField searchActorInput;
    @FXML
    private ChoiceBox choiceBoxEntityType;
    @FXML
    private DatePicker searchAuditStartDateInput;
    @FXML
    private DatePicker searchAuditEndDateInput;

    // section 6 - loyalty program
    @FXML
    private BorderPane borderPaneLoyalty;
    @FXML
    private Button btnAddLoyaltyMember;
    @FXML
    private Button btnUpdateLoyaltyDetails;
    @FXML
    private Button btnCancelLoyaltyMembership;
    @FXML
    private TableView<Guest> tableLoyalty;
    @FXML
    private TableColumn<Guest, Long> loyaltyIdCol;
    @FXML
    private TableColumn<Guest, String> loyaltyGuestCol;
    @FXML
    private TableColumn<Guest, String> loyaltyPhoneCol;
    @FXML
    private TableColumn<Guest, Integer> loyaltyNumCol;
    @FXML
    private TableColumn<Guest, Boolean> loyaltyActiveCol;
    @FXML
    private TableColumn<Guest, Integer> loyaltyPtsCol;
    @FXML
    private TextField searchLoyaltyPhoneInput;
    @FXML
    private ChoiceBox choiceBoxEarningRate;
    @FXML
    private ChoiceBox choiceBoxRedemptionCap;
    @FXML
    private Button btnViewRedemptionHistory;


    // the checkout modal (admin-checkout-view.fxml)
    @FXML
    private BorderPane borderPaneAdminCheckOut;
    @FXML
    private Label labelCheckOutHeader;
    @FXML
    private Label labelCheckOutSubHeader;
    @FXML
    private Label labelCheckOutInvoiceId;
    @FXML
    private Label labelCheckOutSubTotal;
    @FXML
    private Label labelCheckOutTaxRate;
    @FXML
    private Label labelCheckOutTaxTotal;
    @FXML
    private ChoiceBox choiceBoxApplyDiscountRate;
    @FXML
    private TextField loyaltyPtsRedeemInput;
    @FXML
    private Label labelCheckOutTotal;
    @FXML
    private Label labelCheckOutPaid;
    @FXML
    private Label labelCheckOutBalance;
    @FXML
    private ChoiceBox choiceBoxCheckOutPaymentStatus;
    @FXML
    private Label labelCheckOutLoyaltyPtsBalance;
    @FXML
    private ChoiceBox<PaymentMethod> choiceBoxCheckOutPaymentType;
    @FXML
    private TextField checkOutPaymentAmountInput;
    @FXML
    private Button btnCheckOutConfirm;
    @FXML
    private Button btnCheckOutViewPaymentHistory;
    @FXML
    private Button btnCloseCheckout;
    @FXML
    private Label labelCheckOutReservationTitle;

    private Logger Logger= LoggerFactory.getLogger(AdminController.class);
    private List<BorderPane> adminPages;
    private final GuiceFXMLLoader guiceLoader;
    // lists for tables
    private ObservableList<Reservation> reservations;
    private FilteredList<Reservation> filteredReservations;
    private ObservableList<Guest> guestList;
    private FilteredList<Guest> filteredGuests;



    private AdminUser loggedInAdmin;
    private Stage checkoutStage;

    @Inject
    private ReservationRepository reservationRepository;
    @Inject
    private GuestRepository guestRepository;
    @Inject
    private RoomRepository roomRepository;
    @Inject
    private ReservationRoomRepository reservationRoomRepository;

    @Inject
    public AdminController(GuiceFXMLLoader loader) {
        this.guiceLoader = loader;
    }

    public void init(AdminUser admin) {
        this.loggedInAdmin = admin;

        Logger.info("Logged in as: " + admin.getUsername());
        Logger.info("Role: " + admin.getRole());

        if (admin.getRole() == Role.MANAGER) {
            //disableDiscountSettings();
        }

        // all of the border panes which toggle visibility
        adminPages = List.of(
                borderPaneReservations,
                borderPaneGuests,
                borderPaneRoomMgmt,
                borderPaneFeedback,
                borderPaneAuditLogs,
                borderPaneLoyalty
                //borderPaneAdminCheckOut
        );
        // event listeners for all the buttons that control the panes
        btnAdminReservations.setOnAction(e -> showPage(borderPaneReservations));
        btnAdminGuests.setOnAction(e -> showPage(borderPaneGuests));
        btnAdminRooms.setOnAction(e -> showPage(borderPaneRoomMgmt));
        btnAdminFeedback.setOnAction(e -> showPage(borderPaneFeedback));
        btnAdminLogs.setOnAction(e -> showPage(borderPaneAuditLogs));
        btnAdminLoyaltyPts.setOnAction(e -> showPage(borderPaneLoyalty));
       // btnReservationsCheckout.setOnAction(e->showPage(borderPaneAdminCheckOut));
        btnReservationsCheckout.setOnAction(e -> onOpenCheckout());
        btnAdminLogOut.setOnAction(e->logOut());
        // default screen
        showPage(borderPaneReservations);
        // other buttons in various panes
        btnCancelReservation.setOnAction(e -> handleCancelReservation());
        btnNewReservation.setOnAction(e -> handleNewReservation());
        btnUpdateAvailability.setOnAction(e->updateAvailabilityButton());
        initLoyaltyPane();

        // tables
        initReservationColumns();
        loadReservations();
        initGuestColumns();
        loadGuests();
        initRoomManagementTable();
        loadRooms();
        initLoyaltyTable();
        loadLoyalty();


        // search filters
        initReservationStatusFilter();
        searchGuestsInput.textProperty().addListener((obs, oldV, newV) -> applyFilters());

        searchReservationsDateStartInput.valueProperty().addListener((obs, o, n) -> applyFilters());
        searchReservationsDateEndInput.valueProperty().addListener((obs, o, n) -> applyFilters());

        choiceBoxReservationStatus.valueProperty().addListener((obs, o, n) -> applyFilters());

        initGuestSearchFilters();
        choiceBoxRoomTypeFilter.getItems().add(null); // "Any" option
        choiceBoxRoomTypeFilter.getItems().addAll(RoomType.values());
        choiceBoxRoomTypeFilter.setValue(null); // default no filter
        choiceBoxRoomStatusFilter.getItems().add(null);
        choiceBoxRoomStatusFilter.getItems().addAll(RoomStatus.values());
        choiceBoxRoomStatusFilter.setValue(null);
        initRoomFilterListeners();
        updateOccupancyPercentage();
        searchLoyaltyPhoneInput.textProperty().addListener((obs, oldV, newV) -> applyLoyaltyFilters());

    }

    public void initialize() {
        loggedInAdmin = AuthService.getCurrentAdmin();


        // all of the border panes which toggle visibility
        adminPages = List.of(
                borderPaneReservations,
                borderPaneGuests,
                borderPaneRoomMgmt,
                borderPaneFeedback,
                borderPaneAuditLogs,
                borderPaneLoyalty
                //borderPaneAdminCheckOut
        );
        // event listeners for all the buttons that control the panes
        btnAdminReservations.setOnAction(e -> showPage(borderPaneReservations));
        btnAdminGuests.setOnAction(e -> showPage(borderPaneGuests));
        btnAdminRooms.setOnAction(e -> showPage(borderPaneRoomMgmt));
        btnAdminFeedback.setOnAction(e -> showPage(borderPaneFeedback));
        btnAdminLogs.setOnAction(e -> showPage(borderPaneAuditLogs));
        btnAdminLoyaltyPts.setOnAction(e -> showPage(borderPaneLoyalty));
        // btnReservationsCheckout.setOnAction(e->showPage(borderPaneAdminCheckOut));
        btnReservationsCheckout.setOnAction(e -> onOpenCheckout());
        btnAdminLogOut.setOnAction(e->logOut());
        // default screen
        showPage(borderPaneReservations);
        // other buttons in various panes
        btnCancelReservation.setOnAction(e -> handleCancelReservation());
        btnNewReservation.setOnAction(e -> handleNewReservation());
        btnUpdateAvailability.setOnAction(e->updateAvailabilityButton());
        initLoyaltyPane();

        // tables
        initReservationColumns();
        loadReservations();
        initGuestColumns();
        loadGuests();
        initRoomManagementTable();
        loadRooms();
        initLoyaltyTable();
        loadLoyalty();

        // search filters
        initReservationStatusFilter();
        searchGuestsInput.textProperty().addListener((obs, oldV, newV) -> applyFilters());

        searchReservationsDateStartInput.valueProperty().addListener((obs, o, n) -> applyFilters());
        searchReservationsDateEndInput.valueProperty().addListener((obs, o, n) -> applyFilters());

        choiceBoxReservationStatus.valueProperty().addListener((obs, o, n) -> applyFilters());
        initGuestSearchFilters();
        choiceBoxRoomTypeFilter.getItems().add(null); // "Any" option
        choiceBoxRoomTypeFilter.getItems().addAll(RoomType.values());
        choiceBoxRoomTypeFilter.setValue(null); // default no filter
        choiceBoxRoomStatusFilter.getItems().add(null);
        choiceBoxRoomStatusFilter.getItems().addAll(RoomStatus.values());
        choiceBoxRoomStatusFilter.setValue(null);
        initRoomFilterListeners();
        updateOccupancyPercentage();
        searchLoyaltyPhoneInput.textProperty().addListener((obs, oldV, newV) -> applyLoyaltyFilters());


    }
    @FXML
    private void onOpenCheckout() {
        try {
            Reservation selected = tableReservations.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a reservation first.");
                return;
            }

            labelCheckOutReservationTitle.setText("Checkout Reservation #" + selected.getId());
           // labelCheckOutSubTotal.setText(String.format("$%.2f", selected.getSubtotalEstimate()));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ca/senecapolytechnic/application/apd545project/admin-checkout-view.fxml"));
            loader.setController(this);   // reuse SAME controller instance
            BorderPane checkoutRoot = loader.load();

            Stage modalStage = new Stage();
            modalStage.setTitle("Reservation Checkout");
            modalStage.setScene(new Scene(checkoutRoot));
            modalStage.initModality(Modality.WINDOW_MODAL);

            Stage parentStage = (Stage) btnReservationsCheckout.getScene().getWindow();
            modalStage.initOwner(parentStage);

            modalStage.setResizable(false);
            this.checkoutStage = modalStage;
            // load all the UI elements here!! cant reference them before the fxml is loaded

            modalStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open checkout window.");
        }
    }


    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type, msg);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    // instead of manually showing/hiding the borderpanes for each button on the screen
    // reuse this function and pass in a different argument to determine what to show
    // hide all the other panes
    private void showPage(BorderPane paneToShow) {
        for (BorderPane pane : adminPages) {
            pane.setVisible(false);
            pane.setManaged(false);  // prevents empty spacing
        }

        // only show the specific param pane
        paneToShow.setVisible(true);
        paneToShow.setManaged(true);
    }

    private void deactivateAdmin(AdminUser user) {
        // using state to identify which admin is currently logged in, and log them out
        // setting active to false is considered log out
        EntityManager em = AppConfig.getEntityManager();
        try {
            em.getTransaction().begin();

            AdminUser dbUser = em.find(AdminUser.class, loggedInAdmin.getId());
            dbUser.setActive(false);

            em.getTransaction().commit();

            Logger.info("Admin '{}' logged out and deactivated.", dbUser.getUsername());

        } catch (Exception e) {
            em.getTransaction().rollback();
            Logger.error("Failed to deactivate admin", e);
        } finally {
            em.close();
        }
    }
    private void logOut() {
        // first update the db to set active to false
        deactivateAdmin(loggedInAdmin);
        // clear state
        AuthService.logout();
        // finally redirect to welcome page
        Logger.info("Admin logged out and session cleared");
        try {
            //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ca/senecapolytechnic/application/apd545project/customer-view.fxml"));
            //fxmlLoader.setControllerFactory(injector::getInstance);
            //KioskController controller = injector.getInstance(KioskController.class);
            //fxmlLoader.setController(controller);
            //Parent root = fxmlLoader.load();
            Parent root = guiceLoader.load("/ca/senecapolytechnic/application/apd545project/welcome-view.fxml");
            Stage stage = (Stage) btnAdminLogOut.getScene().getWindow();
            stage.setTitle("Come On Inn!!");

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // loading functions

    // RESERVATIONS (initialize tables, load records from db, apply filters)
    private void initReservationColumns() {
        reservationIdCol.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().getId())
        );

        reservationGuestNameCol.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getGuest().getName()
                )
        );
        //DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        // remove the T00:00 from the string


        reservationCheckInCol.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().getCheckIn().toLocalDate())
        );
        reservationCheckOutCol.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().getCheckOut().toLocalDate())
        );
        reservationAdultsCol.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().getNumAdults())
        );
        reservationChildrenCol.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().getNumChildren())
        );

        reservationStatusCol.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getReservationStatus().name()
                )
        );
    }

    private void loadReservations() {
        //List<Reservation> reservations = reservationRepository.findAll(); // OLD
        reservations = FXCollections.observableArrayList(reservationRepository.findAll());
        filteredReservations = new FilteredList<>(reservations, r -> true);

        tableReservations.setItems(filteredReservations);
        //tableReservations.getItems().setAll(reservations); // OLD
    }
    private void initReservationStatusFilter() {
        choiceBoxReservationStatus.getItems().clear();
        choiceBoxReservationStatus.getItems().add(null);

        choiceBoxReservationStatus.getItems().addAll(ReservationStatus.values());

        // "any" means that no filter is applied
        choiceBoxReservationStatus.setConverter(new StringConverter<ReservationStatus>() {
            @Override
            public String toString(ReservationStatus status) {
                return status == null ? "Any" : status.name();
            }
            @Override
            public ReservationStatus fromString(String s) {
                return "Any".equals(s) ? null : ReservationStatus.valueOf(s);
            }
        });

        choiceBoxReservationStatus.setValue(null); // default "Any"
    }
    private void applyFilters() {

        String guestText = searchGuestsInput.getText() != null
                ? searchGuestsInput.getText().trim().toLowerCase()
                : "";

        LocalDate start = searchReservationsDateStartInput.getValue();
        LocalDate end = searchReservationsDateEndInput.getValue();

        ReservationStatus selectedStatus = choiceBoxReservationStatus.getValue();

        filteredReservations.setPredicate(res -> {

            // searching for guest name
            if (!guestText.isEmpty()) {
                String guestName = res.getGuest().getName().toLowerCase();
                if (!guestName.contains(guestText)) {
                    return false;
                }
            }
            // reservation status
            if (selectedStatus != null && res.getReservationStatus() != selectedStatus) {
                return false;
            }
            // check in date between
            if (start != null) {
                if (res.getCheckIn().toLocalDate().isBefore(start)) {
                    return false;
                }
            }
            if (end != null) {
                if (res.getCheckIn().toLocalDate().isAfter(end)) {
                    return false;
                }
            }
            return true;
        });
    }
    @FXML
    private void handleCancelReservation() {
        Reservation selected = tableReservations.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a reservation to cancel.");
            Logger.info("Couldn't cancel reservation: None specified");
            return;
        }

        if (selected.getReservationStatus() == ReservationStatus.CANCELLED) {
            showAlert(Alert.AlertType.INFORMATION, "Already Cancelled", "This reservation is already cancelled.");
            Logger.info("Couldn't cancel reservation: Already cancelled");
            return;
        }

        /* OLD
        // need to actually update all the rooms of this reservation to be available
        List<ReservationRoom> rrList = reservationRoomRepository.findByReservation(selected.getId());
        // check if currently active
        LocalDateTime now = LocalDateTime.now();
        boolean currentlyActive =
                selected.getCheckIn().isBefore(now) &&
                        selected.getCheckOut().isAfter(now);
        // delete reservation rooms
        for (ReservationRoom rr : rrList) {
            Room room = rr.getRoom();
            if (currentlyActive) {
                room.setRoomStatus(RoomStatus.AVAILABLE);
                roomRepository.save(room);
            }
            reservationRoomRepository.delete(rr);
        }

        selected.setReservationStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(selected);



        loadReservations();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation cancelled successfully.");
        */
         // NEW and taking into account DA

        EntityManager em = AppConfig.getEntityManager();

        Reservation res = reservationRepository.findById(selected.getId());
        res.setReservationStatus(ReservationStatus.CANCELLED);
        em.merge(res);

        List<ReservationRoom> rrs = reservationRoomRepository.findByReservation(res.getId());
        for (ReservationRoom rr : rrs) {
            // optionally update room status if needed
            Room room = em.find(Room.class, rr.getRoom().getId());
            em.remove(em.contains(rr) ? rr : em.merge(rr));
            // recompute occupancy for this room:
            boolean stillOccupied = reservationRoomRepository.existsOverlapForRoom(room.getId(), LocalDate.now(), LocalDate.now().plusDays(1));
            if (!stillOccupied) {
                room.setRoomStatus(RoomStatus.AVAILABLE);
                em.merge(room);
            }
        }
    }
    @FXML
    private void handleNewReservation() {
        try {
            Parent root = guiceLoader.load("/ca/senecapolytechnic/application/apd545project/login-view.fxml");
            Stage stage = (Stage) tableReservations.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("New Reservation");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Unable to open page.");
            Logger.error(e.getMessage());
        }
    }
    // guests
    private void initGuestColumns() {
        guestsIdCol.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().getId())
        );
        guestsNameCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getName()));
        guestsPhoneCol.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().getPhone()));
        guestsEmailCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getEmail()));
        guestsAddressCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getAddress()));
        guestsLoyaltyPtsCol.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().getLoyaltyPoints()));
    }
    private void loadGuests() {
        List<Guest> allGuests = guestRepository.findAll();
        guestList = FXCollections.observableArrayList(allGuests);
        filteredGuests = new FilteredList<>(guestList, g -> true);
        tableGuests.setItems(filteredGuests);
    }
    private void applyGuestFilter() {
        String query = searchGuestsInput2.getText().toLowerCase().trim();
        // empty or filter
        if (query.isEmpty()) {
            filteredGuests.setPredicate(g -> true);
            return;
        }
        RadioButton selected = (RadioButton) guestsSearchFilter.getSelectedToggle();
        if (selected == null) {
            filteredGuests.setPredicate(g -> true);
            return;
        }

        String mode = selected.getText();  // “By Name”, “By Phone”, “By Email”

        filteredGuests.setPredicate(g -> {
            switch (mode) {
                case "By Name":
                    return g.getName() != null &&
                            g.getName().toLowerCase().contains(query);

                case "By Phone":
                    return g.getPhone() != null &&
                            g.getPhone().toLowerCase().contains(query);

                case "By Email":
                    return g.getEmail() != null &&
                            g.getEmail().toLowerCase().contains(query);

                default:
                    return true;
            }
        });
    }
    private void initGuestSearchFilters() {
        searchGuestsInput2.textProperty().addListener((obs, old, val) -> applyGuestFilter());
        guestsSearchFilter.selectedToggleProperty().addListener((obs, old, val) -> applyGuestFilter());
    }

    // rooms
    private void initRoomManagementTable() {
        roomMgmtIdCol.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().getId()));
        roomMgmtRoomNoCol.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().getRoomNumber()));
        roomMgmtTypeCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getRoomType().name())
        );
        roomMgmtBedsCol.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().getBeds()));
        roomMgmtBasePriceCol.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().getBasePrice()));
        /*
        Doesnt take into account the date :(
        roomMgmtStatusCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getRoomStatus().name())
        );*/
        roomMgmtStatusCol.setCellValueFactory(cell -> {
            Room r = cell.getValue();
            RoomStatus display = computeDisplayStatus(r, roomDateFilterInput.getValue() == null ? LocalDate.now() : roomDateFilterInput.getValue());
            return new SimpleStringProperty(display.name());
        });
    }
    private void loadRooms() {
        List<Room> rooms = roomRepository.findAll();
        tableRoomMgmt.setItems(FXCollections.observableArrayList(rooms));
    }
    private void initRoomFilterListeners() {
        btnSearchRoomNoInput.textProperty().addListener((obs, oldV, newV) -> {
            applyRoomFilters();
            updateOccupancyPercentage();
        });
        choiceBoxRoomTypeFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
            applyRoomFilters();
            updateOccupancyPercentage();
        });
        choiceBoxRoomStatusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyRoomFilters());
        roomDateFilterInput.valueProperty().addListener((obs, oldV, newV) -> {
            applyRoomFilters();
            updateOccupancyPercentage();
        });
    }
    private void applyRoomFilters() {
        String roomNoText = btnSearchRoomNoInput.getText().trim();
        RoomType typeFilter = choiceBoxRoomTypeFilter.getValue();
        RoomStatus statusFilter = choiceBoxRoomStatusFilter.getValue();
        List<Room> rooms = roomRepository.findAll();
        // need to account for the date when checking availability
        LocalDate selectedDate =
                (roomDateFilterInput.getValue() == null ? LocalDate.now() : roomDateFilterInput.getValue());
        List<Room> filtered = rooms.stream()
                .filter(room -> {
                    if (!roomNoText.isEmpty()) {
                        try {
                            int num = Integer.parseInt(roomNoText);
                            if (room.getRoomNumber() != num) return false;
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    }
                    if (typeFilter != null && room.getRoomType() != typeFilter)
                        return false;
                    /*
                    if (statusFilter != null && room.getRoomStatus() != statusFilter)
                        return false;*/
                    if (statusFilter != null) {
                        RoomStatus computed =
                                computeDisplayStatus(room, selectedDate);
                        if (computed != statusFilter)
                            return false;
                    }

                    return true;
                })
                .collect(Collectors.toList());

        tableRoomMgmt.setItems(FXCollections.observableArrayList(filtered));
    }
    private void updateAvailabilityButton() {
        btnUpdateAvailability.setOnAction(event -> {
            Room selected = tableRoomMgmt.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Room Selected", "Please select a room from the table.");
                return;
            }

            selected.setRoomStatus(RoomStatus.AVAILABLE);
            roomRepository.save(selected);

            applyRoomFilters();

            showAlert(Alert.AlertType.CONFIRMATION,"Success", "Room availability updated.");
        });
    }
    private RoomStatus computeDisplayStatus(Room room, LocalDate date) {
        // check for date overlaps when figuring out the availability...
        boolean occupied = reservationRoomRepository.existsOverlapForRoom(room.getId(), date, date.plusDays(1));
        return occupied ? RoomStatus.OCCUPIED : RoomStatus.AVAILABLE;
    }
    private void updateOccupancyPercentage() {
        LocalDate date =
                (roomDateFilterInput.getValue() != null)
                        ? roomDateFilterInput.getValue()
                        : LocalDate.now();
        RoomType typeFilter = choiceBoxRoomTypeFilter.getValue();
        // takes into account different room types
        List<Room> rooms = roomRepository.findAll();
        if (typeFilter != null) {
            rooms = rooms.stream()
                    .filter(r -> r.getRoomType() == typeFilter)
                    .collect(Collectors.toList());
        }
        if (rooms.isEmpty()) {
            occupancyPercentage.setText("0.00%");
            return;
        }

        // percentage is dividing the occupied rooms by the total rooms
        long occupiedCount = rooms.stream()
                .filter(r -> computeDisplayStatus(r, date) == RoomStatus.OCCUPIED)
                .count();
        double percentage = (occupiedCount * 100.0) / rooms.size();

        occupancyPercentage.setText(String.format("%.2f%%", percentage));
    }

    // loyalty

    private void initLoyaltyPane() {
        choiceBoxEarningRate.getItems().clear();
        choiceBoxEarningRate.getItems().addAll(
                "50% (0.5x)",
                "100% (1x)",
                "200% (2x)"
        );

        // sync with the loyalty policy constants (earnings, redemption)
        // changing the values here updates the constants
        double rate = LoyaltyPolicy.getInstance().getEarningRate();
        if (rate == 0.5) choiceBoxEarningRate.setValue("50% (0.5x)");
        else if (rate == 2.0) choiceBoxEarningRate.setValue("200% (2x)");
        else choiceBoxEarningRate.setValue("100% (1x)");

        choiceBoxEarningRate.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;

            LoyaltyPolicy policy = LoyaltyPolicy.getInstance();
            switch (newVal.toString()) {
                case "50% (0.5x)":
                    policy.setEarningRate(0.5);
                    break;
                case "200% (2x)":
                    policy.setEarningRate(2.0);
                    break;
                default:
                    policy.setEarningRate(1.0);
            }
        });
        choiceBoxRedemptionCap.getItems().clear();
        choiceBoxRedemptionCap.getItems().addAll(100, 500, 1000, 2000);

        choiceBoxRedemptionCap.setValue(LoyaltyPolicy.getInstance().getRedemptionCap());

        choiceBoxRedemptionCap.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                LoyaltyPolicy.getInstance().setRedemptionCap((Integer) newVal);
            }
        });
    }
    private void applyLoyaltyFilters() {
        String phone = searchLoyaltyPhoneInput.getText().trim();

        List<Guest> guests = guestRepository.findAll(); // or findActiveLoyaltyMembers()

        List<Guest> filtered = guests.stream()
                .filter(g -> phone.isEmpty() || g.getPhone().contains(phone))
                .collect(Collectors.toList());

        tableLoyalty.setItems(FXCollections.observableArrayList(filtered));
    }
    private void initLoyaltyTable() {

        loyaltyIdCol.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().getId()));
        loyaltyGuestCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getName()));
        loyaltyPhoneCol.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().getPhone()));
        loyaltyNumCol.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().getLoyaltyNumber()));
        loyaltyActiveCol.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().getActive()));
        loyaltyPtsCol.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().getLoyaltyPoints()));
    }
    private void loadLoyalty() {
        tableLoyalty.setItems(
                FXCollections.observableArrayList(guestRepository.findAll())
        );
    }



}

