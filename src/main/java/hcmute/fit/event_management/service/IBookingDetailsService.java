package hcmute.fit.event_management.service;

import hcmute.fit.event_management.dto.OrderTicketDTO;

import java.util.List;

public interface IBookingDetailsService {
    List<OrderTicketDTO> getOrdersTicket(int ticketId);
}
