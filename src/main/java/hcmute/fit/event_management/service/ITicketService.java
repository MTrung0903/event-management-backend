package hcmute.fit.event_management.service;

import hcmute.fit.event_management.dto.TicketDTO;
import hcmute.fit.event_management.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ITicketService {
    Optional<Ticket> findById(Integer integer);


    void addTicket(int eventId, TicketDTO ticketDTO);

    List<TicketDTO> getTicketsByEventId(int eventId);
}
