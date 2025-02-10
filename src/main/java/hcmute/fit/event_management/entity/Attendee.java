package hcmute.fit.event_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "attendee")
public class Attendee {
    @Id
    @Column(name = "attendee_id")
    private int attendeeID;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "email")
    private String email;
    @Column(name = "attendance_status")
    private String attendeeStatus;

    @OneToOne
    @MapsId
    @JoinColumn(name = "attendee_id")
    private Invite invite;
}
