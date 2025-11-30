package ca.senecapolytechnic.application.apd545project.models;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="payments", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // payment must correspond to billing, cant exist without it (1 to 0...1)
    @OneToOne(optional = false)
    @JoinColumn(name = "billing_id", nullable = false, unique = true)
    private Billing billing;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false, name="created_at")
    private LocalDateTime createdAt;

    public Payment() {
    }

    public Payment(Billing billing, PaymentMethod method, double amount, LocalDateTime createdAt) {
        this.billing = billing;
        this.method = method;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Billing getBilling() {
        return billing;
    }

    public void setBilling(Billing billing) {
        this.billing = billing;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", billing=" + billing +
                ", method=" + method +
                ", amount=" + amount +
                ", createdAt=" + createdAt +
                '}';
    }
}
