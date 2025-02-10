package hcmute.fit.event_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamDetailDTO {
    private Integer teamId;
    private String teamName;
    private Integer  eventId;
    private List<EmployeeDTO> listEmployees;
    private List<TaskDTO> listTasks;
    private List<SubTaskDTO> listSubTasks;
}
