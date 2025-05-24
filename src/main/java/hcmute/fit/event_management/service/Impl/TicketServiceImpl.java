package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.dto.TicketDTO;
import hcmute.fit.event_management.entity.*;
import hcmute.fit.event_management.repository.*;
import hcmute.fit.event_management.service.ITicketService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import payload.Response;

import java.util.*;

@Service
public class TicketServiceImpl implements ITicketService {
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private BookingDetailsRepository bookingDetailsRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Optional<Ticket> findById(Integer ticketId) {
        return ticketRepository.findById(ticketId);
    }

    @Override
    public Response deleteById(Integer ticketId) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        Response response = new Response();
        if (ticket.isPresent()) {
            Event event = ticket.get().getEvent();
            if("Complete".equals(event.getEventStatus())){

                response.setStatusCode(401);
                response.setMsg("The event has been completed, cannot delete tickets");
                response.setData(false);
                return response;
            }
            List<BookingDetails> list = bookingDetailsRepository.findByTicketId(ticketId);
            if (!list.isEmpty() && list.size() > 0) {
                response.setStatusCode(401);
                response.setMsg("Tickets of the event were sold, not possible");
                response.setData(false);
                return response;
            }else {
                ticketRepository.deleteById(ticketId);

                response.setStatusCode(200);
                response.setMsg("Successful delete tickets");
                response.setData(true);
                return response;
            }
        }

        return null;
    }

    @Override
    public void addTicket(int eventId, TicketDTO ticketDTO) {
        Ticket ticket = new Ticket();
        BeanUtils.copyProperties(ticketDTO, ticket);
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
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

    @Override
    public void saveEditTicket(int eventId, TicketDTO ticketDTO) throws Exception {
        Optional<Ticket> existingTicketOpt = ticketRepository.findById(ticketDTO.getTicketId());
        Ticket ticket;

        if (existingTicketOpt.isPresent()) {
            // Update existing ticket
            ticket = existingTicketOpt.get();
            BeanUtils.copyProperties(ticketDTO, ticket, "event");
        } else {
            // Create new ticket
            ticket = new Ticket();
            BeanUtils.copyProperties(ticketDTO, ticket);
            Optional<Event> optionalEvent = eventRepository.findById(eventId);
            if (optionalEvent.isEmpty()) {
                throw new RuntimeException("Event not found with id: " + eventId);
            }
            ticket.setEvent(optionalEvent.get());
        }

        ticketRepository.save(ticket);
    }

    @Override
    public void deleteTicketByEventId(int eventId) {
        List<Ticket> tickets = ticketRepository.findByEventID(eventId);
        if (!tickets.isEmpty()) {
            for (Ticket ticket : tickets) {
                ticketRepository.delete(ticket);
            }
        }
    }
    @Override
    public List<Ticket> findByEventUserUserId(int userId) {
        return ticketRepository.findByEventUserUserId(userId);
    }
    @Override
    public List<Ticket> findByEventEventID(int eventId) {
        return ticketRepository.findByEventEventID(eventId);
    }

    @Override
    public Response checkBeforeBuyTicket(String userEmail, int eventId) {
        Response response = new Response();

        // Find user by email
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            response.setStatusCode(404);
            response.setMsg("User not found with email: " + userEmail);
            response.setData(null);
            return response;
        }
        User user = userOpt.get();

        // Find event
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            response.setStatusCode(404);
            response.setMsg("Event not found with id: " + eventId);
            response.setData(null);
            return response;
        }

        // Get all bookings for the user and event
        List<Booking> bookings = bookingRepository.findByEventEventID(eventId);
        int paidTicketCount = 0;
        int freeTicketCount = 0;

        for (Booking booking : bookings) {
            if (booking.getUser().getUserId() == user.getUserId()) {
                List<BookingDetails> bookingDetails = bookingDetailsRepository.findByBookingId(booking.getBookingId());
                for (BookingDetails detail : bookingDetails) {
                    String ticketType = detail.getTicket().getTicketType().toLowerCase();
                    if (ticketType.equals("free")) {
                        freeTicketCount += detail.getQuantity();
                    } else {
                        paidTicketCount += detail.getQuantity();
                    }
                }
            }
        }

        // Calculate remaining tickets
        int maxFreeTickets = 1;
        int maxPaidTickets = 10;
        int remainingFreeTickets = maxFreeTickets - freeTicketCount;
        int remainingPaidTickets = maxPaidTickets - paidTicketCount;

        // Prepare response data
        Map<String, Integer> remainingTickets = new HashMap<>();
        remainingTickets.put("remainingFreeTickets", Math.max(0, remainingFreeTickets));
        remainingTickets.put("remainingPaidTickets", Math.max(0, remainingPaidTickets));

        // Check if user can still purchase tickets
        if (remainingFreeTickets <= 0 && remainingPaidTickets <= 0) {
            response.setStatusCode(403);
            response.setMsg("User has reached the maximum limit for both free and paid tickets for this event.");
            response.setData(remainingTickets);
            return response;
        }

        response.setStatusCode(200);
        response.setMsg("User can purchase up to " + remainingFreeTickets + " free ticket(s) and " +
                remainingPaidTickets + " paid ticket(s) for this event.");
        response.setData(remainingTickets);
        return response;
    }
}