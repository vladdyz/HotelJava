package ca.senecapolytechnic.application.apd545project.models;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="Feedbacks", uniqueConstraints = {@UniqueConstraint(columnNames={"id"})})
public class Feedback implements Serializable {
    // a feedback belongs to a specific guest (but a guest can leave many feedbacks...or none)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="guest_id", nullable = false)
    private Guest guest;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // a feedback needs to correspond to a specific reservation
    @OneToOne
    @JoinColumn(name="reservation_id", nullable = false, unique = true)
    private Reservation reservation;
    @Column(nullable = false)
    private int rating;
    @Column(length=300)
    private String comments;
    @Column(length=50)
    private String sentimentTag;
    @Column(nullable = false, name="created_at")
    private LocalDateTime createdAt;

    public Feedback() {
    }

    public Feedback(Guest guest, Reservation reservation, int rating, String comments, String sentimentTag, LocalDateTime createdAt) {
        this.guest = guest;
        this.reservation = reservation;
        this.rating = rating;
        this.comments = comments;
        this.sentimentTag = sentimentTag;
        this.createdAt = createdAt;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getSentimentTag() {
        return sentimentTag;
    }

    public void setSentimentTag(String sentimentTag) {
        this.sentimentTag = sentimentTag;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", guest=" + guest +
                ", reservation=" + reservation +
                ", rating=" + rating +
                ", comments='" + comments + '\'' +
                ", sentimentTag='" + sentimentTag + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
