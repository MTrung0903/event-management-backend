package hcmute.fit.event_management.service;


import hcmute.fit.event_management.dto.EventDTO;
import hcmute.fit.event_management.dto.EventEditDTO;
import hcmute.fit.event_management.entity.Event;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import payload.Response;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IEventService {
    Event saveEvent(EventDTO eventDTO) throws IOException;
    Optional<Event> findById(Integer eventId);
    EventDTO getEventById(int eventId);
    EventDTO convertToDTO(Event event);
    List<EventDTO> getAllEvent();

    EventEditDTO getEventAfterEdit(int eventId);
    EventEditDTO saveEditEvent(EventEditDTO eventEditDTO) throws Exception;
    void deleteEvent(int eventId);
    List<EventDTO> findEventsByName(String eventName);

    List<EventDTO> findEventsStatus(String eventStatus);

    List<EventDTO> findEventsByDate(LocalDateTime eventStart);
    List<EventDTO> findEventsByHost(String eventHost);
    List<EventDTO> findEventsByLocation(String eventLocation);
    List<EventDTO> findEventsByTags(String tag);
    List<EventDTO> findEventsByType(String eventType);
    List<EventDTO> findEventsByNameAndLocation(String name, String location);
    List<EventDTO> searchEventsByNameAndCity(String searchTerm, String cityKey);

    @Transactional
    ResponseEntity<Response> saveEventToDB(EventDTO eventDTO);

    List<EventDTO> getAllEventByHost(String email);
}
