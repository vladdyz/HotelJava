package ca.senecapolytechnic.application.apd545project.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Optional;

@Entity
@Table(name="Guests", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Guest implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length=200)
    private String name;
    @Column(nullable = false, length=30)
    private String phone;
    @Column(length=100)
    private String email;
    @Column(nullable = false, length=300)
    private String address;
    @Column
    private int loyaltyPoints;
    @Column
    private int loyaltyNumber;
    @Column
    private Boolean isActive;

    public Guest() {
    }

    public Guest(String name, String phone, String email, String address, int loyaltyPoints, int loyaltyNumber, Boolean isActive) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.loyaltyPoints = loyaltyPoints;
        this.loyaltyNumber = loyaltyNumber;
        this.isActive = isActive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public int getLoyaltyNumber() {
        return loyaltyNumber;
    }

    public void setLoyaltyNumber(int loyaltyNumber) {
        this.loyaltyNumber = loyaltyNumber;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Guest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", loyaltyPoints=" + loyaltyPoints +
                ", loyaltyNumber=" + loyaltyNumber +
                ", isActive=" + isActive +
                '}';
    }

}
