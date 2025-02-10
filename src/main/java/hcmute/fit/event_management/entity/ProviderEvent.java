package hcmute.fit.event_management.entity;

import hcmute.fit.event_management.entity.keys.ProviderEventId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "provider_event")
public class ProviderEvent {
    @EmbeddedId
    private ProviderEventId id;

    @ManyToOne
    @MapsId("provider_id")
    @JoinColumn(name = "provider_id", referencedColumnName = "provider_id", nullable = false)
    private Provider provider;

    @ManyToOne
    @MapsId("event_id")
    @JoinColumn(name = "event_id", referencedColumnName = "event_id", nullable = false)
    private Event event;

}