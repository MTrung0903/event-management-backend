package hcmute.fit.event_management.controller.manager;

import hcmute.fit.event_management.dto.EventDTO;
import hcmute.fit.event_management.dto.EventEditDTO;
import hcmute.fit.event_management.entity.Event;
import hcmute.fit.event_management.service.Impl.EventServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import payload.Response;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventServiceImpl eventService;

    @PostMapping("/create")
    @PreAuthorize("hasRole(ORGANIZER)")
    public ResponseEntity<Integer> createEvent(@RequestBody EventDTO event) throws IOException {
        Event savedEvent = eventService.saveEvent(event);
        return ResponseEntity.ok(savedEvent.getEventID());
    }
    @PostMapping("/create-event")
    @PreAuthorize("hasRole(ORGANIZER)")
    public ResponseEntity<Response> saveEvent(@RequestBody EventDTO event)  {
       return eventService.saveEventToDB(event);
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
    @PreAuthorize("hasRole(ORGANIZER)")
    public ResponseEntity<EventEditDTO> editEvent( @RequestBody EventEditDTO eventEditDTO) throws Exception {
       EventEditDTO eventEdit = eventService.saveEditEvent(eventEditDTO);
       return ResponseEntity.ok(eventEdit);
    }
    @DeleteMapping("/delete/{eventId}")
    @PreAuthorize("hasRole(ORGANIZER)")
    public ResponseEntity<Boolean> deleteEvent(@PathVariable int eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/edit/{eventId}")
    @PreAuthorize("hasRole(ORGANIZER)")
    public ResponseEntity<EventEditDTO> editEvent(@PathVariable int eventId) {
        EventEditDTO eventEdit = eventService.getEventAfterEdit(eventId);
        return ResponseEntity.ok(eventEdit);
    }

    @GetMapping("/search/by-name-and-city")
    public ResponseEntity<List<EventDTO>> searchEventsByNameAndCity(
            @RequestParam("term") String searchTerm,
            @RequestParam("city") String cityKey) {
        try {
            List<EventDTO> results = eventService.searchEventsByNameAndCity(searchTerm, cityKey);
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    @GetMapping("search/by-type/{categoryName}")
    public ResponseEntity<List<EventDTO>> searchEventsByEventType(@PathVariable String categoryName){
        List<EventDTO> eventsSearchByType = eventService.findEventsByType(categoryName);
        return ResponseEntity.ok(eventsSearchByType);
    }
    @GetMapping("search/by-city/{city}")
    public ResponseEntity<List<EventDTO>> searchEventsByCity(@PathVariable String city){
        List<EventDTO> events = eventService.findEventsByLocation( city );
        return ResponseEntity.ok(events);
    }

}