package hcmute.fit.event_management.dto;


import hcmute.fit.event_management.entity.Event;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SectionDTO {
    private int sectionId;
    private String startTime;
    private String endTime;
    private String sectionTitle;
    private String sectionDescription;
    private String handOut;
    private int eventId;
    private int speakerId;
    private String speakerName;

}
