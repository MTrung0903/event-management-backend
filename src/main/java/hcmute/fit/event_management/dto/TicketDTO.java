package hcmute.fit.event_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketDTO {
    private int ticketId;
    private String ticketName;
    private String ticketType;
    private double price;
    private int quantity;
    private Date startTime;
    private Date endTime;

}
