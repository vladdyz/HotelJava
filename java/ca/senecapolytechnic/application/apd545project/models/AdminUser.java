package ca.senecapolytechnic.application.apd545project.models;

//import org.hibernate.annotations.Entity;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="AdminUsers", uniqueConstraints = {@UniqueConstraint(columnNames={"id", "username"})})
public class AdminUser implements Serializable {


    private static final long serialVersionUID = 1L;

    // id is a generated field
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, length = 200)
    private String username;
    @Column
    private String passwordHash;
    // must have a role (default admin)
    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(nullable = false)
    private Boolean active;

    public AdminUser() {}

    public AdminUser(String username, String passwordHash, Role role, Boolean active) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "AdminUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", active=" + active +
                '}';
    }
}
