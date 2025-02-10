package hcmute.fit.event_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subtask")
public class SubTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subtask_id")
    private int subTaskId;

    @Column(name = "subtask_name")
    private String subTaskName;
    @Column(name = "subtask_desc")
    private String subTaskDesc;
    @Column(name ="create_date")
    private Date createDate;
    @Column(name = "subtask_deadline")
    private Date subTaskDeadline;
    @Column(name = "subtask_status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
