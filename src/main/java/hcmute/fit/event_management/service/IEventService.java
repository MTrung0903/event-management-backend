package hcmute.fit.event_management.service;


import hcmute.fit.event_management.dto.EventDTO;
import hcmute.fit.event_management.entity.Event;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IEventService {

    Event saveEvent(EventDTO eventDTO) throws IOException;

    Event getEvent(Integer eventId);


    Optional<Event> findById(Integer integer);

    EventDTO getEventById(int eventId);

    EventDTO convertToDTO(Event event);

    List<EventDTO> findEventsByName(String eventName);

    List<EventDTO> findEventsByDate(String eventStart);

    List<EventDTO> findEventsByHost(String eventHost);

    List<EventDTO> findEventsByLocation(String eventLocation);

    List<EventDTO> findEventsByTags(String tag);

    List<EventDTO> findEventsByType(String eventType);
}
