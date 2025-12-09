package ca.senecapolytechnic.application.apd545project.controllers;

import ca.senecapolytechnic.application.apd545project.AppConfig;
import ca.senecapolytechnic.application.apd545project.config.DiscountPolicy;
import ca.senecapolytechnic.application.apd545project.config.LoyaltyPolicy;
import ca.senecapolytechnic.application.apd545project.models.*;
import ca.senecapolytechnic.application.apd545project.repositories.*;
import ca.senecapolytechnic.application.apd545project.security.AuthService;
import ca.senecapolytechnic.application.apd545project.services.ActivityLogService;
import ca.senecapolytechnic.application.apd545project.services.ReportingService;
import ca.senecapolytechnic.application.apd545project.utils.GuiceFXMLLoader;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CheckoutController
{
    // I moved the checkout modal to its own controller because its parent controller already does too much
    // this cleans up the code a little because there is also a lot going on here
    // accessed from the admin view reservations pane

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
    private ChoiceBox<Integer> choiceBoxApplyDiscountRate;
    @FXML
    private Label loyaltyPtsRedeemInput;
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

    private Logger Logger= LoggerFactory.getLogger(CheckoutController.class);
    private final GuiceFXMLLoader guiceLoader;

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
    private PaymentRepository paymentRepository;
    @Inject
    private DiscountPolicy discountPolicy;
    @Inject
    private LoyaltyPolicy loyaltyPolicy;

    // -----------------------------------
    // payment modal

    @FXML
    private Label labelPaymentHeader;
    @FXML
    private Label labelPaymentSubHeader;
    @FXML
    private ListView<String> paymentHistoryListView;
    @FXML
    private Button btnPaymentHistoryClose;

    @Inject
    private ReportingService reportingService;

    @Inject
    private ActivityLogService activityLogService;

    private Reservation reservation;
    private Billing billing;
    private AdminUser adminUser;


    @Inject
    public CheckoutController(GuiceFXMLLoader loader) {
        this.guiceLoader = loader;

        System.out.println("Checkout Controller constructor called");

    }


    // originally used params : init(Reservation reservation, Billing billing, AdminUser adminUser)
    // now services keep track of these instead

    @FXML
    public void initialize() {
        this.reservation = reportingService.getSelectedReservation();
        this.billing = reportingService.getSelectedBilling();
        this.adminUser = AuthService.getCurrentAdmin();

        System.out.println("Constructor reportingService=" + reportingService);
        System.out.println("Constructor reservation=" + reportingService.getSelectedReservation());
        System.out.println("Constructor billing=" + reportingService.getSelectedBilling());
        //         reportingService.clearSelection(); // clean-up
        loadData();
        loadListeners();
        choiceBoxCheckOutPaymentType.setItems(FXCollections.observableArrayList(PaymentMethod.values()));
        choiceBoxCheckOutPaymentStatus.setItems(FXCollections.observableArrayList("UNPAID", "PARTIAL", "PAID"));
        int max = discountPolicy.maxDiscountForRole(adminUser.getRole());
        // discount rates applied in multiples of 5 up to the limit of the current admin (15/30)
        List<Integer> options = new ArrayList<>();
        for (int p = 0; p <= max; p += 5) options.add(p);
        choiceBoxApplyDiscountRate.setItems(FXCollections.observableArrayList(options));
        int currentDiscount = (int)Math.round(billing.getDiscountValue());
        if (options.contains(currentDiscount)) choiceBoxApplyDiscountRate.setValue(currentDiscount);
        else choiceBoxApplyDiscountRate.setValue(0);
        // this doesnt allow the admin user to reconfigure the discount rate if partial/full payment has already been made
        if (billing.getPaidAmount() > 0.0) choiceBoxApplyDiscountRate.setDisable(true);
        btnCheckOutViewPaymentHistory.setOnAction(e-> openPaymentHistoryModal());

        // conditionally disable buttons if reservation checked out
        if (reservation.getReservationStatus().toString().equals("CHECKED_OUT")) {
            btnCheckOutConfirm.setDisable(true);
        }
        else {
            btnCheckOutConfirm.setDisable(false);
        }


    }

    private void loadData() {
        labelCheckOutReservationTitle.setText("Checkout for Reservation " + reservation.getId());
        labelCheckOutHeader.setText(reservation.getGuest().getName() + " - " + reservation.getCheckIn().format(DATE_FMT) + " to " +
                reservation.getCheckOut().format(DATE_FMT) + " - " + reservation.getNumAdults() + " Adults " +
                reservation.getNumChildren() + " Children ");
        labelCheckOutSubHeader.setText(""); // this needs to show all the rooms and addons
        labelCheckOutInvoiceId.setText(billing.getId().toString());
        labelCheckOutSubTotal.setText("$" + billing.getSubTotal());
        labelCheckOutTaxRate.setText("%" + billing.getTaxRate());
        labelCheckOutTaxTotal.setText("$" + billing.getTaxAmount());
        loyaltyPtsRedeemInput.setText(billing.getLoyaltyRedeemedPoints() + " points");

        //double discounted = discountPolicy.applyDiscount(billing.getTotalAmount(), (int)choiceBoxApplyDiscountRate.getValue());
        if ( billing.getDiscountValue() > 0) {
            labelCheckOutTotal.setText(billing.getTotalAmount()+ " (%" + (int)billing.getDiscountValue() + " discount applied)");
        }
        else {
            labelCheckOutTotal.setText("$" + billing.getTotalAmount());
        }
        labelCheckOutPaid.setText("$" + billing.getPaidAmount());
        labelCheckOutBalance.setText("$" + billing.getBalanceAmount());
        int pts = reservation.getGuest().getLoyaltyPoints();
        labelCheckOutLoyaltyPtsBalance.setText(String.valueOf(pts));

    }

    private void loadListeners() {
        // recalculation for totals based on changes to discount rate and loyalty input validation
        choiceBoxApplyDiscountRate.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n == null) return;
            applyDiscount(n);
            loadData();
        });


        btnCheckOutConfirm.setOnAction(e -> onConfirmPayment());
        btnCloseCheckout.setOnAction(e -> {
            Stage st = (Stage) borderPaneAdminCheckOut.getScene().getWindow();
            try {
                Parent root = guiceLoader.load(
                        "/ca/senecapolytechnic/application/apd545project/admin-view.fxml"
                );
                st.setScene(new Scene(root));
                st.show();
            } catch (Exception err) {
                err.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Unable to return to the admin interface.");
                Logger.debug("Error returning to Admin interface from checkout :", err.getMessage());
            }

        });

        btnCheckOutViewPaymentHistory.setOnAction(e-> openPaymentHistoryModal());
    }

    private void applyDiscount(int percent) { // do not persist here, persist once confirmed
        double getExisting = billing.getDiscountValue();
        billing.setDiscountValue(percent);

        double base = billing.getSubTotal() + billing.getTaxAmount();
        double newTotal = discountPolicy.applyDiscount(base, percent);

        newTotal = roundToCents(newTotal);

        billing.setTotalAmount(newTotal);
        billing.setBalanceAmount(
                Math.max(0.0, roundToCents(newTotal - billing.getPaidAmount()))
        );
        Logger.info("Discount applied");
        // create a log of this
        // edit: this was firing off even when the discount rate was unchanged
        // change to only hit if the rate is different!
        if (getExisting != billing.getDiscountValue()) {
            activityLogService.log(AuthService.getCurrentAdmin().getUsername() + " (" + AuthService.getCurrentAdmin().getRole() + ")",
                    "APPLIED_DISCOUNT", "DiscountPolicy", 0,
                    "Applied discount rate of %" + billing.getDiscountValue() + " to Billing ID " + billing.getId());
        }
    }

    private void onConfirmPayment() {
        try {
            String amountStr = checkOutPaymentAmountInput.getText().trim();

            // validations
            if (amountStr.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Payment Error", "Please enter a payment amount.");
                return;
            }
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                showAlert(Alert.AlertType.WARNING, "Payment Error", "Payment amount must be positive.");
                return;
            }
            PaymentMethod method = choiceBoxCheckOutPaymentType.getValue();
            if (method == null) {
                method = PaymentMethod.CASH;
            }
            Guest guest = reservation.getGuest();
            // for spending loyalty points, they can't exceed the cap defined in the loyalty policy
            // this applies to the billing and not the individual payment
            // so if a guest already used loyalty points for a partial payment, all points payments cant exceed this
            if (method == PaymentMethod.POINTS) {
                if (guest.getLoyaltyPoints() < amount) {
                    showAlert(Alert.AlertType.WARNING, "Loyalty Error",
                            "Guest does not have enough loyalty points.");
                    return;
                }
                if (billing.getLoyaltyRedeemedPoints() + amount > loyaltyPolicy.getRedemptionCap()) {
                    showAlert(Alert.AlertType.WARNING, "Loyalty Error",
                            "This payment exceeds the loyalty redemption cap (" +
                                    loyaltyPolicy.getRedemptionCap() + " points).");
                    return;
                }
            }

            // create payment for the billing and update the billing amounts
            Payment payment = new Payment();
            payment.setBilling(billing);
            payment.setAmount(amount);
            payment.setMethod(method);
            payment.setCreatedAt(java.time.LocalDateTime.now());
            paymentRepository.save(payment);

            // if loyalty points were used, update the redeemed amt in this billing and the guest loyalty pts total
            if (method == PaymentMethod.POINTS) {
                billing.setLoyaltyRedeemedPoints(billing.getLoyaltyRedeemedPoints() + (int)amount);
                guest.setLoyaltyPoints(guest.getLoyaltyPoints() - (int) amount);
                guestRepository.save(guest);
                double dollarValue = loyaltyPolicy.convertPointsToDollars((int)amount);
                billing.setBalanceAmount(billing.getBalanceAmount() - dollarValue);
                billing.setPaidAmount(billing.getPaidAmount() + dollarValue);
            } else {
                billing.setPaidAmount(
                        roundToCents(billing.getPaidAmount() + amount)
                );
                billing.setBalanceAmount(
                        roundToCents(billing.getTotalAmount() - billing.getPaidAmount())
                );
            }
            billing.setPaymentStatus(billing.getBalanceAmount() <= 0.001 ? "PAID" : "PARTIAL");

            if (billing.getPaymentStatus().equals("PAID")) {
                reservation.setReservationStatus(ReservationStatus.CHECKED_OUT);
                reservationRepository.save(reservation);
            }
            EntityManager em = AppConfig.getEntityManager();
            try {
                em.getTransaction().begin();
                //em.persist(payment);
                em.merge(billing);
                em.getTransaction().commit();
            } finally {
                em.close();
            }
            // definitely log this
            activityLogService.log(AuthService.getCurrentAdmin().getUsername() + " (" + AuthService.getCurrentAdmin().getRole() + ")",
                    "CONFIRMED_PAYMENT", "Billing", billing.getId().intValue(),
                    "Confirmed payment of $" + payment.getAmount() + " (" + payment.getMethod() + ") for Billing ID " + billing.getId() );

            showAlert(Alert.AlertType.INFORMATION, "Success", "Payment recorded successfully.");
            loadData();

            //((Stage) borderPaneAdminCheckOut.getScene().getWindow()).hide();
            Stage stage = (Stage) btnCheckOutConfirm.getScene().getWindow();
            Parent root = guiceLoader.load("/ca/senecapolytechnic/application/apd545project/admin-view.fxml");
            stage.setScene(new Scene(root));

        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Invalid amount", "Please enter a valid numeric amount.");
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to record payment: " + ex.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.showAndWait();
    }

    // formatting and calculations

    // was experiencing issues with the discount rates / policy due to the floating points
    private double roundToCents(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MMM d, yyyy");

    private void openPaymentHistoryModal() {
        try {
            /*Parent root = guiceLoader.load(
                    "/ca/senecapolytechnic/application/apd545project/payment-history-view.fxml"
            );*/
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ca/senecapolytechnic/application/apd545project/payment-history-view.fxml")
            );
            loader.setController(this);
            Parent root = loader.load();

            Stage modal = new Stage();
            modal.setTitle("Payment History");
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setScene(new Scene(root));

            populatePaymentHistory();

            btnPaymentHistoryClose.setOnAction(e -> modal.close());

            modal.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load payment history.");
        }
    }

    private void populatePaymentHistory() {

        labelPaymentHeader.setText(
                "Payment History for invoice " + billing.getId() +
                        " by " + reservation.getGuest().getName() +
                        " for reservation " + reservation.getId()
        );
        labelPaymentSubHeader.setText(
                String.format(
                        "Total Amount : $%.2f - Balance Paid : $%.2f - Balance Remaining : $%.2f",
                        billing.getTotalAmount(),
                        billing.getPaidAmount(),
                        billing.getBalanceAmount()
                )
        );

        System.out.println("Billing ID: " + billing.getId());
        List<Payment> test = paymentRepository.findByBillingId(billing.getId());
        System.out.println("Payments found: " + test.size());
        for (Payment p : test) {
            System.out.println("Payment ID: " + p.getId() + ", amount=" + p.getAmount());
        }
        List<Payment> test2 = paymentRepository.findAll();
        System.out.println("All payments found: " + test2.size());


        List<Payment> payments = paymentRepository.findByBillingId(billing.getId());
        ObservableList<String> items = FXCollections.observableArrayList();
        if (payments == null || payments.isEmpty()) {
            items.add("No previous payments found.");
            paymentHistoryListView.setItems(items);
            return;
        }


        for (Payment p : payments) {

            String formattedDate = p.getCreatedAt().toLocalDate().toString();
            double displayAmount = p.getAmount();

            // amount would be loyalty points if it was the payment method, convert it to $ to avoid confusion
            if (p.getMethod() == PaymentMethod.POINTS) {
                displayAmount = loyaltyPolicy.convertPointsToDollars((int)displayAmount);
            }
            String entry = formattedDate +
                    String.format(" - Amount Paid : $%.2f - Payment in %s",
                            displayAmount,
                            p.getMethod().toString()
                    );

            items.add(entry);
        }
        paymentHistoryListView.setItems(items);

    }

}
