package hcmute.fit.event_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ticket")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private int ticketId;
    @Column(name = "ticket_name")
    private String ticketName;

    @Column(name = "ticket_type")
    private String ticketType;

    @Column(name="price")
    private double price;

    @Column(name = "quantity")
    private int quantity;

    @ManyToOne
    @JoinColumn(name ="event_id")
    private Event event;



    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<RefundDetails> refundDetails;


}
