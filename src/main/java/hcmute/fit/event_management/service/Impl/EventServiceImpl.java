package hcmute.fit.event_management.service.Impl;

import com.cloudinary.Cloudinary;
import hcmute.fit.event_management.dto.*;
import hcmute.fit.event_management.entity.*;
import hcmute.fit.event_management.repository.EventRepository;
import hcmute.fit.event_management.repository.SegmentRepository;
import hcmute.fit.event_management.repository.TicketRepository;
import hcmute.fit.event_management.repository.UserRepository;
import hcmute.fit.event_management.service.IEventService;
import hcmute.fit.event_management.service.ISegmentService;
import hcmute.fit.event_management.service.ITicketService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import payload.Response;

import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDateTime;
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
    @Autowired
    private UserRepository userRepository;
    Logger logger = LoggerFactory.getLogger(this.getClass());
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


    public EventEditDTO getEventAfterEdit(int eventId) {
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
    public EventEditDTO saveEditEvent(EventEditDTO eventEditDTO)  {
        Event event = eventRepository.findById(eventEditDTO.getEvent().getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventEditDTO.getEvent().getEventId()));
        int eventId = eventEditDTO.getEvent().getEventId();

        if (eventEditDTO.getEvent().getEventStart().isAfter(eventEditDTO.getEvent().getEventEnd())) {
            logger.error("Event start time {} is after end time {}", eventEditDTO.getEvent().getEventStart(), eventEditDTO.getEvent().getEventEnd());
            throw new IllegalArgumentException("Event start time must be before end time");
        }

        BeanUtils.copyProperties(eventEditDTO.getEvent(), event, "eventLocation", "eventImages", "mediaContent");

        if (eventEditDTO.getEvent().getEventLocation() != null) {
            EventLocation eventLocation = new EventLocation();
            BeanUtils.copyProperties(eventEditDTO.getEvent().getEventLocation(), eventLocation);
            event.setEventLocation(eventLocation);
        }

        if (eventEditDTO.getEvent().getEventImages() != null) {
            event.getEventImages().clear();
            event.getEventImages().addAll(eventEditDTO.getEvent().getEventImages());
        }

        if (eventEditDTO.getEvent().getMediaContent() != null) {
            event.getMediaContent().clear();
            event.getMediaContent().addAll(eventEditDTO.getEvent().getMediaContent());
        }

        eventRepository.save(event);
        logger.info("Event {} edited successfully", event.getEventName());
        return getEventAfterEdit(eventId);
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
    public List<EventDTO> findEventsByDate(LocalDateTime eventStart) {
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

    @Transactional
    @Override
    public ResponseEntity<Response> saveEventToDB(EventDTO eventDTO) {
        // Tìm user theo email
        String name = eventDTO.getEventHost();
        Optional<User> userOpt = userRepository.findByFullName(name);
        if (!userOpt.isPresent()) {
            logger.error("User with name {} not found", name);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(404, "Not Found", "User not found"));
        }
        User user = userOpt.get();

        // Kiểm tra eventStart và eventEnd
        if (eventDTO.getEventStart().isAfter(eventDTO.getEventEnd())) {
            logger.error("Event start time {} is after end time {}", eventDTO.getEventStart(), eventDTO.getEventEnd());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response(400, "Bad Request", "Event start time must be before end time"));
        }

        // Tạo Event
        Event event = new Event();
        BeanUtils.copyProperties(eventDTO, event, "eventLocation", "eventImages", "mediaContent","eventLocation");
        event.setEventHost(name);
        event.setUser(user);

        // Xử lý EventLocation
        EventLocation eventLocation = new EventLocation();
        EventLocationDTO locationDTO = eventDTO.getEventLocation();
        if (locationDTO != null) {
            BeanUtils.copyProperties(locationDTO, eventLocation);
            event.setEventLocation(eventLocation);
        }

        // Xử lý eventImages và mediaContent
        if (eventDTO.getEventImages() != null) {
            event.setEventImages(new ArrayList<>(eventDTO.getEventImages()));
        }
        if (eventDTO.getMediaContent() != null) {
            event.setMediaContent(new ArrayList<>(eventDTO.getMediaContent()));
        }

        // Lưu Event
        eventRepository.save(event);

        logger.info("Event {} created successfully by user {}", event.getEventName(), name);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response(201, "Success", "Event created successfully"));
    }
}