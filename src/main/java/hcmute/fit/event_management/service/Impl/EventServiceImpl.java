package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.dto.EventDTO;
import hcmute.fit.event_management.entity.Event;
import hcmute.fit.event_management.entity.Manager;
import hcmute.fit.event_management.repository.EventRepository;
import hcmute.fit.event_management.repository.ManagerRepository;
import hcmute.fit.event_management.repository.McRepository;
import hcmute.fit.event_management.service.IEventService;
import hcmute.fit.event_management.service.IFileService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements IEventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private McRepository mcRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private IFileService fileService;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<EventDTO> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        List<EventDTO> eventDTOs = new ArrayList<>();
        for (Event event : events) {
            EventDTO eventDTO = new EventDTO();
            BeanUtils.copyProperties(event, eventDTO);
            eventDTO.setEventId(event.getEventID());
            eventDTO.setManId(event.getManager().getManID());
//            eventDTO.setMcId(event.getMc().getMcID());
            eventDTOs.add(eventDTO);
        }
        return eventDTOs;
    }

    @Override
    public EventDTO getEventById(int id) {
        Optional<Event> event = eventRepository.findById(id);
        if (event.isPresent()) {
            EventDTO eventDTO = new EventDTO();
            BeanUtils.copyProperties(event.get(), eventDTO);
            eventDTO.setEventId(id);
            eventDTO.setManId(event.get().getManager().getManID());
            eventDTO.setEventStart(event.get().getEventStart().toString());
            eventDTO.setEventEnd(event.get().getEventEnd().toString());
//            eventDTO.setMcId(event.get().getMc().getMcID());
            return eventDTO;
        }
        return null;
    }

    @Override
    public boolean addEvent(MultipartFile image, EventDTO eventDTO) {
        boolean isSuccess = false;
        boolean isUoloadImg = fileService.saveFiles(image);
        if (isUoloadImg) {
            try {
                System.out.println("Manager ID: " + eventDTO.getManId());
                if (managerRepository.existsById(eventDTO.getManId())) {
                    Event event = new Event();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    if (eventDTO.getEventStart() != null && eventDTO.getEventEnd() != null) {
                        try {
                            event.setEventStart(formatter.parse(eventDTO.getEventStart().trim()));
                            event.setEventEnd(formatter.parse(eventDTO.getEventEnd().trim()));
                        } catch (ParseException e) {
                            System.out.println("Invalid date format: " + eventDTO.getEventStart());
                            System.out.println("Invalid date format: " + eventDTO.getEventEnd());
                            return isSuccess;
                        }
                    }
                    BeanUtils.copyProperties(eventDTO, event);
                    event.setEventImg(image.getOriginalFilename());
                    event.setManager(managerRepository.findById(eventDTO.getManId()).orElse(null));
                    eventRepository.save(event);
                    isSuccess = true;
                }
            } catch (Exception e) {
                System.out.println("add event failed" + e.getMessage());
            }
        }

        return isSuccess;
    }

    @Override
    public boolean deleteEvent(int eventId) {
        boolean isSuccess = false;
        try {
            eventRepository.deleteById(eventId);
            isSuccess = true;
        } catch (Exception e) {
            System.out.println("delete event failed" + e.getMessage());
        }
        return isSuccess;
    }

    @Override
    public boolean updateEvent(MultipartFile image, EventDTO eventDTO) {
        boolean isSuccess = false;
        boolean isUoloadImg = fileService.saveFiles(image);
        if (isUoloadImg) {
            try {
                Optional<Manager> managerOpt = managerRepository.findById(eventDTO.getManId());
                Optional<Event> eventOpt = eventRepository.findById(eventDTO.getEventId());

                if (managerOpt.isPresent() && eventOpt.isPresent()) {
                    Event event = eventOpt.get();
                    BeanUtils.copyProperties(eventDTO, event);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    if (eventDTO.getEventStart() != null && eventDTO.getEventEnd() != null) {
                        try {
                            event.setEventStart(formatter.parse(eventDTO.getEventStart().trim()));
                            event.setEventEnd(formatter.parse(eventDTO.getEventEnd().trim()));
                        } catch (ParseException e) {
                            System.out.println("Invalid date format: " + eventDTO.getEventStart());
                            System.out.println("Invalid date format: " + eventDTO.getEventEnd());
                            return isSuccess;
                        }
                    }
                    event.setEventImg(image.getOriginalFilename());
                    event.setManager(managerOpt.get());
                    if (eventDTO.getMcId() != null)
                        event.setMc(mcRepository.findById(eventDTO.getMcId()).orElse(null));
                    eventRepository.save(event);
                    isSuccess = true;
                } else {
                    throw new RuntimeException("Manager or Event not found with ID: ");
                }
            } catch (Exception e) {
                System.out.println("Add event failed: " + e.getMessage());
            }
        } else {
            System.out.println("Update file image failed");
        }
        return isSuccess;
    }

    @Override
    public boolean addMc(int eventId, int mcId) {
        boolean isSuccess = false;
        try {
            if (mcRepository.findById(mcId).isPresent()) {
                Event event = eventRepository.findById(eventId).get();
                event.setMc(mcRepository.findById(mcId).get());
                eventRepository.save(event);
                isSuccess = true;
            } else {
                throw new RuntimeException("Mc not found with ID: " + mcId);
            }
        } catch (Exception e) {
            System.out.println("add mc failed" + e.getMessage());
        }
        return isSuccess;
    }

    @Override
    public List<EventDTO> getAllEventByEmp(int empId) {
        List<Event> events = eventRepository.findByEmpId(empId);
        List<EventDTO> eventDTOs = new ArrayList<>();
        for (Event event : events) {
            EventDTO eventDTO = new EventDTO();
            BeanUtils.copyProperties(event, eventDTO);
            eventDTO.setEventId(event.getEventID());
            eventDTO.setManId(event.getManager().getManID());
            eventDTO.setMcId(event.getMc().getMcID());
            eventDTOs.add(eventDTO);
        }
        return eventDTOs;
    }

    @Override
    public String getListAttendeeByEventId(int eventId){
        Event event = eventRepository.findById(eventId).orElse(new Event());
        return event.getEventAttendee();
    }
    @Override
    public String addAttendee(int eventId, MultipartFile attendee){
        try {
            Event event = eventRepository.findById(eventId).orElse(new Event());
            boolean isUploadSeccess = fileService.saveFiles(attendee);
            if (isUploadSeccess) {
                event.setEventAttendee(attendee.getOriginalFilename());
            }
            eventRepository.save(event);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return attendee.getOriginalFilename();
    }
}
