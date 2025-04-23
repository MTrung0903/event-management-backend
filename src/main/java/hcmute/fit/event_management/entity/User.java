package hcmute.fit.event_management.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
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
//    name: formData.fullName,
//    email: formData.email,
//    password: formData.password,
//    birthday: formData.birthday,
//    gender: formData.gender,
//    address: formData.address,
//    isOrganize: role === 'organizer' ? true : false,
//    organizer: role === 'organizer' ? {
//        organizerName: formData.organizerName,
//                organizerAddress: formData.address,
//                organizerWebsite: formData.organizerWebsite,
//                organizerPhone: formData.organizerPhone
//    } : {}
    private String name;
    private Date birthday;
    private String gender;
    

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
