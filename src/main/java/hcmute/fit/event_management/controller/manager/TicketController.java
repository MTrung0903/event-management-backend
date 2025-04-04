package hcmute.fit.event_management.controller.manager;

import hcmute.fit.event_management.dto.TicketDTO;
import hcmute.fit.event_management.service.ITicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticket")
public class TicketController {
    @Autowired
    private ITicketService ticketService;

    @PostMapping("/{eventId}")
    public ResponseEntity<TicketDTO> createTicket(@PathVariable int eventId, @RequestBody TicketDTO ticketDTO) {
        ticketService.addTicket(eventId, ticketDTO);
        return ResponseEntity.ok(ticketDTO);
    }

    @GetMapping("list/{eventId}")
    public ResponseEntity<List<TicketDTO>> getTicket(@PathVariable int eventId) {
        List<TicketDTO> list = ticketService.getTicketsByEventId(eventId);
        return ResponseEntity.ok(list);
    }

}
