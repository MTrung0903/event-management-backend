package hcmute.fit.event_management.entity.keys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProviderServiceEventId {
    @Column(name = "service_id")
    private int serviceId;

    @Column(name = "event_id")
    private int eventId;
}
