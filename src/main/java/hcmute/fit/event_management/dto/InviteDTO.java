package hcmute.fit.event_management.dto;

import hcmute.fit.event_management.entity.Attendee;
import hcmute.fit.event_management.entity.Event;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InviteDTO {
    private int id;
    private String name;
    private String email;
    private String inviteDate;
    private String status;
    private int eventId;
}
