package hcmute.fit.event_management.service;


import hcmute.fit.event_management.dto.EventDTO;
import hcmute.fit.event_management.entity.Event;

import java.io.IOException;
import java.util.Optional;

public interface IEventService {

    Event saveEvent(EventDTO eventDTO) throws IOException;

    Event getEvent(Integer eventId);


    Optional<Event> findById(Integer integer);
}
