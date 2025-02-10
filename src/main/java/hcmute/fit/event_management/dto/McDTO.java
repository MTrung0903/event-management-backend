package hcmute.fit.event_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;
import java.util.Set;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class McDTO {
    private int mcID;
    private String mcName;
    private String email;
    private String image;
    private String title;
    private String phone;
    private String address;
    private String description;;
    private List<EventDTO> listEvents;
}
