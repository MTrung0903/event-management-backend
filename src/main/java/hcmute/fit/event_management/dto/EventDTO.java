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
    private String eventHost;
    private String eventStatus;
    private String eventStart;
    private String eventEnd;
    private EventLocationDTO eventLocation;
    private String tags;
    private String eventVisibility;
    private String publishTime;
    private String refunds;
    private int validityDays;
    private List<String> eventImages;
    private String textContent;
    private List<String> mediaContent;

}
