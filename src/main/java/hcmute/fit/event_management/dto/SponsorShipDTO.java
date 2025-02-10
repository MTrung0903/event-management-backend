package hcmute.fit.event_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SponsorShipDTO {
    private int sponsorShipID;
    private String level;
    private String benefit;
    private List<SponsorDTO> listSponsors;
}
