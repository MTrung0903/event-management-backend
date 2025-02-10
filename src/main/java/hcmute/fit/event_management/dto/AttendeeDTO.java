package hcmute.fit.event_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendeeDTO {
    private int attendeeID;
    private String fullName;
    private String email;
    private String attendeeStatus;
    private Integer inviteId;
}
