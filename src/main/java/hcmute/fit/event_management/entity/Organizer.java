package hcmute.fit.event_management.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "organizer")
public class Organizer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organizer_id")
    private int organizerId;

    @Column(name = "organizer_name", nullable = false)
    private String organizerName;

    @Column(name = "organizer_address")
    private String organizerAddress;

    @Column(name = "organizer_website")
    private String organizerWebsite;

    @Column(name = "organizer_phone")
    private String organizerPhone;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;
}