package ca.senecapolytechnic.application.apd545project.models;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="AuditLogs", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class AuditLog implements Serializable {
    private static final long serialVersionUID = 1L;
    // id is a generated field
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDateTime timestamp;
    @Column(nullable = false, length = 200)
    private String actor;
    @Column(nullable = false, length=250)
    private String action;
    @Column(nullable = false, length=200)
    private String entityType;
    @Column(nullable = false)
    private int entityId;
    @Column(length=300)
    private String message;

    public AuditLog() {
    }

    public AuditLog(LocalDateTime timestamp, String actor, String action, String entityType, int entityId, String message) {
        this.timestamp = timestamp;
        this.actor = actor;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", actor='" + actor + '\'' +
                ", action='" + action + '\'' +
                ", entityType='" + entityType + '\'' +
                ", entityId=" + entityId +
                ", message='" + message + '\'' +
                '}';
    }
}
