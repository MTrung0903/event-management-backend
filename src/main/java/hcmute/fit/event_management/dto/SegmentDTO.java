package hcmute.fit.event_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SegmentDTO {
    private int segmentId;
    private String segmentTitle;
    private int speakerID;
    private String speakerName;
    private String speakerTitle;
    private int eventID;
    private String segmentDesc;
    private Data startTime;
    private Data endTime;
}
