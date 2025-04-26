package hcmute.fit.event_management.dto;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public  class OrganizerDTO {
    private String organizerName;
    private String organizerAddress;
    private String organizerWebsite;
    private String organizerPhone;
}
