package hcmute.fit.event_management.dto;

import hcmute.fit.event_management.entity.Provider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProviderEventDTO {
    private int id;
    private int providerId;
    private int eventId;
}
