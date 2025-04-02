package hcmute.fit.event_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "speaker")
public class Speaker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "speaker_id")
    private int speakerId;
    @Column(name = "speakerImage")
    private String speakerImage;
    @Column(name = "speaker_name")
    private String speakerName;
    @Column(name = "speaker_email")
    private String speakerEmail;
    @Column(name = "speaker_title")
    private String speakerTitle;
    @Column(name = "speaker_phone")
    private String speakerPhone;
    @Column(name = "speaker_address")
    private String speakerAddress;
    @Column(name = "speaker_desc")
    private String speakerDesc;

    @OneToMany(mappedBy = "speaker",cascade = CascadeType.ALL)
    private List<Segment> segments;
}