package hcmute.fit.event_management.dto;

import hcmute.fit.event_management.entity.Provider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProviderServiceDTO {
    private int id;
    private String serviceType;
    private String serviceName;
    private String serviceDesc;
    private String price;
    private String duration;
    private int providerId;
}
