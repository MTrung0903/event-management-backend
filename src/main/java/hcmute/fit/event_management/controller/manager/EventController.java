package hcmute.fit.event_management.controller.manager;

import hcmute.fit.event_management.dto.EventDTO;
import hcmute.fit.event_management.dto.EventDetailDTO;
import hcmute.fit.event_management.dto.EventEditDTO;
import hcmute.fit.event_management.entity.Event;
import hcmute.fit.event_management.service.IOrganizerService;
import hcmute.fit.event_management.service.ISegmentService;
import hcmute.fit.event_management.service.ISponsorService;
import hcmute.fit.event_management.service.ITicketService;
import hcmute.fit.event_management.service.Impl.EventServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import payload.Response;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventServiceImpl eventService;

    @Autowired
    private ISegmentService segmentService;

    @Autowired
    private ITicketService ticketService;

    @Autowired
    private ISponsorService sponsorService;
    @Autowired
    private IOrganizerService organizerService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<Response> createEvent(@RequestBody EventDTO event) throws IOException {

        return eventService.saveEventToDB(event);
    }
    @PostMapping("/create-event")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<Response> saveEvent(@RequestBody EventDTO event)  {
       return eventService.saveEventToDB(event);
    }
    @GetMapping("/all")
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<EventDTO> events = eventService.getAllEvent();
        return ResponseEntity.ok(events);
    }
    @GetMapping("detail/{eventId}")
    public ResponseEntity<EventDetailDTO> getEventById(@PathVariable int eventId) {
        EventDetailDTO detailDTO = new EventDetailDTO();
        detailDTO.setEvent(eventService.getEventById(eventId));
        detailDTO.setTickets(ticketService.getTicketsByEventId(eventId));
        detailDTO.setSegments(segmentService.getAllSegments(eventId));
        if(sponsorService.getAllSponsorsInEvent(eventId) !=null){
            detailDTO.setSponsors(sponsorService.getAllSponsorsInEvent(eventId));
        }

        if(detailDTO.getEvent()!=null && detailDTO.getEvent().getEventHost() != null) {
            String eventHost = detailDTO.getEvent().getEventHost();
            detailDTO.setOrganizer(organizerService.getOrganizerInforByEventHost(eventHost));
        }

        return ResponseEntity.ok(detailDTO);
    }

    @PutMapping("/edit")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<EventEditDTO> editEvent( @RequestBody EventEditDTO eventEditDTO) throws Exception {
       EventEditDTO eventEdit = eventService.saveEditEvent(eventEditDTO);
       return ResponseEntity.ok(eventEdit);
    }
    @DeleteMapping("/delete/{eventId}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<Boolean> deleteEvent(@PathVariable int eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/edit/{eventId}")
    @PreAuthorize("hasRole('ORGANIZER')")
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
    @GetMapping("/get-all-event-by-org/{email}")
    public ResponseEntity<List<EventDTO>> getAllEventsByOrg(@PathVariable String email){
        List<EventDTO> events = eventService.getAllEventByHost(email);
        return ResponseEntity.ok(events);
    }
    @GetMapping("search/by-host/{eventHost}")
    public ResponseEntity<List<EventDTO>> searchEventsByHost(@PathVariable String eventHost){
        List<EventDTO> events = eventService.findEventsByHost(eventHost);
        return ResponseEntity.ok(events);
    }
    @GetMapping("search/by-tag/{tag}")
    public ResponseEntity<List<EventDTO>> searchEventsByTag(@PathVariable String tag){
        List<EventDTO> events = eventService.findEventsByTags(tag);
        return ResponseEntity.ok(events);
    }
    @GetMapping("/search/by-name/{eventName}")
    public ResponseEntity<List<EventDTO>> searchEventsByName(@PathVariable String eventName){
        List<EventDTO> events = eventService.findEventsByName(eventName);
        return ResponseEntity.ok(events);
    }
    @GetMapping("/search/by-status/{eventStatus}")
    public ResponseEntity<List<EventDTO>> searchEventsByStatus(@PathVariable String eventStatus){
        List<EventDTO> events = eventService.findEventsStatus(eventStatus);
        return ResponseEntity.ok(events);
    }
    @GetMapping("/search/by-event-start/{eventStart}")
    public ResponseEntity<List<EventDTO>> searchEventsByEventStart(@PathVariable LocalDateTime eventStart){
        List<EventDTO> events = eventService.findEventsByDate(eventStart);
        return ResponseEntity.ok(events);
    }

}