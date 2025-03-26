package hcmute.fit.event_management.service.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import hcmute.fit.event_management.dto.EventDTO;
import hcmute.fit.event_management.entity.Event;
import hcmute.fit.event_management.repository.EventRepository;
import hcmute.fit.event_management.repository.TicketRepository;
import hcmute.fit.event_management.service.IEventService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import payload.Response;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EventServiceImpl implements IEventService {
    @Autowired
    private EventRepository eventRepository;

    public Event saveEvent(Event event) throws IOException {

        return eventRepository.save(event);
    }

    public Event getEvent(Integer eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }
}
