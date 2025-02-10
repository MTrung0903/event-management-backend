package hcmute.fit.event_management.dto;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpeakerDTO {
    private int id;
    private String name;
    private String email;
    private String title;
    private String phone;
    private String address;
    private String description;
    private String image;

}
