package ca.senecapolytechnic.application.apd545project.models;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="Waitlists", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Waitlist implements Serializable {
    // a waitlist is for a specific guest
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private RoomType requestedType;
    @Column(nullable = false)
    private LocalDateTime dateRangeStart;
    @Column(nullable = false)
    private LocalDateTime dateRangeEnd;
    @Column(nullable = false, length = 200)
    private String status;

    public Waitlist() {
    }

    public Waitlist(Guest guest, RoomType requestedType, LocalDateTime dateRangeStart, LocalDateTime dateRangeEnd, String status) {
        this.guest = guest;
        this.requestedType = requestedType;
        this.dateRangeStart = dateRangeStart;
        this.dateRangeEnd = dateRangeEnd;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public RoomType getRequestedType() {
        return requestedType;
    }

    public void setRequestedType(RoomType requestedType) {
        this.requestedType = requestedType;
    }

    public LocalDateTime getDateRangeStart() {
        return dateRangeStart;
    }

    public void setDateRangeStart(LocalDateTime dateRangeStart) {
        this.dateRangeStart = dateRangeStart;
    }

    public LocalDateTime getDateRangeEnd() {
        return dateRangeEnd;
    }

    public void setDateRangeEnd(LocalDateTime dateRangeEnd) {
        this.dateRangeEnd = dateRangeEnd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Waitlist{" +
                "id=" + id +
                ", guest=" + guest +
                ", requestedType=" + requestedType +
                ", dateRangeStart=" + dateRangeStart +
                ", dateRangeEnd=" + dateRangeEnd +
                ", status='" + status + '\'' +
                '}';
    }
}
