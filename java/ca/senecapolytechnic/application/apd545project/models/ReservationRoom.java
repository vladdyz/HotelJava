package ca.senecapolytechnic.application.apd545project.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="ReservationRooms")
public class ReservationRoom implements Serializable {
    private static final long serialVersionUID = 1L;
    // bridge table between Reservation and Room (many to many)
    // a reservation can be booked for multiple rooms, and rooms can be reserved many times
    // 1 - 1...*
    // need a PK for every model
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reservation_id", nullable = false/*, unique = true*/) // not a 1-1!!
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name="room_id", nullable = false/*, unique = true*/) // not a 1-1!!
    private Room room;

    public ReservationRoom() {
    }

    public ReservationRoom(Reservation reservation, Room room) {
        this.reservation = reservation;
        this.room = room;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ReservationRoom{" +
                "reservation=" + reservation +
                ", room=" + room +
                '}';
    }
}
