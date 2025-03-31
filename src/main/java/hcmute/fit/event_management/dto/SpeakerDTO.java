package hcmute.fit.event_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
