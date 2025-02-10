package hcmute.fit.event_management.dto;

import hcmute.fit.event_management.entity.SponsorEvent;
import hcmute.fit.event_management.entity.SponsorShip;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SponsorDTO {
    private int id;
    private String name;
    private String sponsorLogo;
    private String contact;
    private String email;
    private String phone;
    private String website;
    private String address;
    private int sponsorshipId;
    private String sponsorshipLevel;
    private List<SponsorEventDTO> listSponsorEvents;
}
