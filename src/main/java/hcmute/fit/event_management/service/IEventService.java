package hcmute.fit.event_management.service;


import hcmute.fit.event_management.dto.EventDTO;
import hcmute.fit.event_management.dto.EventEditDTO;
import hcmute.fit.event_management.entity.Event;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IEventService {
    Event saveEvent(EventDTO eventDTO) throws IOException;
    Event getEvent(Integer eventId);
    Optional<Event> findById(Integer eventId);
    EventDTO getEventById(int eventId);
    EventDTO convertToDTO(Event event);
    List<EventDTO> getAllEvent();
    void editEvent(EventDTO eventDTO) throws Exception;
    EventEditDTO getEventForEdit(int eventId);
    EventEditDTO saveEditEvent(EventEditDTO eventEditDTO) throws Exception;
    void deleteEvent(int eventId);
    List<EventDTO> findEventsByName(String eventName);
    List<EventDTO> findEventsByDate(String eventStart);
    List<EventDTO> findEventsByHost(String eventHost);
    List<EventDTO> findEventsByLocation(String eventLocation);
    List<EventDTO> findEventsByTags(String tag);
    List<EventDTO> findEventsByType(String eventType);
    List<EventDTO> findEventsByNameAndLocation(String name, String location);
    List<EventDTO> searchEvents(String searchTerm, String searchType);

    List<EventDTO> searchEventsByNameAndCity(String searchTerm, String cityKey);
}
