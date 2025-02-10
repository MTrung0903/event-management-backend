package hcmute.fit.event_management.dto;

import hcmute.fit.event_management.entity.Employee;
import hcmute.fit.event_management.entity.Task;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubTaskDTO {
    private int subTaskId;
    private String subTaskName;
    private String subTaskDesc;
    private String subTaskDeadline;
    private String subTaskStart;
    private String status;
    private int employeeId;
    private int taskId;
}
