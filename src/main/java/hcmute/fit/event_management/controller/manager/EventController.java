package hcmute.fit.event_management.controller.manager;

import hcmute.fit.event_management.entity.Event;
import hcmute.fit.event_management.service.Impl.EventServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventServiceImpl eventService;

    @PostMapping(consumes = "application/json") // Chỉ nhận JSON
    public ResponseEntity<Event> createEvent(@RequestBody Event event) throws IOException {
        Event savedEvent = eventService.saveEvent(event);
        return ResponseEntity.ok(savedEvent);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<Event> getEvent(@PathVariable Integer eventId) {
        Event event = eventService.getEvent(eventId);
        return ResponseEntity.ok(event);
    }
}