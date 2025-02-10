package hcmute.fit.event_management.dto;

import hcmute.fit.event_management.entity.ProviderEvent;
import hcmute.fit.event_management.entity.ProviderService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProviderDTO {
    private int id;
    private String name;
    private String contact;
    private String email;
    private String phone;
    private String address;
    private String website;
    private List<ProviderServiceDTO> listProviderServices;
}
