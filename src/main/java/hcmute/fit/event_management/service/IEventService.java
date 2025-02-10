package hcmute.fit.event_management.service;

import hcmute.fit.event_management.dto.EventDTO;
import hcmute.fit.event_management.entity.Event;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IEventService {


    List<EventDTO> getAllEvents();

    EventDTO getEventById(int id);

    boolean addEvent(MultipartFile image, EventDTO eventDTO);

    boolean deleteEvent(int eventId);

    boolean updateEvent(MultipartFile image, EventDTO eventDTO);

    boolean addMc(int eventId, int mcId);

    List<EventDTO> getAllEventByEmp(int empId);

    String getListAttendeeByEventId(int eventId);

    String addAttendee(int eventId, MultipartFile attendee);
}
