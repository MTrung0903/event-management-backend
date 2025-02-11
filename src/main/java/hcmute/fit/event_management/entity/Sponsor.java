package hcmute.fit.event_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sponsor")
public class Sponsor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sponsor_id")
    private int sponsorId;
    @Column(name = "sponsor_logo")
    private String sponsorLogo;
    @Column(name = "sponsor_name")
    private String sponsorName;
    @Column(name = "contact_person")
    private String sponsorContact;
    @Column(name = "contact_email")
    private String sponsorEmail;
    @Column(name = "contact_phone")
    private String sponsorPhone;
    @Column(name = "website")
    private String sponsorWebsite;
    @Column(name = "address")
    private String sponsorAddress;

    @ManyToOne
    @JoinColumn(name = "sponsor_ship_id", referencedColumnName = "sponsor_ship_id")
    private SponsorShip sponsorship;

    @OneToMany(mappedBy = "sponsor")
    private List<SponsorEvent> listSponsorEvents;
}
