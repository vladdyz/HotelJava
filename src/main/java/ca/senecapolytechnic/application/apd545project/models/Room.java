package ca.senecapolytechnic.application.apd545project.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="Rooms", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 20, unique = true)
    private int roomNumber;
    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private RoomType roomType;
    @Column(nullable = false)
    private int beds;
    @Column(nullable = false)
    private double basePrice;
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private RoomStatus roomStatus;

    public Room() {
    }

    public Room(int roomNumber, RoomType roomType, int beds, double basePrice, RoomStatus roomStatus) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.beds = beds;
        this.basePrice = basePrice;
        this.roomStatus = roomStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public int getBeds() {
        return beds;
    }

    public void setBeds(int beds) {
        this.beds = beds;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public RoomStatus getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(RoomStatus roomStatus) {
        this.roomStatus = roomStatus;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", roomNumber=" + roomNumber +
                ", roomType=" + roomType +
                ", beds=" + beds +
                ", basePrice=" + basePrice +
                ", roomStatus=" + roomStatus +
                '}';
    }
}
