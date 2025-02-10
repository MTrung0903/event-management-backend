package hcmute.fit.event_management.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import hcmute.fit.event_management.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    private int accountID;
    private String email;
    private String password;
    @JsonProperty
    private boolean isActive;
    private List<String> roles;
}
