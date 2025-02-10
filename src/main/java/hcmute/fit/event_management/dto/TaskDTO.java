package hcmute.fit.event_management.dto;

import hcmute.fit.event_management.entity.Event;
import hcmute.fit.event_management.entity.SubTask;
import hcmute.fit.event_management.entity.Team;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    private int taskId;
    private String taskName;
    private String taskDesc;
    private String taskDl;
    private String taskStatus;
    private Integer eventId;
    private Integer teamId;
    private String teamName;
    private List<SubTaskDTO> listSubTasks;
}
