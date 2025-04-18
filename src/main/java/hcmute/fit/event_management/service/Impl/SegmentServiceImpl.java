package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.dto.SessionDTO;
import hcmute.fit.event_management.dto.SpeakerDTO;
import hcmute.fit.event_management.entity.Session;
import hcmute.fit.event_management.entity.Speaker;
import hcmute.fit.event_management.repository.SessionRepository;
import hcmute.fit.event_management.service.IEventService;
import hcmute.fit.event_management.service.ISessionService;
import hcmute.fit.event_management.service.ISpeakerService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SessionServiceImpl implements ISessionService {
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private IEventService eventService;
    @Autowired
    private ISpeakerService speakerService;
    @Autowired
    private CloudinaryService cloudinary;

    public SessionServiceImpl(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void addSession(int eventId, SessionDTO session) {
        SpeakerDTO speakerDTO = new SpeakerDTO();
        speakerDTO.setSpeakerName(session.getSpeaker().getSpeakerName());
        speakerDTO.setSpeakerDesc(session.getSpeaker().getSpeakerDesc());
        speakerDTO.setSpeakerImage(session.getSpeaker().getSpeakerImage());
        Speaker speaker = speakerService.addSpeaker(speakerDTO);

        Session newSession = new Session();
        BeanUtils.copyProperties(session, newSession);
        newSession.setEvent(eventService.findById(eventId).get());
        newSession.setSpeaker(speaker);
        sessionRepository.save(newSession);
    }
    @Override
    public List<SessionDTO> getAllSessions(int eventId) {
        List<Session> list = sessionRepository.findByEventId(eventId);
        List<SessionDTO> dtos = new ArrayList<>();
        for (Session session : list) {
            SessionDTO dto = new SessionDTO();
            Speaker speaker = session.getSpeaker();
            SpeakerDTO speakerDTO = new SpeakerDTO();
            BeanUtils.copyProperties(speaker, speakerDTO);
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
}
