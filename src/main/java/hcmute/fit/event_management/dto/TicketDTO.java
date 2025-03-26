package hcmute.fit.event_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketDTO {
    private int ticketId;
    private String ticketName;
    private String ticketType;
    private double price;
    private int quantity;
    private Data startTime;
    private Data endTime;

}
