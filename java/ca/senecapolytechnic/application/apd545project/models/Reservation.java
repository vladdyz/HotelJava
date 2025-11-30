package ca.senecapolytechnic.application.apd545project.models;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="Reservations", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDateTime checkIn;
    @Column(nullable = false)
    private LocalDateTime checkOut;
    @Column
    private int numAdults;
    @Column
    private int numChildren;
    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;
    // i needed to add an additional field to establish relations w Guest parent
    @ManyToOne(optional = false)      // optional=false if every reservation must have a guest
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    public Guest getGuest() {
        return guest;
    }

    public Reservation(LocalDateTime checkIn, LocalDateTime checkOut, int numAdults, int numChildren, ReservationStatus reservationStatus, Guest guest) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.numAdults = numAdults;
        this.numChildren = numChildren;
        this.reservationStatus = reservationStatus;
        this.guest = guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public Reservation() {
    }

    public Reservation(LocalDateTime checkIn, LocalDateTime checkOut, int numAdults, int numChildren, ReservationStatus reservationStatus) {

        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.numAdults = numAdults;
        this.numChildren = numChildren;
        this.reservationStatus = reservationStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDateTime checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDateTime getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDateTime checkOut) {
        this.checkOut = checkOut;
    }

    public int getNumAdults() {
        return numAdults;
    }

    public void setNumAdults(int numAdults) {
        this.numAdults = numAdults;
    }

    public int getNumChildren() {
        return numChildren;
    }

    public void setNumChildren(int numChildren) {
        this.numChildren = numChildren;
    }

    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", numAdults=" + numAdults +
                ", numChildren=" + numChildren +
                ", reservationStatus=" + reservationStatus +
                '}';
    }
}
