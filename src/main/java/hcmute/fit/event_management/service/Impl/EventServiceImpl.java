package hcmute.fit.event_management.service.Impl;

import com.cloudinary.Cloudinary;
import hcmute.fit.event_management.dto.*;
import hcmute.fit.event_management.entity.Event;
import hcmute.fit.event_management.entity.EventLocation;
import hcmute.fit.event_management.entity.Segment;
import hcmute.fit.event_management.entity.Speaker;
import hcmute.fit.event_management.entity.Ticket;
import hcmute.fit.event_management.repository.EventRepository;
import hcmute.fit.event_management.repository.SegmentRepository;
import hcmute.fit.event_management.repository.TicketRepository;
import hcmute.fit.event_management.service.IEventService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
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
    private SegmentRepository segmentRepository;

    @Autowired
    private ISegmentService segmentService;

    @Autowired
    private ITicketService ticketService;


    private static final Map<String, String> cityMap = Map.of(
            "ho-chi-minh", "TP. Hồ Chí Minh",
            "ha-noi", "Hà Nội",
            "da-nang", "Đà Nẵng",
            "hai-phong", "Hải Phòng",
            "can-tho", "Cần Thơ",
            "nha-trang", "Nha Trang",
            "da-lat", "Đà Lạt",
            "binh-duong", "Bình Dương",
            "dong-nai", "Đồng Nai",
            "quang-ninh", "Quảng Ninh"
    );


    private String getCityDisplayName(String slug) {
        return cityMap.getOrDefault(slug, slug);
    }

    @Override
    public Event saveEvent(EventDTO eventDTO) throws IOException {
        Event event = new Event();
        BeanUtils.copyProperties(eventDTO, event, "eventLocation");

        EventLocation eventLocation = new EventLocation();
        EventLocationDTO locationDTO = eventDTO.getEventLocation();
        if (locationDTO != null) {
            BeanUtils.copyProperties(locationDTO, eventLocation);

            event.setEventLocation(eventLocation);
        }

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
        Event event = findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
        EventDTO dto = new EventDTO();
        BeanUtils.copyProperties(event, dto, "eventLocation");

        EventLocationDTO locationDTO = new EventLocationDTO();
        if (event.getEventLocation() != null) {
            BeanUtils.copyProperties(event.getEventLocation(), locationDTO);
            locationDTO.setCity(getCityDisplayName(locationDTO.getCity()));
            dto.setEventLocation(locationDTO);
        }

        dto.setEventId(event.getEventID());

        List<String> imageUrls = event.getEventImages().stream()
                .map(publicId -> cloudinary.url().generate(publicId))
                .collect(Collectors.toList());
        dto.setEventImages(imageUrls);

        List<String> mediaUrls = event.getMediaContent().stream()
                .map(publicId -> cloudinary.url().generate(publicId))
                .collect(Collectors.toList());
        dto.setMediaContent(mediaUrls);

        return dto;
    }

    @Override
    public EventDTO convertToDTO(Event event) {
        EventDTO dto = new EventDTO();
        BeanUtils.copyProperties(event, dto, "eventLocation");
        dto.setEventId(event.getEventID());
        EventLocationDTO locationDTO = new EventLocationDTO();
        if (event.getEventLocation() != null) {
            BeanUtils.copyProperties(event.getEventLocation(), locationDTO);
            locationDTO.setCity(getCityDisplayName(locationDTO.getCity()));
            dto.setEventLocation(locationDTO);
        }
        List<String> imageUrls = event.getEventImages().stream()
                .map(publicId -> cloudinary.url().generate(publicId))
                .collect(Collectors.toList());
        dto.setEventImages(imageUrls);

        List<String> mediaUrls = event.getMediaContent().stream()
                .map(publicId -> cloudinary.url().generate(publicId))
                .collect(Collectors.toList());
        dto.setMediaContent(mediaUrls);

        return dto;
    }

    @Override
    public List<EventDTO> getAllEvent() {
        List<Event> events = eventRepository.findAll();
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void editEvent(EventDTO eventDTO) throws Exception {
        Optional<Event> existingEventOpt = eventRepository.findById(eventDTO.getEventId());
        if (!existingEventOpt.isPresent()) {
            throw new Exception("Event with ID " + eventDTO.getEventId() + " not found");
        }
        Event event = existingEventOpt.get();
        BeanUtils.copyProperties(eventDTO, event, "eventLocation");

        EventLocation eventLocation = new EventLocation();
        if (eventDTO.getEventLocation() != null) {
            BeanUtils.copyProperties(eventDTO.getEventLocation(), eventLocation);
            event.setEventLocation(eventLocation);
        }

        if (eventDTO.getEventImages() != null) {
            event.getEventImages().clear();
            event.getEventImages().addAll(eventDTO.getEventImages());
        }

        if (eventDTO.getMediaContent() != null) {
            event.getMediaContent().clear();
            event.getMediaContent().addAll(eventDTO.getMediaContent());
        }

        eventRepository.save(event);
    }

    @Override
    public EventEditDTO getEventForEdit(int eventId) {
        EventDTO event = getEventById(eventId);
        List<Ticket> tickets = ticketRepository.findByEventID(eventId);
        List<TicketDTO> ticketDTOs = new ArrayList<>();
        for (Ticket ticket : tickets) {
            TicketDTO ticketDTO = new TicketDTO();
            BeanUtils.copyProperties(ticket, ticketDTO);
            ticketDTOs.add(ticketDTO);
        }
        List<SegmentDTO> segments = getAllSegments(eventId);
        EventEditDTO eventEdit = new EventEditDTO();
        eventEdit.setEvent(event);
        eventEdit.setTicket(ticketDTOs);
        eventEdit.setSegment(segments);
        return eventEdit;
    }

    @Override
    public EventEditDTO saveEditEvent(EventEditDTO eventEditDTO) throws Exception {
        Event event = eventRepository.findById(eventEditDTO.getEvent().getEventId())
                .orElseThrow(() -> new Exception("Event with ID " + eventEditDTO.getEvent().getEventId() + " not found"));
        int eventId = eventEditDTO.getEvent().getEventId();
        BeanUtils.copyProperties(eventEditDTO.getEvent(), event, "eventLocation");

        EventLocation eventLocation = new EventLocation();
        if (eventEditDTO.getEvent().getEventLocation() != null) {
            BeanUtils.copyProperties(eventEditDTO.getEvent().getEventLocation(), eventLocation);
            event.setEventLocation(eventLocation);
        }

        for (TicketDTO ticketDTO : eventEditDTO.getTicket()) {
            ticketService.saveEditTicket(eventId, ticketDTO);
        }
        for (SegmentDTO segmentDTO : eventEditDTO.getSegment()) {
            segmentService.saveEditSegment(eventId, segmentDTO);
        }
        eventRepository.save(event);

        return getEventForEdit(eventId);
    }

    @Override
    public void deleteEvent(int eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new RuntimeException("Event not found with id " + eventId);
        }
        segmentService.deleteSegmentByEventId(eventId);
        ticketService.deleteTicketByEventId(eventId);
        eventRepository.delete(event.get());
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
        List<Event> events = eventRepository.findByEventHostContainingIgnoreCase(eventHost);
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDTO> findEventsByLocation(String eventLocation) {
        List<Event> events = eventRepository.findByEventLocationCityContainingIgnoreCase(eventLocation);
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
    public List<EventDTO> findEventsByType(String eventType) {
        List<Event> events = eventRepository.findByEventTypeContainingIgnoreCase(eventType);
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDTO> findEventsByNameAndLocation(String name, String location) {
        List<Event> eventsByLocation = eventRepository.findByEventLocationCityContainingIgnoreCase(location);
        List<Event> filteredEvents = eventsByLocation.stream()
                .filter(event -> event.getEventName() != null &&
                        event.getEventName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
        return filteredEvents.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDTO> searchEvents(String searchTerm, String searchType) {
        if (searchTerm == null || searchTerm.trim().isEmpty() || searchType == null) {
            return getAllEvent();
        }

        List<Event> events;
        switch (searchType.toLowerCase()) {
            case "eventname":
                events = eventRepository.findByEventNameContainingIgnoreCase(searchTerm);
                break;
            case "city":
                events = eventRepository.findByEventLocationCityContainingIgnoreCase(searchTerm);
                break;
            case "venuename":
                events = eventRepository.findByEventLocationVenueNameContainingIgnoreCase(searchTerm);
                break;
            case "eventtag":
                events = eventRepository.findByTagsContainingIgnoreCase(searchTerm);
                break;
            case "eventtype":
                events = eventRepository.findByEventTypeContainingIgnoreCase(searchTerm);
                break;
            case "eventhost":
                events = eventRepository.findByEventHostContainingIgnoreCase(searchTerm);
                break;
            default:
                throw new IllegalArgumentException("Invalid search type: " + searchType);
        }

        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Hàm phụ trợ để lấy danh sách segment
    private List<SegmentDTO> getAllSegments(int eventId) {
        List<Segment> list = segmentRepository.findByEventId(eventId);
        List<SegmentDTO> dtos = new ArrayList<>();
        for (Segment segment : list) {
            SegmentDTO dto = new SegmentDTO();
            Speaker speaker = segment.getSpeaker();
            SpeakerDTO speakerDTO = new SpeakerDTO();
            BeanUtils.copyProperties(speaker, speakerDTO);
            String urlImage = cloudinary.url().generate(speaker.getSpeakerImage());
            speakerDTO.setSpeakerImage(urlImage);
            BeanUtils.copyProperties(segment, dto);
            dto.setEventID(eventId);
            dto.setStartTime(segment.getStartTime());
            dto.setEndTime(segment.getEndTime());
            dto.setSegmentId(segment.getSegmentId());
            dto.setSpeaker(speakerDTO);
            dtos.add(dto);
        }
        return dtos;
    }
    // ham bo dau tieng viet
    private String removeDiacritics(String str) {
        if (str == null) return null;
        String normalized = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").toLowerCase();
    }
    @Override
    public List<EventDTO> searchEventsByNameAndCity(String searchTerm, String cityKey) {
        if (searchTerm == null || searchTerm.trim().isEmpty() || cityKey == null || cityKey.trim().isEmpty()) {
            return getAllEvent();
        }
        if (cityKey.isEmpty()) {
            throw new IllegalArgumentException("Invalid city key: " + cityKey);
        }
        List<Event> eventsByCity = eventRepository.findByEventLocationCityContainingIgnoreCase(cityKey);

        List<Event> filteredEvents = eventsByCity.stream()
                .filter(event -> event.getEventName() != null &&
                        removeDiacritics(event.getEventName()).contains(searchTerm))
                .collect(Collectors.toList());

        return filteredEvents.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}