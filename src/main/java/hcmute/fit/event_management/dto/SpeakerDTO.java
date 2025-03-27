package hcmute.fit.event_management.dto;

import hcmute.fit.event_management.entity.Segment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpeakerDTO {
    private int speakerId;
    private String speakerImage;
    private String speakerName;
    private String speakerEmail;
    private String speakerTitle;
    private String speakerPhone;
    private String speakerAddress;
    private String speakerDesc;
}
