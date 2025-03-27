package hcmute.fit.event_management.service.Impl;

import com.cloudinary.Cloudinary;
import hcmute.fit.event_management.dto.EventDTO;
import hcmute.fit.event_management.entity.Event;
import hcmute.fit.event_management.repository.EventRepository;
import hcmute.fit.event_management.service.IEventService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements IEventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private Cloudinary cloudinary;
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
        dto.setTags(event.getTags().split("\\|")); // Tách tags bằng ký tự "|"
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
}
