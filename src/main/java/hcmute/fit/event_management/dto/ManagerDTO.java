package hcmute.fit.event_management.dto;

import hcmute.fit.event_management.entity.Account;
import hcmute.fit.event_management.entity.Employee;
import hcmute.fit.event_management.entity.Event;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagerDTO {
    private int manID;
    private String name;
    private String address;
    private String phone;
    private String email;
    private AccountDTO account;
    private List<EventDTO> listEvents;
    private List<EmployeeDTO> listEmployees;
}
