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
@Table(name ="mc")
public class Mc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mc_id")
    private int mcID;
    @Column(name = "full_name")
    private String mcName;
    @Column(name = "email")
    private String email;
    @Column(name = "image")
    private String image;
    @Column(name = "title")
    private String title;
    @Column(name = "phone")
    private String phone;
    @Column(name = "address")
    private String address;
    @Column(name = "description")
    private String description;
    @OneToMany(mappedBy = "mc")
    private List<Event> listEvents;
}
