package hcmute.fit.event_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {
    private int eventId;
    private String eventName;
    private String eventDesc;
    private String eventType;
    private String eventImage;
    private String eventHost;
    private String eventStatus;
    private String eventStart;
    private String eventEnd;
    private String eventLocation;
    private String eventOverView;
    private boolean isRefund;
    private int dayAllowRefund;
    private boolean isSchedulePublic;
    private String dateSchedulePublic;
    private String eventTag;

}
