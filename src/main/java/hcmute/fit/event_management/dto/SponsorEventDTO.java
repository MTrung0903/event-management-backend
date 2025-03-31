package hcmute.fit.event_management.dto;

import hcmute.fit.event_management.entity.Event;
import hcmute.fit.event_management.entity.Sponsor;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SponsorEventDTO {
    private int sponsorId;
    private String sponsorName;
    private String sponsorLogo;
    private String sponsorEmail;
    private String sponsorPhone;
    private String sponsorAddress;
    private String sponsorWebsite;
    private String sponsorRepresentativeName;
    private String sponsorRepresentativeEmail;
    private String sponsorRepresentativePhone;
    private String sponsorRepresentativePosition;
    private String sponsorType;
    private String sponsorLevel;
    private Double sponsorAmount;
    private String sponsorContribution;
    private String sponsorContract;
    private String sponsorStartDate;
    private String sponsorEndDate;
    private String sponsorStatus;
}
