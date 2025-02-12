package hcmute.fit.event_management.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(name ="email")
    private String email;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "password")
    private String password;

    @OneToOne
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Notification> listNoti;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Booking> listBooking;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private PasswordResetToken token;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserRole> listUserRoles;

}
