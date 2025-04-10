package hcmute.fit.event_management.controller.manager;

import hcmute.fit.event_management.dto.EventDTO;
import hcmute.fit.event_management.dto.EventEditDTO;
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

    @PostMapping("/create")
    public ResponseEntity<Integer> createEvent(@RequestBody EventDTO event) throws IOException {
        Event savedEvent = eventService.saveEvent(event);
        return ResponseEntity.ok(savedEvent.getEventID());
    }
    @GetMapping("/all")
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<EventDTO> events = eventService.getAllEvent();
        return ResponseEntity.ok(events);
    }
    @GetMapping("/{eventId}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable int eventId) {
        EventDTO event = eventService.getEventById(eventId);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(event);
    }
    @PutMapping("/edit")
    public ResponseEntity<EventEditDTO> editEvent( @RequestBody EventEditDTO eventEditDTO) throws Exception {
       EventEditDTO eventEdit = eventService.saveEditEvent(eventEditDTO);
       return ResponseEntity.ok(eventEdit);
    }
    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<Boolean> deleteEvent(@PathVariable int eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/edit/{eventId}")
    public ResponseEntity<EventEditDTO> editEvent(@PathVariable int eventId) {
        EventEditDTO eventEdit = eventService.getEventForEdit(eventId);
        return ResponseEntity.ok(eventEdit);
    }
    @GetMapping("/search")
    public ResponseEntity<List<EventDTO>> searchEvents(
            @RequestParam("term") String searchTerm,
            @RequestParam("type") String searchType) {
        System.out.println("searchTerm: " + searchTerm);
        System.out.println("searchType: " + searchType);
        try {
            List<EventDTO> results = eventService.searchEvents(searchTerm, searchType);
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}