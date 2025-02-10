package hcmute.fit.event_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {
    private int eventId;
    private String eventName;
    private String eventType;
    private String eventHost;
    private String eventStatus;
    private String eventDescription;
    private String eventImg;
    private String eventStart;
    private String eventEnd;
    private String eventLocation;
    private Integer manId;
    private Integer mcId;
}
