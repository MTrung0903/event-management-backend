package hcmute.fit.event_management.dto;

import hcmute.fit.event_management.entity.Event;
import hcmute.fit.event_management.entity.Sponsor;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SponsorEventDTO {
    private int eventId;
    private int sponsorId;
    private String eventName;
}
