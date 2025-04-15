package hcmute.fit.event_management.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private int userId;
    private String email;
    private String password;
    private boolean isActive;
    private List<String> roles;
}
