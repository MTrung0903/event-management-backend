package hcmute.fit.event_management.dto;

import hcmute.fit.event_management.entity.Account;
import hcmute.fit.event_management.entity.Manager;
import hcmute.fit.event_management.entity.SubTask;
import hcmute.fit.event_management.entity.Team;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {
    private int id;
    private String fullName;
    private String address;
    private String phone;
    private String email;
    private AccountDTO account;
    private TeamDTO team;
    private ManagerDTO manager;
    private List<SubTaskDTO> listSubTasks;
}
