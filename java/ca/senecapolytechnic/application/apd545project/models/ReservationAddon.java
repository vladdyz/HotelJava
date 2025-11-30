package ca.senecapolytechnic.application.apd545project.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="ReservationAddons")
public class ReservationAddon implements Serializable {
    private static final long serialVersionUID = 1L;

    // optional addon, cant exist without a reservation
  // a reservation does not necessarily have an add-on however
  // 1 - 0...1
  // need a PK for every model
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
  @ManyToOne(optional = false)
  @JoinColumn(name = "reservation_id", nullable = false/*, unique = true*/) // not a 1-1!!
  private Reservation reservation;
  // must correspond to a service (this is a bridge table)
  // a service addon is not unique in the sense a reservation is unique, a service can be used by many reservations
  // 1 - 1...*
  @ManyToOne(optional = false)
  @JoinColumn(name = "addon_id", nullable = false/*, unique = true*/) // not a 1-1!!
  private ServiceAddon addon;
  @Column(nullable = false)
  private int quantity;

    public ReservationAddon() {
    }

    public ReservationAddon(Reservation reservation, ServiceAddon addon, int quantity) {
        this.reservation = reservation;
        this.addon = addon;
        this.quantity = quantity;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public ServiceAddon getAddon() {
        return addon;
    }

    public void setAddon(ServiceAddon addon) {
        this.addon = addon;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ReservationAddon{" +
                "reservation=" + reservation +
                ", addon=" + addon +
                ", quantity=" + quantity +
                '}';
    }
}
