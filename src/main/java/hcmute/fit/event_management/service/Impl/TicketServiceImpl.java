package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.dto.TicketDTO;
import hcmute.fit.event_management.entity.Event;
import hcmute.fit.event_management.entity.Ticket;
import hcmute.fit.event_management.repository.TicketRepository;
import hcmute.fit.event_management.service.IEventService;
import hcmute.fit.event_management.service.ITicketService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements ITicketService {
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
   private IEventService eventService;

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Optional<Ticket> findById(Integer integer) {
        return ticketRepository.findById(integer);
    }

    @Override
    public void addTicket(int eventId, TicketDTO ticketDTO) {
        Ticket ticket = new Ticket();
        BeanUtils.copyProperties(ticketDTO, ticket);
        Optional<Event> optionalEvent = eventService.findById(eventId);
        if (optionalEvent.isEmpty()) {
            throw new RuntimeException("Event not found with id: " + eventId);
        }
        ticket.setEvent(optionalEvent.get());

        ticketRepository.save(ticket);
    }
    public TicketDTO convertToDTO(Ticket ticket) {
        TicketDTO ticketDTO = new TicketDTO();
        BeanUtils.copyProperties(ticket, ticketDTO);
        return ticketDTO;
    }
    @Override
    public List<TicketDTO> getTicketsByEventId(int eventId) {
        List<Ticket> tickets = ticketRepository.findByEventID(eventId);
        List<TicketDTO> ticketDTOs = new ArrayList<>();
        for (Ticket ticket : tickets) {
            TicketDTO ticketDTO = convertToDTO(ticket);
            ticketDTOs.add(ticketDTO);
        }
        return ticketDTOs;
    }

}
