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
    // Tìm kiếm sự kiện theo tên
    @GetMapping("/search/name")
    public ResponseEntity<List<EventDTO>> getEventsByName(
            @RequestParam("name") String eventName) {
        List<EventDTO> events = eventService.findEventsByName(eventName);
        return ResponseEntity.ok(events);
    }

    // Tìm kiếm sự kiện theo ngày diễn ra
    @GetMapping("/search/date")
    public ResponseEntity<List<EventDTO>> getEventsByDate(
            @RequestParam("date") String eventStart) {
        List<EventDTO> events = eventService.findEventsByDate(eventStart);
        return ResponseEntity.ok(events);
    }

    // Tìm kiếm sự kiện theo host
    @GetMapping("/search/host")
    public ResponseEntity<List<EventDTO>> getEventsByHost(
            @RequestParam("host") String eventHost) {
        List<EventDTO> events = eventService.findEventsByHost(eventHost);
        return ResponseEntity.ok(events);
    }

    // Tìm kiếm sự kiện theo địa điểm
    @GetMapping("/search/location")
    public ResponseEntity<List<EventDTO>> getEventsByLocation(
            @RequestParam("location") String eventLocation) {
        List<EventDTO> events = eventService.findEventsByLocation(eventLocation);
        return ResponseEntity.ok(events);
    }

    // Tìm kiếm sự kiện theo tags
    @GetMapping("/search/tags")
    public ResponseEntity<List<EventDTO>> getEventsByTags(
            @RequestParam("tag") String tag) {
        List<EventDTO> events = eventService.findEventsByTags(tag);
        return ResponseEntity.ok(events);
    }

    // Tìm kiếm sự kiện theo loại
    @GetMapping("/search/type")
    public ResponseEntity<List<EventDTO>> getEventsByType(
            @RequestParam("type") String eventType) {
        List<EventDTO> events = eventService.findEventsByType(eventType);
        return ResponseEntity.ok(events);
    }
    @GetMapping("/search/name-and-location")
    public ResponseEntity<List<EventDTO>> getEventsByNameAndLocation(
            @RequestParam("name") String name,
            @RequestParam("location") String location) {
        if (name == null || name.trim().isEmpty() || location == null || location.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<EventDTO> events = eventService.findEventsByNameAndLocation(name, location);
        return events.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(events);
    }
    @GetMapping("/edit/{eventId}")
    public ResponseEntity<EventEditDTO> editEvent(@PathVariable int eventId) {
        EventEditDTO eventEdit = eventService.getEventForEdit(eventId);
        return ResponseEntity.ok(eventEdit);
    }
}