package hcmute.fit.event_management.dto;

import hcmute.fit.event_management.entity.Employee;
import hcmute.fit.event_management.entity.Event;
import hcmute.fit.event_management.entity.Task;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamDTO {
    private Integer teamId;
    private String teamName;
    private Integer  eventId;
    private List<EmployeeDTO> listEmployees;
    private List<TaskDTO> listTasks;
}
