package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.dto.AttendeeDTO;
import hcmute.fit.event_management.entity.Attendee;
import hcmute.fit.event_management.repository.AttendeeRepository;
import hcmute.fit.event_management.repository.InviteRepository;
import hcmute.fit.event_management.service.IAttendeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AttendeeServiceImpl implements IAttendeeService {
    @Autowired
    private AttendeeRepository attendeeRepository;

    @Autowired
    private InviteRepository inviteRepository;

    public AttendeeServiceImpl(AttendeeRepository attendeeRepository) {
        this.attendeeRepository = attendeeRepository;
    }

    @Override
    public List<AttendeeDTO> getListAttendeeByEventId(Integer eventId) {
        List<AttendeeDTO> dtoList =new ArrayList<>();
        List<Attendee> attendees = attendeeRepository.findAttendeesByEventId(eventId);
        for(Attendee attendee : attendees){
            AttendeeDTO dto = new AttendeeDTO();
            BeanUtils.copyProperties(attendee,dto);
            dto.setInviteId(attendee.getInvite().getId());
            dtoList.add(dto);
        }
        return dtoList;
    }


    @Override
    public AttendeeDTO findAttendee(Integer id) {
        AttendeeDTO dto = new AttendeeDTO();
        Attendee attendee = attendeeRepository.findById(id).get();
        if(attendee != null){
            BeanUtils.copyProperties(attendee,dto);
            dto.setInviteId(attendee.getInvite().getId());
        }
        return dto;
    }

    @Override
    public boolean updateStatus(Integer attendeeId,String status) {
        boolean isSuccess = false;
        Optional<Attendee> attendee = attendeeRepository.findById(attendeeId);
        if(attendee.isPresent()){
            Attendee updateAttendee = attendee.get();
            updateAttendee.setAttendeeStatus(status);
            attendeeRepository.save(updateAttendee);
            isSuccess = true;
        }
        return isSuccess;
    }
    @Override
    public boolean addAttendee(AttendeeDTO attendeeDTO) {
        boolean isSuccess = false;
        try{
            Attendee attendee = new Attendee();
            BeanUtils.copyProperties(attendeeDTO,attendee);
            attendee.setInvite(inviteRepository.findById(attendeeDTO.getInviteId()).get());
            attendeeRepository.save(attendee);
            isSuccess = true;
        } catch (Exception e) {
            System.out.println("add attendee failed"+e.getMessage());
        }
        return isSuccess;
    }


}
