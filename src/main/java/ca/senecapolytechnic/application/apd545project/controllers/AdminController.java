package ca.senecapolytechnic.application.apd545project.controllers;

import ca.senecapolytechnic.application.apd545project.AppConfig;
import ca.senecapolytechnic.application.apd545project.config.LoyaltyPolicy;
import ca.senecapolytechnic.application.apd545project.models.*;
import ca.senecapolytechnic.application.apd545project.repositories.*;
import ca.senecapolytechnic.application.apd545project.security.AuthService;
import ca.senecapolytechnic.application.apd545project.services.ActivityLogService;
import ca.senecapolytechnic.application.apd545project.services.ReportingService;
import ca.senecapolytechnic.application.apd545project.utils.*;
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
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
    @FXML
    private Button btnExportRoomMgmtCSV;
    @FXML
    private Label labelUrgentWaitlist;

    // section 4 - feedback submissions
    @FXML
    private BorderPane borderPaneFeedback;
    @FXML
    private Button btnExportFeedback;
    @FXML
    private TableView<Feedback> tableFeedback;
    @FXML
    private TableColumn<Feedback, Long> feedbackIdCol;
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
    private TableColumn<Feedback, LocalDate> feedbackCreatedCol;
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
    private Button btnExportAuditLogsCSV;
    @FXML
    private TableView<AuditLog> tableAudit;
    @FXML
    private TableColumn<AuditLog, Long> auditIdCol;
    @FXML
    private TableColumn<AuditLog, LocalDateTime> auditTimestampCol;
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

    // the loyalty history modal
    @FXML
    private Label labelLoyaltyHeader;
    @FXML
    private Label labelLoyaltySubHeader;
    @FXML
    private ListView<Object> loyaltyHistoryListView;
    @FXML
    private Button btnLoyaltyHistoryClose;


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
    private FeedbackRepository feedbackRepository;
    @Inject
    private AuditLogRepository auditLogRepository;
    @Inject
    private BillingRepository billingRepository;
    @Inject
    private ReportingService reportingService;
    @Inject
    private PaymentRepository paymentRepository;
    @Inject
    private ActivityLogService activityLogService;
    @Inject
    private WaitlistObserverImpl waitlistObserver;

    //private final EntityManager em;

    // file exports
    private PdfExporter pdfExporter;
    private CsvExporter csvExporter;



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
        btnAddLoyaltyMember.setOnAction(e->onAddMemberClicked());
        btnCancelLoyaltyMembership.setOnAction(e->onCancelMembershipClicked());
        btnReservationsCheckout.setOnAction(e-> handleCheckout());
        btnViewRedemptionHistory.setOnAction(e->handleLoyaltyHistory());
        btnViewWaitlist.setOnAction(e->handleWaitListView());



        // tables
        initReservationColumns();
        loadReservations();
        initGuestColumns();
        loadGuests();
        initRoomManagementTable();
        loadRooms();
        initLoyaltyTable();
        loadLoyalty();
        initializeFeedbackTable();
        loadFeedback();
        initializeAuditLogTable();
        loadAuditLog();


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
        choiceBoxFeedbackSentiment.setItems(
                FXCollections.observableArrayList(null, "Positive", "Neutral", "Negative")
        );
        choiceBoxFeedbackSentiment.setValue(null);
        choiceBoxFeedbackRating.setItems(
                FXCollections.observableArrayList(null, 1, 2, 3, 4, 5)
        );
        choiceBoxFeedbackRating.setValue(null);
        choiceBoxFeedbackType.setDisable(true); // DEPRECATED
        choiceBoxFeedbackType.setVisible(false);
        initFeedbackFilters();
        choiceBoxEntityType.getItems().addAll(
                "AdminUser", "Billing", "DiscountPolicy", "Feedback",
                "Guest", "LoyaltyPolicy", "Payment", "PricingPolicy",
                "Reservation", "Room", "ServiceAddon", "Waitlist"
        );
        choiceBoxEntityType.getItems().add(0, "All");
        choiceBoxEntityType.getSelectionModel().selectFirst();
        choiceBoxEntityType.getItems().add(0, "All");
        choiceBoxEntityType.getSelectionModel().selectFirst();
        searchActorInput.textProperty().addListener((obs, o, n) -> initAuditLogFilters());
        choiceBoxEntityType.valueProperty().addListener((obs, o, n) -> initAuditLogFilters());
        searchAuditStartDateInput.valueProperty().addListener((obs, o, n) -> initAuditLogFilters());
        searchAuditEndDateInput.valueProperty().addListener((obs, o, n) -> initAuditLogFilters());
        initFeedbackListeners();
        Observable o = Observable.getInstance();
        o.notificationProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                labelUrgentWaitlist.setText(newVal);
                labelUrgentWaitlist.setVisible(true);
            }
        });


        // file exports
        btnExportAuditLogs.setOnAction(e->onExportAuditTXT());
        btnExportAuditLogsCSV.setOnAction(e->onExportAuditCSV());
        btnExportFeedback.setOnAction(e->onExportFeedback());
        btnExportRoomMgmt.setOnAction(e->onExportRoom(true));
        btnExportRoomMgmtCSV.setOnAction(e->onExportRoom(false));
        // observer
        waitlistObserver.checkAndNotify();


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
        btnAddLoyaltyMember.setOnAction(e->onAddMemberClicked());
        btnCancelLoyaltyMembership.setOnAction(e->onCancelMembershipClicked());
        btnReservationsCheckout.setOnAction(e-> handleCheckout());
        btnViewRedemptionHistory.setOnAction(e->handleLoyaltyHistory());
        btnViewWaitlist.setOnAction(e->handleWaitListView());


        // tables
        initReservationColumns();
        loadReservations();
        initGuestColumns();
        loadGuests();
        initRoomManagementTable();
        loadRooms();
        initLoyaltyTable();
        loadLoyalty();
        initializeFeedbackTable();
        loadFeedback();
        initializeAuditLogTable();
        loadAuditLog();


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
        choiceBoxFeedbackSentiment.setItems(
                FXCollections.observableArrayList(null, "Positive", "Neutral", "Negative")
        );
        choiceBoxFeedbackSentiment.setValue(null);
        choiceBoxFeedbackRating.setItems(
                FXCollections.observableArrayList(null, 1, 2, 3, 4, 5)
        );
        choiceBoxFeedbackRating.setValue(null);
        choiceBoxFeedbackType.setDisable(true); // DEPRECATED
        choiceBoxFeedbackType.setVisible(false);
        initFeedbackFilters();
        choiceBoxEntityType.getItems().addAll(
                "AdminUser", "Billing", "DiscountPolicy", "Feedback",
                "Guest", "LoyaltyPolicy", "Payment", "PricingPolicy",
                "Reservation", "Room", "ServiceAddon", "Waitlist"
        );
        choiceBoxEntityType.getItems().add(0, "All");
        choiceBoxEntityType.getSelectionModel().selectFirst();
        searchActorInput.textProperty().addListener((obs, o, n) -> initAuditLogFilters());
        choiceBoxEntityType.valueProperty().addListener((obs, o, n) -> initAuditLogFilters());
        searchAuditStartDateInput.valueProperty().addListener((obs, o, n) -> initAuditLogFilters());
        searchAuditEndDateInput.valueProperty().addListener((obs, o, n) -> initAuditLogFilters());
        initFeedbackListeners();
        Observable o = Observable.getInstance();
        o.notificationProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                labelUrgentWaitlist.setText(newVal);
                labelUrgentWaitlist.setVisible(true);
            }
        });

        // file exports
        btnExportAuditLogs.setOnAction(e->onExportAuditTXT());
        btnExportAuditLogsCSV.setOnAction(e->onExportAuditCSV());
        btnExportFeedback.setOnAction(e->onExportFeedback());
        btnExportRoomMgmt.setOnAction(e->onExportRoom(true));
        btnExportRoomMgmtCSV.setOnAction(e->onExportRoom(false));
        // observer
        waitlistObserver.checkAndNotify();


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
        // create an audit log of the event
        activityLogService.log(loggedInAdmin.getUsername() + " (" + loggedInAdmin.getRole() + ")",
                "LOGOUT", "AdminUser", loggedInAdmin.getId().intValue(),
                "This admin has logged out");

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

    // RESERVATIONS (init tables, load records from db, apply filters)
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
        try {
            em.getTransaction().begin();

            //Reservation res = reservationRepository.findById(selected.getId());
            Reservation res = em.find(Reservation.class, selected.getId());
            res.setReservationStatus(ReservationStatus.CANCELLED);
            em.merge(res);

            /*
            List<ReservationRoom> rrs = reservationRoomRepository.findByReservation(res.getId());

            for (ReservationRoom rr : rrs) {
                Room room = em.find(Room.class, rr.getRoom().getId());
                em.remove(em.contains(rr) ? rr : em.merge(rr));
                boolean stillOccupied = reservationRoomRepository.existsOverlapForRoom(room.getId(), LocalDate.now(), LocalDate.now().plusDays(1));
                if (!stillOccupied) {
                    room.setRoomStatus(RoomStatus.AVAILABLE);
                    em.merge(room);
                }
            }
               */
            List<ReservationRoom> links = em.createQuery(
                            "SELECT rr FROM ReservationRoom rr WHERE rr.reservation.id = :id",
                            ReservationRoom.class)
                    .setParameter("id", res.getId())
                    .getResultList();
            for (ReservationRoom rr : links) {

                Room room = rr.getRoom(); // already managed

                // Remove the link
                em.remove(rr);

                // Recompute if the room is still occupied on ANY overlapping reservation
                boolean occupied = reservationRoomRepository
                        .existsOverlapForRoom(room.getId(), LocalDate.now(), LocalDate.now().plusDays(1));

                if (!occupied) {
                    room.setRoomStatus(RoomStatus.AVAILABLE);
                    em.merge(room);
                }
            }

            em.getTransaction().commit();
            // this should generate an audit log
            activityLogService.log(loggedInAdmin.getUsername() + " (" + loggedInAdmin.getRole() + ")",
                    "CANCELLED_RESERVATION", "Reservation", res.getId().intValue(),
                    "Reservation " + res.getId() + " cancelled by this admin");
            loadAuditLog();
            waitlistObserver.checkAndNotify();


            showAlert(Alert.AlertType.CONFIRMATION, "Success", "Reservation has been cancelled.");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
        loadReservations();
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
            else if (selected.getRoomStatus() == RoomStatus.AVAILABLE) {
                selected.setRoomStatus(RoomStatus.MAINTENANCE);
            }
            else if (selected.getRoomStatus() == RoomStatus.MAINTENANCE) {
                selected.setRoomStatus(RoomStatus.AVAILABLE);
            }

            roomRepository.update(selected);

            applyRoomFilters();
            loadRooms();
            Logger.info("UPDATED Room Availability of Room" + selected.getRoomNumber() + " to " + selected.getRoomStatus());


            // create audit log
            activityLogService.log(loggedInAdmin.getUsername() + " (" + loggedInAdmin.getRole() + ")",
                    "UPDATED_ROOM_AVAILABILITY", "Room", selected.getId().intValue(),
                    "Room " + selected.getRoomNumber() + " availability changed to " + selected.getRoomStatus());
            loadAuditLog();


            showAlert(Alert.AlertType.CONFIRMATION,"Success", "Room availability updated.");
        });
    }
    private RoomStatus computeDisplayStatus(Room room, LocalDate date) {
        // check for date overlaps when figuring out the availability...
        boolean occupied = reservationRoomRepository.existsOverlapForRoom(room.getId(), date, date.plusDays(1));
        boolean maintenance = (room.getRoomStatus() == RoomStatus.MAINTENANCE);
        //return occupied ? RoomStatus.OCCUPIED : RoomStatus.AVAILABLE;
        return occupied ? RoomStatus.OCCUPIED : (maintenance ? RoomStatus.MAINTENANCE : RoomStatus.AVAILABLE);
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
            activityLogService.log(loggedInAdmin.getUsername() + " (" + loggedInAdmin.getRole() + ")",
                    "UPDATED_EARNING_RATE", "LoyaltyPolicy", 0,
                    "Changed loyalty point earning rate to " + policy.getEarningRate());
            loadAuditLog();

        });
        choiceBoxRedemptionCap.getItems().clear();
        choiceBoxRedemptionCap.getItems().addAll(100, 500, 1000, 2000);

        choiceBoxRedemptionCap.setValue(LoyaltyPolicy.getInstance().getRedemptionCap());

        choiceBoxRedemptionCap.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                LoyaltyPolicy.getInstance().setRedemptionCap((Integer) newVal);
                activityLogService.log(loggedInAdmin.getUsername() + " (" + loggedInAdmin.getRole() + ")",
                        "UPDATED_REDEMPTION_CAP", "LoyaltyPolicy", 0,
                        "Changed loyalty point redemption cap to " + LoyaltyPolicy.getInstance().getRedemptionCap());
                loadAuditLog();

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
    @FXML
    private void onAddMemberClicked() {
        Guest selected = tableLoyalty.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING,"No Selection", "Please select a guest from the table.");
            return;
        }
        if (Boolean.TRUE.equals(selected.getActive())) {
            showAlert(Alert.AlertType.WARNING,"Already Active", "This guest is already a loyalty member.");
            return;
        }

        // assign loyalty number if missing - moved to KioskController
        /*
        if (selected.getLoyaltyNumber() == null) {
            loyaltyService.assignLoyaltyNumber(selected);
        }
        */

        selected.setActive(true);
        guestRepository.save(selected);
        // log this
        activityLogService.log(loggedInAdmin.getUsername() + " (" + loggedInAdmin.getRole() + ")",
                "UPDATED_GUEST_LOYALTY", "Guest", selected.getId().intValue(),
                "Activated loyalty membership of guest:  " + selected.getName());

        loadAuditLog();
        loadLoyalty();
    }
    @FXML
    private void onCancelMembershipClicked() {
        Guest selected = tableLoyalty.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a guest to cancel membership.");
            return;
        }

        if (!Boolean.TRUE.equals(selected.getActive())) {
            showAlert(Alert.AlertType.WARNING,"Not Active", "This guest is not currently an active loyalty member.");
            return;
        }

        selected.setActive(false);
        guestRepository.save(selected);
        // log this
        activityLogService.log(loggedInAdmin.getUsername() + " (" + loggedInAdmin.getRole() + ")",
                "UPDATED_GUEST_LOYALTY", "Guest", selected.getId().intValue(),
                "Cancelled loyalty membership of guest:  " + selected.getName());
        loadAuditLog();
        loadLoyalty();
    }

    private void initializeFeedbackTable() {

        feedbackIdCol.setCellValueFactory(cell ->
                new SimpleObjectProperty(cell.getValue().getId()));

        feedbackGuestCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getGuest().getName()));

        feedbackReservationIdCol.setCellValueFactory(cell ->
                new SimpleObjectProperty(cell.getValue().getReservation().getId().intValue()));

        feedbackRatingCol.setCellValueFactory(cell ->
                new SimpleObjectProperty(cell.getValue().getRating()));

        feedbackCommentsCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getComments()));

        feedbackSentimentCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getSentimentTag()));

        feedbackCreatedCol.setCellValueFactory(cell ->
                new SimpleObjectProperty(
                        cell.getValue().getCreatedAt().toLocalDate().toString()
                ));
    }
    private void loadFeedback() {
        tableFeedback.setItems(FXCollections.observableArrayList(feedbackRepository.findAll()));
    }
    private void initFeedbackFilters() {

        String guestSearch = searchGuestInput3.getText().trim().toLowerCase();
        Integer ratingFilter = (Integer) choiceBoxFeedbackRating.getValue();
        String sentimentFilter = (String) choiceBoxFeedbackSentiment.getValue();

        LocalDate startDate = searchFeedbackDateStartInput.getValue();
        LocalDate endDate = searchFeedbackDateEndInput.getValue();
        List<Feedback> allFeedback = feedbackRepository.findAll();

        List<Feedback> filtered = allFeedback.stream()
                .filter(f -> {
                    if (!guestSearch.isEmpty()) {
                        String guestName = f.getGuest().getName().toLowerCase();
                        if (!guestName.contains(guestSearch)) return false;
                    }

                    if (ratingFilter != null && f.getRating() != ratingFilter)
                        return false;

                    if (sentimentFilter != null && !sentimentFilter.equalsIgnoreCase(f.getSentimentTag()))
                        return false;

                    LocalDate createdDate = f.getCreatedAt().toLocalDate();

                    if (startDate != null && endDate != null) {
                        if (createdDate.isBefore(startDate) || createdDate.isAfter(endDate))
                            return false;
                    } else if (startDate != null) {
                        if (createdDate.isBefore(startDate)) return false;
                    } else if (endDate != null) {
                        if (createdDate.isAfter(endDate)) return false;
                    }

                    return true;
                })
                .collect(Collectors.toList());

        tableFeedback.setItems(FXCollections.observableArrayList(filtered));
    }
    private void initFeedbackListeners() {

        searchGuestInput3.textProperty().addListener((obs, o, n) -> initFeedbackFilters());
        choiceBoxFeedbackRating.valueProperty().addListener((obs, o, n) -> initFeedbackFilters());
        choiceBoxFeedbackSentiment.valueProperty().addListener((obs, o, n) -> initFeedbackFilters());

        searchFeedbackDateStartInput.valueProperty().addListener((obs, o, n) -> initFeedbackFilters());
        searchFeedbackDateEndInput.valueProperty().addListener((obs, o, n) -> initFeedbackFilters());
    }

    // audit logs
    private void initializeAuditLogTable() {
        auditIdCol.setCellValueFactory(cell ->
                new SimpleObjectProperty(cell.getValue().getId()));
        auditTimestampCol.setCellValueFactory(cell ->
                new SimpleObjectProperty(cell.getValue().getTimestamp()));
        auditActorCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getActor()));
        auditActionCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getAction()));
        auditEntityTypeCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getEntityType()));
        auditEntityIdCol.setCellValueFactory(cell ->
                new SimpleObjectProperty(cell.getValue().getEntityId()));
        auditMessageCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getMessage()));

    }
    private void initAuditLogFilters() {
       // List<AuditLog> list = auditLogRepository.findAll();
       // List<AuditLog> filtered = new ArrayList<>(list);
        List<AuditLog> filtered = auditLogRepository.findAll();
        LocalDate start = searchAuditStartDateInput.getValue();
        LocalDate end = searchAuditEndDateInput.getValue();

        String actorSearch = searchActorInput.getText().trim().toLowerCase();
        if (!actorSearch.isEmpty()) {
            filtered = filtered.stream()
                    .filter(a -> a.getActor().toLowerCase().contains(actorSearch))
                    .collect(Collectors.toList());
        }
        String entityType = (String) choiceBoxEntityType.getValue();
        if (!"All".equals(entityType)) {
            filtered = filtered.stream()
                    .filter(a -> a.getEntityType().equals(entityType))
                    .collect(Collectors.toList());
        }
        if (start != null) {
            LocalDateTime startDate = start.atStartOfDay();
            filtered = filtered.stream()
                    .filter(a -> !a.getTimestamp().isBefore(startDate))
                    .collect(Collectors.toList());
        }
        if (end != null) {
            LocalDateTime endDate = end.plusDays(1).atStartOfDay();
            filtered = filtered.stream()
                    .filter(a -> a.getTimestamp().isBefore(endDate))
                    .collect(Collectors.toList());
        }

        tableAudit.getItems().setAll(filtered);
    }
    private void loadAuditLog() {
        tableAudit.setItems(FXCollections.observableArrayList(auditLogRepository.findAll()));
    }

    private void handleCheckout() {
        Reservation selected = tableReservations.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a reservation to check out.");
            Logger.info("Couldn't checkout reservation: None specified");
            return;
        }
        // need to load the billing object that corresponds to the selected reservation
        // there should always be one (they are created together) but check just in case
        Billing billing = billingRepository.findByReservationId(selected.getId());
        if (billing == null) {
            showAlert(Alert.AlertType.ERROR, "Missing Billing",
                    "This reservation does not have an associated billing record.");
            Logger.error("Could not checkout reservation: No billing record found.");
            return;
        }
        // instead of using controller params, use the service
        reportingService.setSelectedReservation(selected);
        reportingService.setSelectedBilling(billing);

        if (selected.getReservationStatus() == ReservationStatus.CHECKED_OUT) {
            //showAlert(Alert.AlertType.INFORMATION, "Already Checked Out", "This reservation is already checked out. Would you like to view its payment history?");
            Logger.info("Couldn't checkout reservation: Already checked out");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Already Checked Out");
            alert.setHeaderText("This reservation has already been checked out.");
            alert.setContentText("Would you like to view its payment history?");
            ButtonType yes = new ButtonType("Yes");
            ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(yes, no);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == yes) {
                Logger.info("Viewing payment history");
                try {
                    // FXMLLoader loader = new FXMLLoader(
                    //        getClass().getResource("/ca/senecapolytechnic/application/apd545project/admin-checkout-view.fxml"));
                    // Parent root = loader.load();
                    // works now with services
                    Parent root = guiceLoader.load(
                            "/ca/senecapolytechnic/application/apd545project/admin-checkout-view.fxml"
                    );



                    //CheckoutController controller = loader.getController();
                    //controller.init(selected, billing, this.loggedInAdmin);

                    Stage stage = (Stage) btnReservationsCheckout.getScene().getWindow();
                    stage.setTitle("Checkout – Reservation #" + selected.getId());
                    stage.setScene(new Scene(root));
                    //stage.initModality(Modality.APPLICATION_MODAL);
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load checkout screen.");
                }

            }
            Logger.info("Checkout prevented. Reservation already checked out.");
            return;
        }

        // transition scene, need to specify the billing for the next controller to handle it
        try {
           // FXMLLoader loader = new FXMLLoader(
           //        getClass().getResource("/ca/senecapolytechnic/application/apd545project/admin-checkout-view.fxml"));
           // Parent root = loader.load();
            // works now with services
            Parent root = guiceLoader.load(
                   "/ca/senecapolytechnic/application/apd545project/admin-checkout-view.fxml"
            );



            //CheckoutController controller = loader.getController();
            //controller.init(selected, billing, this.loggedInAdmin);

            Stage stage = (Stage) btnReservationsCheckout.getScene().getWindow();
            stage.setTitle("Checkout – Reservation #" + selected.getId());
            stage.setScene(new Scene(root));
            //stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load checkout screen.");
        }

    }

    // loyalty points earning history modal...
    private void handleLoyaltyHistory() {
        Guest selected = tableLoyalty.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a guest to view loyalty history.");
            Logger.info("Couldn't view loyalty history: No guest specified");
            return;
        }
        try {
            reportingService.setSelectedGuest(selected);
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ca/senecapolytechnic/application/apd545project/loyalty-history-view.fxml")
            );
            loader.setController(this);
            Parent root = loader.load();

            Stage modal = new Stage();
            modal.setTitle("Loyalty History");
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setScene(new Scene(root));

            populateLoyaltyHistory();

            btnLoyaltyHistoryClose.setOnAction(e -> modal.close());

            modal.show();
        } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load loyalty history.");
            }

    }

    private void populateLoyaltyHistory() {
        Guest selected = reportingService.getSelectedGuest();
        labelLoyaltyHeader.setText("Loyalty Points History for Guest: " + selected.getName());

        labelLoyaltySubHeader.setText("Loyalty Points Balance: " + selected.getLoyaltyPoints() +
                " - Account Number: " + selected.getLoyaltyNumber());

        // listview stuff - complicated !
        // reservations are connected to guests, and to billings, which are connected to payments
        // since we know who the guest is from the table, we need the reservations for the rest

        LocalDateTime date;
        String type; // "EARNED" or "REDEEMED"
        double amount;
        // EDIT: This works better as a class since we can sort the result (cant sort the string...)
        class LoyaltyHistoryEntry {
            private LocalDateTime date;
            private String type; // "EARNED" or "REDEEMED"
            private double amount;

            public LoyaltyHistoryEntry(LocalDateTime date, String type, double amount) {
                this.date = date;
                this.type = type;
                this.amount = amount;
            }
            public LocalDateTime getDate() { return date; }
            @Override
            public String toString() {
                return date.toLocalDate() + " - " + type + " - " +
                        (type.equals("REDEEMED") ? "-" : "+") +
                        (int) amount;
            }
        }
        //List<String> entries = new ArrayList<>();
        List<LoyaltyHistoryEntry> entries = new ArrayList<>();
        List<Reservation> reservations =
                reservationRepository.findByGuestId(selected.getId());

        // each reservetation should have a billing
        for (Reservation r : reservations) {
            Billing billing = billingRepository.findByReservationId(r.getId());

            // calc loyalty points EARNED
            LocalDateTime earnedDate = r.getCheckIn(); // your assumption
            double earnedAmount = billing.getTotalAmount();
            //entries.add(earnedDate.toString() + " - EARNED - +" + earnedAmount);
            entries.add(new LoyaltyHistoryEntry(
                    earnedDate,
                    "EARNED",
                    earnedAmount
            ));

            // now get all the spending/redemptions
            List<Payment> payments =
                    paymentRepository.findByBillingId(billing.getId());
            // only payments where the guest pays with loyalty points
            for (Payment p : payments) {
                if (p.getMethod() == PaymentMethod.POINTS) {
                    //entries.add(p.getCreatedAt().toString() + " - REDEEMED - -" + p.getAmount());
                    entries.add(new LoyaltyHistoryEntry(
                            p.getCreatedAt(),
                            "REDEEMED",
                            p.getAmount()
                    ));
                }
            }
            entries.sort(Comparator.comparing(LoyaltyHistoryEntry::getDate));
           // loyaltyHistoryListView.getItems().setAll(String.valueOf(entries));
            //loyaltyHistoryListView.setItems(entries);
            loyaltyHistoryListView.getItems().setAll(entries);
        }

    }
    @FXML
    private void onExportAuditTXT() {
        try {
            csvExporter.exportAuditLogsTXT(tableAudit.getItems());
            showAlert(Alert.AlertType.CONFIRMATION, "Export Success", "Audit Logs successfully exported to /export folder");
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR,"Export Failed", "Exporting audit logs failed: " + ex.getMessage());
        }
    }

    @FXML
    private void onExportAuditCSV() {
        try {
            csvExporter.exportAuditLogsCSV(tableAudit.getItems());
            showAlert(Alert.AlertType.CONFIRMATION, "Export Success", "Audit Logs successfully exported to /export folder");
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR,"Export Failed", "Exporting audit logs failed: " + ex.getMessage());
        }
    }

    @FXML
    private void onExportFeedback() {
        try {
            csvExporter.exportFeedbackCSV(tableFeedback.getItems());
            showAlert(Alert.AlertType.CONFIRMATION, "Export Success", "Feedback successfully exported to /export folder");
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR,"Export Failed", "Exporting feedback failed: " + ex.getMessage());
        }
    }

    @FXML
    private void onExportRoom(Boolean pdf) {
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
            showAlert(Alert.AlertType.ERROR,"Export Failed", "Exporting rooms failed: No rooms to export");
            return;
        }

        // percentage is dividing the occupied rooms by the total rooms
        long occupiedCount = rooms.stream()
                .filter(r -> computeDisplayStatus(r, date) == RoomStatus.OCCUPIED)
                .count();
       double occupancy = (occupiedCount * 100.0) / rooms.size();
       String selected = (roomDateFilterInput.getValue() == null ? LocalDate.now() : roomDateFilterInput.getValue()).toString();
       //selected.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);

        // i merged the pdf and csv into one function to reduce code duplicity
        // check the param passed in to determine which file extension to export to

        if (pdf) {
            // passing in the rooms table data, the selected date filter value, and the calculated occupancy percentage
            try {
                pdfExporter.exportRoomPDF(tableRoomMgmt.getItems(),
                        selected,
                        occupancy
                );
                showAlert(Alert.AlertType.CONFIRMATION, "Export Success", "Room Occupancy successfully exported to /export folder");

            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Export Failed", "Exporting rooms failed: " + ex.getMessage());
            }
        }
        else {
            try {
                csvExporter.exportOccupancy(tableRoomMgmt.getItems(),
                        selected,
                        occupancy
                );
                showAlert(Alert.AlertType.CONFIRMATION, "Export Success", "Room Occupancy successfully exported to /export folder");

            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Export Failed", "Exporting rooms failed: " + ex.getMessage());
            }
        }
    }

    @FXML
    private void handleWaitListView() {
        try {
            labelUrgentWaitlist.setText("");
            labelUrgentWaitlist.setVisible(false);
            Parent root = guiceLoader.load(
                    "/ca/senecapolytechnic/application/apd545project/waitlist-view.fxml"
            );

            Stage stage = (Stage) btnViewWaitlist.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error opening view",  "Unable to open admin interface.");
        }

    }




}

