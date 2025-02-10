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
@Table(name = "speaker")
public class Speaker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "speaker_id")
    private int id;
    @Column(name = "image")
    private String image;
    @Column(name = "speaker_name")
    private String name;
    @Column(name = "speaker_email")
    private String email;
    @Column(name = "speaker_title")
    private String title;
    @Column(name = "speaker_phone")
    private String phone;
    @Column(name = "speaker_address")
    private String address;
    @Column(name = "speaker_desc")
    private String description;


}
