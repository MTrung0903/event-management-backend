package hcmute.fit.event_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity(name = "Role")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private int roleID;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "role")
    private List<UserRole> listUserRoles;

    @OneToOne(mappedBy = "role")
    private User user;


    public Role(int roleID, String name) {
        this.roleID = roleID;
        this.name = name;
    }


}
