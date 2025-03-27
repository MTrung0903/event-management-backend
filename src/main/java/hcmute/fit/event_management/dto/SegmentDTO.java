package hcmute.fit.event_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SegmentDTO {
    private int segmentId;
    private String segmentTitle;
    private SpeakerDTO speaker;
    private int eventID;
    private String segmentDesc;
    private Date startTime;
    private Date endTime;
}
