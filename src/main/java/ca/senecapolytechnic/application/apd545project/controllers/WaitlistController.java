package ca.senecapolytechnic.application.apd545project.controllers;

import ca.senecapolytechnic.application.apd545project.models.AdminUser;
import ca.senecapolytechnic.application.apd545project.models.Guest;
import ca.senecapolytechnic.application.apd545project.models.RoomType;
import ca.senecapolytechnic.application.apd545project.models.Waitlist;
import ca.senecapolytechnic.application.apd545project.repositories.GuestRepository;
import ca.senecapolytechnic.application.apd545project.repositories.WaitlistRepository;
import ca.senecapolytechnic.application.apd545project.security.AuthService;
import ca.senecapolytechnic.application.apd545project.services.ActivityLogService;
import ca.senecapolytechnic.application.apd545project.services.WaitlistService;
import ca.senecapolytechnic.application.apd545project.utils.GuiceFXMLLoader;
import ca.senecapolytechnic.application.apd545project.utils.WaitlistObserverImpl;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

import static ca.senecapolytechnic.application.apd545project.security.AuthService.getCurrentAdmin;

public class WaitlistController {




    @FXML
    private ListView<Waitlist> waitlistView;
    @FXML
    private Button btnConvertWaitlist;
    @FXML
    private Button btnCancelWaitlist;
    @FXML
    private Button btnWaitlistClose;
    @FXML
    private Button btnCreateListView;
    @FXML
    private Button btnViewWaitlistList;
    // create tab elements
    @FXML
    private ChoiceBox<Guest> choiceBoxGuests;
    @FXML
    private ChoiceBox<RoomType> roomTypeChoiceBox;
    @FXML
    private DatePicker waitlistCheckinDate;
    @FXML
    private DatePicker waitlistCheckoutDate;
    @FXML
    private Button btnWaitlistConfirm;
    @FXML
    private GridPane gridPaneCreateWaitlist;

    @Inject
    WaitlistRepository waitlistRepository;
    @Inject
    GuestRepository guestRepository;
    @Inject
    private ActivityLogService activityLogService;
    @Inject
    private AuthService authService;
    @Inject
    private WaitlistService waitlistService;
    @Inject
    private WaitlistObserverImpl waitlistObserver;




    private final GuiceFXMLLoader guiceLoader;
    private static Logger Logger = LoggerFactory.getLogger(WaitlistController.class);

    @Inject
    public WaitlistController(GuiceFXMLLoader guiceLoader) {
        this.guiceLoader = guiceLoader;
    }
    private AdminUser loggedInAdmin = getCurrentAdmin();

    @FXML
    public void initialize() {
        loadWaitlist();
        loadChoiceBoxes();
        setupButtons();
        gridPaneCreateWaitlist.setVisible(false);
        waitlistView.setVisible(true);
    }

