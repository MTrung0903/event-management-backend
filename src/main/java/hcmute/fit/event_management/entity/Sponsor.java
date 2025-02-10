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
    private int id;
    @Column(name = "sponsor_logo")
    private String sponsorLogo;
    @Column(name = "sponsor_name")
    private String name;
    @Column(name = "contact_person")
    private String contact;
    @Column(name = "contact_email")
    private String email;
    @Column(name = "contact_phone")
    private String phone;
    @Column(name = "website")
    private String website;
    @Column(name = "address")
    private String address;

    @ManyToOne
    @JoinColumn(name = "sponsor_ship_id", referencedColumnName = "sponsor_ship_id")
    private SponsorShip sponsorship;

    @OneToMany(mappedBy = "sponsor")
    private List<SponsorEvent> listSponsorEvents;
}
