package hcmute.fit.event_management.service.Impl;

import com.cloudinary.Cloudinary;
import hcmute.fit.event_management.dto.*;
import hcmute.fit.event_management.entity.*;
import hcmute.fit.event_management.repository.*;
import hcmute.fit.event_management.service.IEventService;
import hcmute.fit.event_management.service.ISegmentService;
import hcmute.fit.event_management.service.ITicketService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import payload.Response;


import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.*;
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
    @Autowired
    private TransactionRepository transactionRepository;
   @Autowired
           private VNPAYService vnpayService;

    Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final Map<String, String> cityMap = Map.ofEntries(
            Map.entry("ho-chi-minh", "TP. Hồ Chí Minh"),
            Map.entry("ha-noi", "Hà Nội"),
            Map.entry("da-nang", "Đà Nẵng"),
            Map.entry("hai-phong", "Hải Phòng"),
            Map.entry("can-tho", "Cần Thơ"),
            Map.entry("nha-trang", "Nha Trang"),
            Map.entry("da-lat", "Đà Lạt"),
            Map.entry("binh-duong", "Bình Dương"),
            Map.entry("dong-nai", "Đồng Nai"),
            Map.entry("quang-ninh", "Quảng Ninh"),
            Map.entry("bac-lieu", "Bạc Liêu")
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
        updateEventStatus();
        Event event = findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
        EventDTO dto = convertToDTO(event);
        return dto;
    }

    private void updateEventStatus() {
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<Event> events = eventRepository.findAll();
        for (Event event : events) {
            if (event.getEventEnd() != null) {
                LocalDateTime eventEndDate = event.getEventEnd().withHour(0).withMinute(0).withSecond(0).withNano(0);
                if (eventEndDate.isEqual(today) && !"Complete".equals(event.getEventStatus())) {
                    event.setEventStatus("Complete");
                    eventRepository.save(event);
                    logger.info("Updated event {} to status Complete", event.getEventName());
                }
            }
        }
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
        updateEventStatus();
        List<Event> events = eventRepository.findAll();
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EventEditDTO getEventAfterEdit(int eventId) {
        updateEventStatus();
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
    @Transactional
    public EventEditDTO saveEditEvent(EventEditDTO eventEditDTO) throws Exception {
        Event event = eventRepository.findById(eventEditDTO.getEvent().getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventEditDTO.getEvent().getEventId()));
        int eventId = eventEditDTO.getEvent().getEventId();

        if (eventEditDTO.getEvent().getEventStart().isAfter(eventEditDTO.getEvent().getEventEnd())) {
            logger.error("Event start time {} is after end time {}", eventEditDTO.getEvent().getEventStart(), eventEditDTO.getEvent().getEventEnd());
            throw new IllegalArgumentException("Event start time must be before end time");
        }

        // Update event details
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

        // Process tickets
        List<TicketDTO> ticketDTOs = eventEditDTO.getTicket();
        if (ticketDTOs != null) {
            for (TicketDTO ticketDTO : ticketDTOs) {
                if (ticketDTO.getTicketId() == 0) {
                    ticketService.addTicket(eventId, ticketDTO);
                } else {
                    ticketService.saveEditTicket(eventId, ticketDTO);
                }
            }
        }

        // Process segments
        List<SegmentDTO> segmentDTOs = eventEditDTO.getSegment();
        if (segmentDTOs != null) {
            for (SegmentDTO segmentDTO : segmentDTOs) {
                if (segmentDTO.getSegmentId() == 0) {
                    segmentService.addSegment(eventId, segmentDTO);
                } else {
                    segmentService.saveEditSegment(eventId, segmentDTO);
                }
            }
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
        updateEventStatus();
        List<Event> events = eventRepository.findByEventNameContainingIgnoreCase(eventName);
        return events.stream()
                .filter(event -> !"Complete".equals(event.getEventStatus()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDTO> findEventsStatus(String eventStatus) {
        updateEventStatus();
        List<Event> events = eventRepository.findByEventStatusIgnoreCase(eventStatus);
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDTO> findEventsByDate(LocalDateTime eventStart) {
        updateEventStatus();
        List<Event> events = eventRepository.findByEventStart(eventStart);
        return events.stream()
                .filter(event -> !"Complete".equals(event.getEventStatus()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDTO> findEventsByHost(String eventHost) {
        updateEventStatus();
        List<Event> events = eventRepository.findByEventHostContainingIgnoreCase(eventHost);
        return events.stream()

                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDTO> findEventsByLocation(String eventLocation) {
        updateEventStatus();
        List<Event> events = eventRepository.findByEventLocationCityContainingIgnoreCase(eventLocation);
        return events.stream()
                .filter(event -> !"Complete".equals(event.getEventStatus()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDTO> findEventsByTags(String tag) {
        updateEventStatus();
        List<Event> events = eventRepository.findByTagsContainingIgnoreCase(tag);
        return events.stream()
                .filter(event -> !"Complete".equals(event.getEventStatus()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDTO> findEventsByType(String eventType) {
        updateEventStatus();
        List<Event> events = eventRepository.findByEventTypeContainingIgnoreCase(eventType);
        return events.stream()
                .filter(event -> !"Complete".equals(event.getEventStatus()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    @Override
    public List<EventDTO> findEventsByCurrentWeek() {
        updateEventStatus();
        List<Event> events = eventRepository.findEventsByCurrentWeek();
        return events.stream()
                .filter(event -> !"Complete".equals(event.getEventStatus()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    @Override
    public List<EventDTO> findEventsByCurrentMonth() {
        updateEventStatus();
        List<Event> events = eventRepository.findEventsByCurrentMonth();
        return events.stream()
                .filter(event -> !"Complete".equals(event.getEventStatus()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    @Override
    public List<EventDTO> findEventsByTicketType(String type) {
        updateEventStatus();
        List<Event> events = eventRepository.findEventsByTicketType(type);
        return events.stream()
                .filter(event -> !"Complete".equals(event.getEventStatus()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    @Override
    public List<EventDTO> searchEventsByMultipleFilters(String eventCategory, String eventLocation, String eventStart, String ticketType) {
        updateEventStatus();
        List<Event> resultEvents = eventRepository.findAll();

        // Filter by event category
        if (eventCategory != null && !eventCategory.equals("all-types")) {
            List<Event> categoryEvents = eventRepository.findByEventTypeContainingIgnoreCase(eventCategory);
            resultEvents = resultEvents.stream()
                    .filter(categoryEvents::contains)
                    .collect(Collectors.toList());
        }

        // Filter by event location
        if (eventLocation != null && !eventLocation.equals("all-locations")) {
            List<Event> locationEvents = eventRepository.findByEventLocationCityContainingIgnoreCase(eventLocation);
            resultEvents = resultEvents.stream()
                    .filter(locationEvents::contains)
                    .collect(Collectors.toList());
        }

        // Filter by event start time
        if (eventStart != null && !eventStart.equals("all-times")) {
            List<Event> timeEvents;
            if (eventStart.equals("this-week")) {
                timeEvents = eventRepository.findEventsByCurrentWeek();
            } else if (eventStart.equals("this-month")) {
                timeEvents = eventRepository.findEventsByCurrentMonth();
            } else {
                timeEvents = eventRepository.findAll(); // Default case, no time filter
            }
            resultEvents = resultEvents.stream()
                    .filter(timeEvents::contains)
                    .collect(Collectors.toList());
        }

        // Filter by ticket type
        if (ticketType != null && !ticketType.equals("all-types")) {
            List<Event> ticketEvents = eventRepository.findEventsByTicketType(ticketType);
            resultEvents = resultEvents.stream()
                    .filter(ticketEvents::contains)
                    .collect(Collectors.toList());
        }

        // Remove completed events and convert to DTO
        return resultEvents.stream()
                .filter(event -> !"Complete".equals(event.getEventStatus()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    @Override
    public List<EventDTO> findEventsByNameAndLocation(String name, String location) {
        updateEventStatus();
        List<Event> eventsByLocation = eventRepository.findByEventLocationCityContainingIgnoreCase(location);
        List<Event> filteredEvents = eventsByLocation.stream()
                .filter(event -> event.getEventName() != null &&
                        event.getEventName().toLowerCase().contains(name.toLowerCase()) &&
                        !"Complete".equals(event.getEventStatus()))
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

    private String removeDiacritics(String str) {
        if (str == null) return null;
        String normalized = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").toLowerCase();
    }

    @Override
    public List<EventDTO> searchEventsByNameAndCity(String searchTerm, String cityKey) {
        updateEventStatus();
        List<Event> filteredEvents = new ArrayList<>();
        if("all-locations".equals(cityKey)) {
            filteredEvents = eventRepository.findByEventNameContainingIgnoreCase(searchTerm);
        }else {
           filteredEvents = eventRepository
                    .findByEventNameContainingIgnoreCaseAndEventLocationCityContainingIgnoreCase(searchTerm, cityKey);
        }
        return filteredEvents.stream()
                .map(event -> {
                    EventDTO dto = convertToDTO(event);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ResponseEntity<Response> saveEventToDB(EventDTO eventDTO) {
        String name = eventDTO.getEventHost();
        Optional<User> userOpt = userRepository.findByOrganizerName(name);
        if (!userOpt.isPresent()) {
            logger.error("User with organizerName {} not found", name);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(404, "Not Found", "User not found"));
        }
        User user = userOpt.get();

        if (eventDTO.getEventStart().isAfter(eventDTO.getEventEnd())) {
            logger.error("Event start time {} is after end time {}", eventDTO.getEventStart(), eventDTO.getEventEnd());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response(400, "Bad Request", "Event start time must be before end time"));
        }

        Event event = new Event();
        BeanUtils.copyProperties(eventDTO, event, "eventLocation", "eventImages", "mediaContent");
        event.setEventHost(name);
        event.setUser(user);

        EventLocation eventLocation = new EventLocation();
        EventLocationDTO locationDTO = eventDTO.getEventLocation();
        if (locationDTO != null) {
            BeanUtils.copyProperties(locationDTO, eventLocation);
            event.setEventLocation(eventLocation);
        }

        if (eventDTO.getEventImages() != null) {
            event.setEventImages(new ArrayList<>(eventDTO.getEventImages()));
        }
        if (eventDTO.getMediaContent() != null) {
            event.setMediaContent(new ArrayList<>(eventDTO.getMediaContent()));
        }

        Event tmp = eventRepository.save(event);
        logger.info("Event {} created successfully by user {}", event.getEventName(), name);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response(201, "Success", convertToDTO(tmp)));
    }

    @Override
    public List<EventDTO> getAllEventByHost(String email) {
        updateEventStatus();
        Optional<User> host = userRepository.findByEmail(email);
        if (!host.isPresent()) {
            logger.error("User with email {} not found", email);
            return new ArrayList<>();
        }
        User organizer = host.get();
        if (organizer.getOrganizer() == null) {
            logger.error("User is not organizer");
            return new ArrayList<>();
        }
        List<Event> events = eventRepository.findByEventHost(organizer.getOrganizer().getOrganizerName());
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    @Override
    public List<Event> findByUserUserId(int userId) {
        return eventRepository.findByUserUserId(userId);
    }

    @Override
    public List<EventDTO> topEventsByTicketsSold(){
        Pageable pageable =  PageRequest.of(0, 10);
        List<Event> topEvents = eventRepository.findTopEventsByTicketsSold("PAID", "SUCCESSFULLY", pageable);
        List<EventDTO> topEventDTO = new ArrayList<>();
        for (Event event : topEvents) {
            EventDTO eventDTO = convertToDTO(event);
            topEventDTO.add(eventDTO);
        }
        return topEventDTO;
    }
    @Override
    public List<EventDTO> top10FavoriteEvents(){
        Pageable pageable =  PageRequest.of(0, 10);
        List<Event> topEvents = eventRepository.findTop10FavoriteEvents(pageable);
        List<EventDTO> topEventDTO = new ArrayList<>();
        for (Event event : topEvents) {
            EventDTO eventDTO = convertToDTO(event);
            topEventDTO.add(eventDTO);
        }
        return topEventDTO;
    }
    @Override
    public List<String> top10Cities(){
        Pageable pageable =  PageRequest.of(0, 10);
        List<String> top10Cities = eventRepository.findTop10CitiesByEventCount(pageable);
        List<String> topCity = new ArrayList<>();
        for(String city : top10Cities){
            String cityName = cityMap.get(city);
            topCity.add(cityName);
        }
        return topCity;
    }

    @Override
    public List<EventDTO> getEventsByUSer(int userId){
        User organizer = userRepository.findById(userId).get();
        List<Event> eventDB = eventRepository.findByUser(organizer);
        List<EventDTO> eventDTOList = new ArrayList<>();
        for (Event event : eventDB) {
            EventDTO eventDTO = convertToDTO(event);
            eventDTOList.add(eventDTO);
        }
        return eventDTOList;
    }

    @Override
    public Response deleteEventAndRefunds(HttpServletRequest request, int eventId) throws Exception {
        Optional<Event> event = eventRepository.findById(eventId);
        if(event.isPresent()) {
            if("Complete".equals(event.get().getEventStatus())){
                Response response = new Response(404, "Failed","Can not delete the events that have been completed");
                return response;
            }
        }
        List<Transaction> transactions = transactionRepository.transactions(eventId);
        if( !transactions.isEmpty()) {
            for (Transaction transaction : transactions) {
                TransactionDTO transactionDTO = new TransactionDTO();
                BeanUtils.copyProperties(transaction, transactionDTO);
                System.out.println(transactionDTO);
                vnpayService.refund(request, transaction);
            }
        }

        eventRepository.deleteById(eventId);
        Response response = new Response(200, "Success", "Event deleted successfully");
        return response;
    }

    @Override
    public Set<EventDTO> findEventsByPreferredEventTypes(String email) {
        updateEventStatus();
        Optional<User> userOpt = userRepository.findByEmail(email);


        User user = userOpt.get();
        List<String> preferredEventTypes = user.getPreferredEventTypes() ;

        if (preferredEventTypes.isEmpty()) {
            return new HashSet<>();
        }

        List<Event> matchedEvents = new ArrayList<>();
        for (String eventType : preferredEventTypes) {
            List<Event> events = eventRepository.findByEventTypeContainingIgnoreCase(eventType);
            matchedEvents.addAll(events);
        }

        Set<EventDTO> eventDTOS  = new HashSet<>();
        for (Event event : matchedEvents) {
            EventDTO eventDTO = convertToDTO(event);
            if(!"Complete".equals(event.getEventStatus())){eventDTOS.add(eventDTO);}

        }
        return eventDTOS;
    }
    @Override
    public Set<EventDTO> findEventsByPreferredTags(String email) {
        updateEventStatus();
        Optional<User> userOpt = userRepository.findByEmail(email);

        User user = userOpt.get();
        List<String> preferredTags = user.getPreferredTags() ;

        if ( preferredTags.isEmpty()) {
            return new HashSet<>();
        }

        List<Event> matchedEvents =new ArrayList<>();
        for (String tag : preferredTags) {
            List<Event> events = eventRepository.findByTagsContainingIgnoreCase(tag);
            matchedEvents.addAll(events);
        }
        Set<EventDTO> eventDTOS  = new HashSet<>();
        for (Event event : matchedEvents) {
            EventDTO eventDTO = convertToDTO(event);
            if(!"Complete".equals(event.getEventStatus())){eventDTOS.add(eventDTO);}
        }
        return eventDTOS;
    }
    @Override
    public Set<EventDTO> findEventsByPreferredTypesAndTags(String email) {
        updateEventStatus();
        Optional<User> userOpt = userRepository.findByEmail(email);


        User user = userOpt.get();
        List<String> preferredEventTypes = user.getPreferredEventTypes() ;
        List<String> preferredTags = user.getPreferredTags() ;

        if (preferredEventTypes.isEmpty() && preferredTags.isEmpty()) {
            return new HashSet<>();
        }
        List<Event> matchedEvents = new ArrayList<>();

        // Tìm sự kiện theo eventType
        for (String eventType : preferredEventTypes) {
            List<Event> events = eventRepository.findByEventTypeContainingIgnoreCase(eventType);
            matchedEvents.addAll(events);
        }

        // Tìm sự kiện theo tags
        for (String tag : preferredTags) {
            List<Event> events = eventRepository.findByTagsContainingIgnoreCase(tag);
            matchedEvents.addAll(events);
        }

        Set<EventDTO> eventDTOS  = new HashSet<>();
        for (Event event : matchedEvents) {
            EventDTO eventDTO = convertToDTO(event);
            if(!"Complete".equals(event.getEventStatus())){eventDTOS.add(eventDTO);}
        }
        return eventDTOS;
    }
    public  String[] splitByPipe(String input) {

        if (input == null || input.trim().isEmpty()) {
            return new String[0];
        }

        return Arrays.stream(input.split("\\|"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
    }
    @Override
    public List<String> getAllTags(){
        Map<String, Integer> tagFrequency = new HashMap<>();
        List<Event> events = eventRepository.findAll();

        // Duyệt qua từng sự kiện để đếm tần suất tag
        for (Event event : events) {
            EventDTO eventDTO = convertToDTO(event);
            String tags = eventDTO.getTags();
            if (tags != null && !tags.trim().isEmpty()) {
                String[] tagArray = splitByPipe(tags);
                for (String tag : tagArray) {
                    tagFrequency.put(tag, tagFrequency.getOrDefault(tag, 0) + 1);
                }
            }
        }

        List<Map.Entry<String, Integer>> tagList = new ArrayList<>(tagFrequency.entrySet());

        // Sắp xếp danh sách theo tần suất giảm dần, nếu bằng thì theo thứ tự chữ cái
        for (int i = 0; i < tagList.size(); i++) {
            for (int j = i + 1; j < tagList.size(); j++) {
                Map.Entry<String, Integer> entry1 = tagList.get(i);
                Map.Entry<String, Integer> entry2 = tagList.get(j);
                // So sánh tần suất
                int freqCompare = entry2.getValue().compareTo(entry1.getValue());
                if (freqCompare == 0) {
                    freqCompare = entry1.getKey().compareTo(entry2.getKey());
                }
                if (freqCompare > 0) {
                    tagList.set(i, entry2);
                    tagList.set(j, entry1);
                }
            }
        }


        List<String> topTags = new ArrayList<>();
        for (int i = 0; i < Math.min(10, tagList.size()); i++) {
            topTags.add(tagList.get(i).getKey());
        }

        return topTags;
    }

}