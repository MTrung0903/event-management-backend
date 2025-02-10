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
@Table(name = "employee")
public class Employee {
    @Id
    @Column(name = "employee_id")
    private int id;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "address")
    private String address;
    @Column(name = "phone")
    private String phone;
    @Column(name = "email")
    private String email;

    @OneToOne
    @MapsId
    @JoinColumn(name = "employee_id")
    private Account account;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<TeamEmployee> listTeamEmployees;

    @ManyToOne
    @JoinColumn(name = "man_id")
    private Manager manager;

    @OneToMany(mappedBy = "employee")
    private List<SubTask> listSubTasks;
}
