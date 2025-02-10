package hcmute.fit.event_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name ="manager")
public class Manager {
    @Id
    @Column(name = "man_id")
    private int manID;
    @Column(name = "full_name")
    private String name;
    @Column(name = "address")
    private String address;
    @Column(name = "phone")
    private String phone;
    @Column(name = "email")
    private String email;

    @OneToOne
    @MapsId
    @JoinColumn(name = "man_id")
    private Account account;

    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL)
    private List<Event> listEvents;

    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL)
    private List<Employee> listEmployees;
}
