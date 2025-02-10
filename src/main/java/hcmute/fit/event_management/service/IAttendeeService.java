package hcmute.fit.event_management.service;

import hcmute.fit.event_management.dto.AttendeeDTO;
import hcmute.fit.event_management.entity.Attendee;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface IAttendeeService {

    List<AttendeeDTO> getListAttendeeByEventId(Integer eventId);

    AttendeeDTO findAttendee(Integer id);

    public boolean updateStatus(Integer attendeeId,String status);

    boolean addAttendee(AttendeeDTO attendeeDTO);
}
