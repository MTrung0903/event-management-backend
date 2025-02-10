package hcmute.fit.event_management.entity;

import hcmute.fit.event_management.entity.keys.TeamEmployeeId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "team_employee")
public class TeamEmployee {
    @EmbeddedId
    private TeamEmployeeId id;

    @ManyToOne
    @MapsId("team_id")
    @JoinColumn(name = "team_id", referencedColumnName = "team_id", nullable = false)
    private Team team;

    @ManyToOne
    @MapsId("employee_id")
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id", nullable = false)
    private Employee employee;
}
