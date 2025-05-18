package hcmute.fit.event_management.controller.manager;

import hcmute.fit.event_management.dto.TicketDTO;
import hcmute.fit.event_management.entity.*;
import hcmute.fit.event_management.service.*;
import hcmute.fit.event_management.service.Impl.BookingServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import payload.Response;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/ticket")
public class TicketController {
    @Autowired
    ITicketService ticketService;
    @Autowired
    IUserService userService;
    @Autowired
    IOrganizerService organizationService;
    @Autowired
    ICheckInTicketService checkInTicketService;
    @Autowired
    IBookingService bookingService;
    @Autowired
    IBookingDetailsService bookingDetailsService;
    @Autowired
    IEventService eventService;
    @PostMapping("/{eventId}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<TicketDTO> createTicket(@PathVariable int eventId, @RequestBody TicketDTO ticketDTO) {
        ticketService.addTicket(eventId, ticketDTO);
        return ResponseEntity.ok(ticketDTO);
    }

    @GetMapping("detail/{eventId}")
    public ResponseEntity<List<TicketDTO>> getTicket(@PathVariable int eventId) {
        List<TicketDTO> list = ticketService.getTicketsByEventId(eventId);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("delete/{ticketId}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<Boolean> deleteTicket(@PathVariable int ticketId) {
        ticketService.deleteById(ticketId);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/check-in/{ticketCode}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<?> checkIn(@PathVariable String ticketCode, Authentication authentication) {
        Response response = new Response();
        String username = authentication.getName();
        Optional<User> userOpt = userService.findByEmail(username);
        if (userOpt.isEmpty()) {
            response = new Response(0, "User not permission!", "");
            return ResponseEntity.ok(response);
        }
        User user = userOpt.get();
        int userId = user.getUserId();

        CheckInTicket ticket = checkInTicketService.getReferenceById(ticketCode);
        if (ticket != null) {
            if (ticket.getBookingDetails().getTicket().getEvent().getUser().getUserId() == userId) {

                if (ticket.getStatus() == 1) {
                    response = new Response(0, "The ticket was checked", "");
                    return ResponseEntity.ok(response);
                }
                if (ticket.getStatus() == -1) {
                    response = new Response(0, "The ticket was canceled", "");
                    return ResponseEntity.ok(response);
                }
                ticket.setStatus(1);
                ticket.setCheckDate(LocalDateTime.now());
                checkInTicketService.save(ticket);
            } else {
                response = new Response(0, "The Ticket not in your event!", "");
                return ResponseEntity.ok(response);
            }
        } else {
            response = new Response(0, "The Ticket not found!", "");
            return ResponseEntity.ok(response);
        }
        response = new Response(1, "Check the ticket successfully!", ticket.getBookingDetails().getTicket().getEvent().getEventID());
        return ResponseEntity.ok(response);
    }

    @GetMapping("{eventId}/stats")
    @PreAuthorize("hasRole('ORGANIZER')")
    public Map<String, String> getEventStats(@PathVariable int eventId) {
        // Tổng số vé có sẵn
        List<Ticket> tickets = ticketService.findByEventEventID(eventId);
        int totalTickets = tickets.stream().mapToInt(Ticket::getQuantity).sum();

        // Vé đã bán
        List<Booking> bookings = bookingService.findByEventEventID(eventId);
        int soldTickets = bookings.stream()
                .flatMap(b -> b.getBookingDetails().stream())
                .mapToInt(bd -> bd.getQuantity())
                .sum();

        // Vé đã check-in
        List<CheckInTicket> checkIns = checkInTicketService.findByBookingDetailsBookingEventEventID(eventId);
        int checkedTickets = (int) checkIns.stream()
                .filter(t -> t.getStatus() == 1)
                .count();

        // Vé bị hủy (giả định trạng thái "CANCELED" trong booking_status)
        int canceledTickets = bookings.stream()
                .filter(b -> "CANCELED".equals(b.getBookingStatus()))
                .flatMap(b -> b.getBookingDetails().stream())
                .mapToInt(BookingDetails::getQuantity)
                .sum();

        Map<String, String> stats = new HashMap<>();
        stats.put("ticketsSold", soldTickets + "/" + totalTickets);
        stats.put("ticketsChecked", String.valueOf(checkedTickets));
        stats.put("ticketsCanceled", String.valueOf(canceledTickets));
        return stats;
    }

    @GetMapping("{eventId}/ticket-types")
    public List<Map<String, String>> getTicketTypes(@PathVariable int eventId) {
        List<Ticket> tickets = ticketService.findByEventEventID(eventId);
        List<Map<String, String>> ticketTypes = new ArrayList<>();

        for (Ticket ticket : tickets) {
            List<BookingDetails> details = bookingDetailsService.findByTicketTicketId(ticket.getTicketId());
            int sold = details.stream().mapToInt(BookingDetails::getQuantity).sum();

            Map<String, String> ticketInfo = new HashMap<>();
            ticketInfo.put("ticketType", ticket.getTicketType());
            ticketInfo.put("sold", sold + "/" + ticket.getQuantity());
            ticketInfo.put("price", String.format("%.2f VND", ticket.getPrice()));

            ticketTypes.add(ticketInfo);
        }

        return ticketTypes;
    }
    @GetMapping("{eventId}/recent-orders")
    public List<Map<String, String>> getRecentOrders(@PathVariable int eventId) {
        List<Booking> bookings = bookingService.findByEventEventIDOrderByCreateDateDesc(eventId);
        List<Map<String, String>> orders = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Booking booking : bookings) {
            for (BookingDetails detail : booking.getBookingDetails()) {
                Map<String, String> order = new HashMap<>();
                order.put("orderId", booking.getBookingCode());
                order.put("name", booking.getUser().getFullName());
                order.put("quantity", String.valueOf(detail.getQuantity()));
                order.put("ticketType", detail.getTicket().getTicketType());
                order.put("price", String.format("%.2f VND", detail.getPrice()));
                order.put("date", dateFormat.format(booking.getCreateDate()));
                orders.add(order);
            }
        }
        return orders;
    }

}
