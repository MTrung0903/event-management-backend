package hcmute.fit.event_management.entity;

import hcmute.fit.event_management.entity.keys.SponsorEventId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sponsor_event")
public class SponsorEvent {

    @EmbeddedId
    private SponsorEventId id;

    @ManyToOne
    @MapsId("sponsor_id")
    @JoinColumn(name = "sponsor_id", referencedColumnName = "sponsor_id", nullable = false)
    private Sponsor sponsor;

    @ManyToOne
    @MapsId("event_id")
    @JoinColumn(name = "event_id", referencedColumnName = "event_id", nullable = false)
    private Event event;

}