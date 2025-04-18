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
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private int eventID;

    private String eventName;
    private String eventDesc;
    private String eventType;
    private String eventHost;
    private String eventStatus;
    private String eventStart;
    private String eventEnd;
    @Embedded
    private EventLocation eventLocation;
    private String tags;
    private String eventVisibility;
    private String publishTime;
    private String refunds;
    private int validityDays;

    @ElementCollection
    private List<String> eventImages;

    private String textContent;

    @ElementCollection
    private List<String> mediaContent;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Session> sessions;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets;

    @OneToMany(mappedBy = "event",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SponsorEvent> sponsorEvents;
}