    private void loadWaitlist() {
        List<Waitlist> items = waitlistRepository.findAll();
        waitlistView.getItems().setAll(items);

        // waitlist display format in the listview
        waitlistView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Waitlist w, boolean empty) {
                super.updateItem(w, empty);
                if (empty || w == null) {
                    setText(null);
                } else {
                    setText(w.getGuest().getName()
                            + " (" + w.getGuest().getPhone() + ") "
                            + " | " + w.getRequestedType()
                            + " | " + w.getDateRangeStart()
                            + " → " + w.getDateRangeEnd());
                }
            }
        });
    }
    private void loadChoiceBoxes() {
        // Guests
        choiceBoxGuests.getItems().setAll(guestRepository.findAll());
        choiceBoxGuests.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Guest g) {
                return (g == null) ? "" : g.getName() + " / " + g.getPhone();
            }

            @Override
            public Guest fromString(String s) { return null; }
        });
        roomTypeChoiceBox.getItems().setAll(RoomType.SINGLE, RoomType.DOUBLE, RoomType.DELUXE, RoomType.PENTHOUSE);
    }
    private void setupButtons() {
        // toggle the stackpane controls
        btnViewWaitlistList.setOnAction(e -> {
            gridPaneCreateWaitlist.setVisible(false);
            waitlistView.setVisible(true);
        });
        btnCreateListView.setOnAction(e -> {
            gridPaneCreateWaitlist.setVisible(true);
            waitlistView.setVisible(false);
        });
        btnWaitlistConfirm.setOnAction(e -> saveNewWaitlist());

        btnWaitlistClose.setOnAction(e -> backtoAdmin());

        btnCancelWaitlist.setOnAction(e -> cancelSelectedWaitlist());

        btnConvertWaitlist.setOnAction(e -> convertSelectedWaitlist());
    }
    private void saveNewWaitlist() {

        Guest g = choiceBoxGuests.getValue();
        RoomType rt = roomTypeChoiceBox.getValue();
        LocalDate start = waitlistCheckinDate.getValue();
        LocalDate end = waitlistCheckoutDate.getValue();

        if (g == null || rt == null || start == null || end == null) {
            showAlert("All fields must be filled.");
            return;
        }

        if (end.isBefore(start)) {
            showAlert("Checkout date cannot be before check-in date.");
            return;
        }

        Waitlist w = new Waitlist();
        w.setGuest(g);
        w.setRequestedType(rt);
        w.setDateRangeStart(start.atStartOfDay());
        w.setDateRangeEnd(end.atStartOfDay());
        w.setStatus("PENDING");

        waitlistRepository.save(w);
        loadWaitlist();
        // logs
        showAlert("Waitlist created successfully.");
        Logger.info("Saved new waitlist item for: " + w.getGuest().getName() + " requesting " + w.getRequestedType());
        activityLogService.log(loggedInAdmin.getUsername() + " (" + loggedInAdmin.getRole() + ")",
                "CREATED_WAITLIST", "Waitlist", w.getId().intValue(),
                "Created a new waitlist item for guest: " + w.getGuest().getName() + " requesting " + w.getRequestedType());
        gridPaneCreateWaitlist.setVisible(false);
        waitlistView.setVisible(true);
    }

    private void cancelSelectedWaitlist() {
        Waitlist w = waitlistView.getSelectionModel().getSelectedItem();
        if (w == null) {
            showAlert("Select a waitlist entry to cancel.");
            return;
        }
        // logs
        Logger.info("Deleting waitlist item for: " + w.getGuest().getName() + " requesting " + w.getRequestedType());
        waitlistRepository.delete(w.getId());
        showAlert("Waitlist cancelled successfully.");
        activityLogService.log(loggedInAdmin.getUsername() + " (" + loggedInAdmin.getRole() + ")",
                "REMOVED_WAITLIST", "Waitlist", w.getId().intValue(),
                "Removed waitlist item for guest: " + w.getGuest().getName() + " requesting " + w.getRequestedType());
        loadWaitlist();
    }
    private void convertSelectedWaitlist() {
        Waitlist w = waitlistView.getSelectionModel().getSelectedItem();
        if (w == null) {
            showAlert("Select a waitlist entry to convert.");
            return;
        }
        // validation + confirmation (you can use utils by referencing them directly without import!!)
        LocalDate start = w.getDateRangeStart().toLocalDate();
        LocalDate end = w.getDateRangeEnd().toLocalDate();
        try {
            ca.senecapolytechnic.application.apd545project.utils.Validator.validateDates(start, end);
        } catch (IllegalArgumentException ex) {
            showAlert("Invalid dates on waitlist entry: " + ex.getMessage());
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Convert Waitlist");
        confirm.setHeaderText("Convert waitlist entry to reservation");
        confirm.setContentText("Convert waitlist for " + w.getGuest().getName()
                + " (" + w.getRequestedType() + ") from "
                + w.getDateRangeStart().toLocalDate() + " to " + w.getDateRangeEnd().toLocalDate()
                + " into a reservation?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            // this does the heavy lifting (creates Reservation + ReservationRoom + billing etc.)
            waitlistService.convertWaitlistToReservation(w);
            w.setStatus("CONVERTED");
            waitlistRepository.save(w);

            // log this event
            String actor = loggedInAdmin != null ? loggedInAdmin.getUsername() + " (" + loggedInAdmin.getRole() + ")" : "SYSTEM";
            activityLogService.log(actor,
                    "CONVERT_WAITLIST",
                    "Waitlist",
                    w.getId().intValue(),
                    "Converted waitlist entry for guest " + w.getGuest().getName()
                            + " to reservation for " + w.getRequestedType()
                            + " from " + w.getDateRangeStart().toLocalDate()
                            + " to " + w.getDateRangeEnd().toLocalDate());

            showAlert("Waitlist converted to reservation successfully.");
            // need to actually delete the waitlist item too
            waitlistRepository.delete(w.getId());
            // after converting to a reservation, it might impact room availability
            waitlistObserver.checkAndNotify();

            loadWaitlist();
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Failed to convert waitlist: " + ex.getMessage());
            Logger.error("Error occurred when trying to convert waitlist to reservation: " + ex.getMessage());
        }
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.showAndWait();
    }

    private void backtoAdmin() {
        try {
            Parent root = guiceLoader.load(
                    "/ca/senecapolytechnic/application/apd545project/admin-view.fxml"
            );

            Stage stage = (Stage) btnWaitlistClose.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert( "Unable to open admin interface.");
        }
    }




}
