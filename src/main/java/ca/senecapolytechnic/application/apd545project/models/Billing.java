package ca.senecapolytechnic.application.apd545project.models;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="Billings", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Billing implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // a billing must belong to a reservation (parent) : 1  - 0...1 relation
    // it cant exist independantly
    @OneToOne(optional = false)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @Column(nullable = false)
    private double subTotal;
    @Column(nullable = false)
    private double taxRate;
    @Column(nullable = false)
    private double taxAmount;
    @Column(nullable = false)
    private double discountValue;
    @Column
    private int loyaltyRedeemedPoints;
    @Column(nullable = false)
    private double totalAmount;
    @Column
    private double paidAmount;
    @Column(nullable = false)
    private double balanceAmount;
    @Column(nullable = false, length = 100)
    private String paymentStatus;

    public Billing() {
    }

    public Billing(Reservation reservation, double subTotal, double taxRate, double taxAmount, double discountValue, int loyaltyRedeemedPoints, double totalAmount, double paidAmount, double balanceAmount, String paymentStatus) {
        this.reservation = reservation;
        this.subTotal = subTotal;
        this.taxRate = taxRate;
        this.taxAmount = taxAmount;
        this.discountValue = discountValue;
        this.loyaltyRedeemedPoints = loyaltyRedeemedPoints;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.balanceAmount = balanceAmount;
        this.paymentStatus = paymentStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }

    public int getLoyaltyRedeemedPoints() {
        return loyaltyRedeemedPoints;
    }

    public void setLoyaltyRedeemedPoints(int loyaltyRedeemedPoints) {
        this.loyaltyRedeemedPoints = loyaltyRedeemedPoints;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public double getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(double balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
