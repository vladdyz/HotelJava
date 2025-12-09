package ca.senecapolytechnic.application.apd545project.controllers;

import ca.senecapolytechnic.application.apd545project.models.Feedback;
import ca.senecapolytechnic.application.apd545project.models.Guest;
import ca.senecapolytechnic.application.apd545project.models.Reservation;
import ca.senecapolytechnic.application.apd545project.repositories.*;
import ca.senecapolytechnic.application.apd545project.utils.GuiceFXMLLoader;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FeedbackController {

    @FXML
    private Slider ratingSlider;
    @FXML
    private ChoiceBox<String> sentimentInput;
    @FXML
    private TextArea commentsInput;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnCancel1;
    @FXML
    private Button btnSubmit;
    @FXML
    private TextField phoneInput;
    @FXML
    private DatePicker checkoutDate;
    @FXML
    private Button btnFeedbackNext;
    @FXML
    private GridPane gridPaneFeedback1;
    @FXML
    private GridPane gridPaneFeedback2;

    @Inject
    private FeedbackRepository feedbackRepository;
    @Inject
    private ReservationRoomRepository reservationRoomRepository;
    @Inject
    private ReservationRepository reservationRepository;
    @Inject
    private GuestRepository guestRepository;
    @Inject
    private BillingRepository billingRepository;
    private Guest guest;
    private Reservation reservation;

    private static Logger Logger= LoggerFactory.getLogger(KioskController.class);
    private final GuiceFXMLLoader guiceLoader;


    @Inject
    public FeedbackController(GuiceFXMLLoader guiceLoader) {
        this.guiceLoader = guiceLoader;
    }


    @FXML
    public void initialize() {
        gridPaneFeedback1.setVisible(true);
        gridPaneFeedback2.setVisible(false);

        btnFeedbackNext.setOnAction(e -> handleFindReservation());
        btnSubmit.setOnAction(e -> handleSubmitFeedback());
        btnCancel.setOnAction(e -> handleCancel());
        btnCancel1.setOnAction(e->handleCancel());
        sentimentInput.setItems(
                FXCollections.observableArrayList("Positive", "Neutral", "Negative")
        );
    }

    private void handleFindReservation() {
        // use the guest's phone number and their checkout date to identify the specific reservation
        // guest probably doesnt know the reservation id or their own id unless explicitly given to them like a code
        // but they would know their phone # and when they checked out
        // this would also be something automated to send a direct link with query params and bypass this realistically
        String phone = phoneInput.getText().trim();
        if (phone.isEmpty() || phone.length() != 10) {
            showAlert(Alert.AlertType.ERROR, "Invalid Phone Number",
                    "Phone number must be 10 digits.");
            return;
        }
        LocalDate checkout = checkoutDate.getValue();
        if (checkout == null) {
            showAlert(Alert.AlertType.ERROR, "Missing Date",
                    "Please specify the checkout date for the reservation.");
            return;
        }
        guest = guestRepository.findByPhone(phone);
        if (guest == null) {
            showAlert(Alert.AlertType.ERROR, "Guest Not Found",
                    "No guest registered with the phone number: " + phone);
            return;
        }
        reservation = reservationRepository.findRyGuestCheckout(phone, checkout);
        if (reservation == null) {
            showAlert(Alert.AlertType.ERROR, "Reservation Not Found",
                    "Could not find reservation for given phone and checkout date.");
            return;
        }
        if (!feedbackRepository.findByReservationId(reservation.getId()).isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Feedback Already Submitted",
                    "Feedback already exists for this reservation.");
            return;
        }

        // validation all good, proceed to next step
        gridPaneFeedback1.setVisible(false);
        gridPaneFeedback2.setVisible(true);
    }

    private void handleSubmitFeedback() {
        int rating = (int) ratingSlider.getValue();
        String sentiment = sentimentInput.getValue();
        String comments = commentsInput.getText().trim();

        if (sentiment.isEmpty() || comments.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Missing Information",
                    "Please fill in all feedback form fields.");
            return;
        }

        Feedback f = new Feedback(
                guest,
                reservation,
                rating,
                comments,
                sentiment,
                LocalDateTime.now()
        );
        Logger.info("Submitting new feedback for reservation ${} by ${}...", reservation.getId(), guest.getName());
        feedbackRepository.save(f);

        showAlert(Alert.AlertType.INFORMATION, "Thank You!",
                "Your feedback has been submitted successfully. Have a great day!");

        try {
            Parent root = guiceLoader.load("/ca/senecapolytechnic/application/apd545project/customer-view.fxml");
            Stage stage = (Stage) btnSubmit.getScene().getWindow();
            stage.setTitle("Customer Kiosk");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.error(e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void handleCancel() {
        try {
            Parent root = guiceLoader.load("/ca/senecapolytechnic/application/apd545project/customer-view.fxml");
            Stage stage = (Stage) btnSubmit.getScene().getWindow();
            stage.setTitle("Customer Kiosk");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.error("Error transitioning from feedback screen : " + e.getMessage());
        }
    }



}
