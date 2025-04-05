package hcmute.fit.event_management.service.Impl;

import com.cloudinary.Cloudinary;
import hcmute.fit.event_management.dto.*;
import hcmute.fit.event_management.entity.Event;
import hcmute.fit.event_management.entity.Session;
import hcmute.fit.event_management.entity.Speaker;
import hcmute.fit.event_management.entity.Ticket;
import hcmute.fit.event_management.repository.EventRepository;
import hcmute.fit.event_management.repository.SessionRepository;
import hcmute.fit.event_management.repository.TicketRepository;
import hcmute.fit.event_management.service.IEventService;
import hcmute.fit.event_management.service.ISessionService;
import hcmute.fit.event_management.service.ITicketService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements IEventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private SessionRepository sessionRepository;


    @Override
    public Event saveEvent(EventDTO eventDTO) throws IOException {
        Event event = new Event();
        BeanUtils.copyProperties(eventDTO, event);
        return eventRepository.save(event);
    }

    @Override
    public Event getEvent(Integer eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    @Override
    public Optional<Event> findById(Integer eventId) {
        return eventRepository.findById(eventId);
    }

    @Override
    public EventDTO getEventById(int eventId) {
        EventDTO dto = new EventDTO();
        Event event = findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
        BeanUtils.copyProperties(event, dto);
        dto.setEventId(event.getEventID());
        dto.setTags(event.getTags());
        dto.setEventVisibility(event.getEventVisibility());
        dto.setPublishTime(event.getPublishTime());
        dto.setRefunds(event.getRefunds());
        dto.setValidityDays(event.getValidityDays());

        // Tạo URL từ public_id cho eventImages
        List<String> imageUrls = event.getEventImages().stream()
                .map(publicId -> cloudinary.url().generate(publicId))
                .collect(Collectors.toList());
        dto.setEventImages(imageUrls);

        dto.setTextContent(event.getTextContent());

        // Tạo URL từ public_id cho mediaContent
        List<String> mediaUrls = event.getMediaContent().stream()
                .map(publicId -> cloudinary.url().generate(publicId))
                .collect(Collectors.toList());
        dto.setMediaContent(mediaUrls);

        return dto;
    }

    @Override
    public EventDTO convertToDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setEventId(event.getEventID());
        dto.setEventName(event.getEventName());
        dto.setEventDesc(event.getEventDesc());
        dto.setEventType(event.getEventType());
        dto.setEventHost(event.getEventHost());
        dto.setEventStatus(event.getEventStatus());
        dto.setEventStart(event.getEventStart());
        dto.setEventEnd(event.getEventEnd());
        dto.setEventLocation(event.getEventLocation());
        dto.setTags(event.getTags());
        dto.setEventVisibility(event.getEventVisibility());
        dto.setPublishTime(event.getPublishTime());
        dto.setRefunds(event.getRefunds());
        dto.setValidityDays(event.getValidityDays());
        dto.setEventImages(event.getEventImages());
        dto.setTextContent(event.getTextContent());
        dto.setMediaContent(event.getMediaContent());
        return dto;
    }


    @Override
    public List<EventDTO> findEventsByName(String eventName) {
        List<Event> events = eventRepository.findByEventNameContainingIgnoreCase(eventName);
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<EventDTO> findEventsByDate(String eventStart) {
        List<Event> events = eventRepository.findByEventStart(eventStart);
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<EventDTO> findEventsByHost(String eventHost) {
        List<Event> events = eventRepository.findByEventHost(eventHost);
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<EventDTO> findEventsByLocation(String eventLocation) {
        List<Event> events = eventRepository.findByEventLocationContainingIgnoreCase(eventLocation);
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<EventDTO> findEventsByTags(String tag) {
        List<Event> events = eventRepository.findByTagsContainingIgnoreCase(tag);
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDTO> getAllEvent(){
        List<Event> events = eventRepository.findAll();
        List<EventDTO> dtos = new ArrayList<>();
        for (Event event : events) {
            EventDTO dto = convertToDTO(event);
            // Tạo URL từ public_id cho eventImages
            List<String> imageUrls = event.getEventImages().stream()
                    .map(publicId -> cloudinary.url().generate(publicId))
                    .collect(Collectors.toList());
            dto.setEventImages(imageUrls);
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public List<EventDTO> findEventsByType(String eventType) {
        List<Event> events = eventRepository.findByEventType(eventType);
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public List<EventDTO> findEventsByNameAndLocation(String name, String location) {
        List<Event> eventsByLocation = eventRepository.findByEventLocationContainingIgnoreCase(location);
        List<Event> filteredEvents = eventsByLocation.stream()
                .filter(event -> event.getEventName() != null &&
                        event.getEventName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
        return filteredEvents.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    @Override
    public void editEvent(EventDTO eventDTO) throws Exception{
        Optional<Event> existingEventOpt = eventRepository.findById(eventDTO.getEventId());

        if (!existingEventOpt.isPresent()) {
            throw new Exception("Event with ID " + eventDTO.getEventId() + " not found");
        }
        Event event = existingEventOpt.get();
        BeanUtils.copyProperties(eventDTO, event);
        if (eventDTO.getEventImages() != null) {
            event.getEventImages().clear();
            event.getEventImages().addAll(eventDTO.getEventImages());
        }
        event.setTextContent(eventDTO.getTextContent());
        if (eventDTO.getMediaContent() != null) {
            event.getMediaContent().clear();
            event.getMediaContent().addAll(eventDTO.getMediaContent());
        }
        eventRepository.save(event);
    }
    public List<SessionDTO> getAllSessions(int eventId) {
        List<Session> list = sessionRepository.findByEventId(eventId);
        List<SessionDTO> dtos = new ArrayList<>();
        for (Session session : list) {
            SessionDTO dto = new SessionDTO();
            Speaker speaker = session.getSpeaker();
            SpeakerDTO speakerDTO = new SpeakerDTO();
            BeanUtils.copyProperties(speaker, speakerDTO);
            String urlImage = cloudinary.url().generate(speaker.getSpeakerImage());
            System.out.println("day la url image cua speaker : " + urlImage);

            speakerDTO.setSpeakerImage(urlImage);
            BeanUtils.copyProperties(session, dto);
            dto.setEventID(eventId);
            dto.setStartTime(session.getStartTime());
            dto.setEndTime(session.getEndTime());
            dto.setSessionId(session.getSessionId());
            dto.setSpeaker(speakerDTO);
            dtos.add(dto);
        }
        return dtos;

    }
    @Override
    public EventEditDTO getEventForEdit(int eventId){
        EventDTO event = getEventById(eventId);
        List<Ticket> tickets = ticketRepository.findByEventID(eventId);
        List<TicketDTO> ticketDTOs = new ArrayList<>();
        for (Ticket ticket : tickets) {
            TicketDTO ticketDTO = new TicketDTO();
            BeanUtils.copyProperties(ticket, ticketDTO);
            ticketDTOs.add(ticketDTO);
        }
        List<SessionDTO> sessions = getAllSessions(eventId);
        EventEditDTO eventEdit = new EventEditDTO();
        eventEdit.setEvent(event);
        eventEdit.setTicket(ticketDTOs);
        eventEdit.setSession(sessions);
        return eventEdit;
    }
}
