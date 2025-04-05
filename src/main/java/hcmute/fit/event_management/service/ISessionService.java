package hcmute.fit.event_management.service;

import hcmute.fit.event_management.dto.SessionDTO;

import java.util.List;

public interface ISessionService {
    void addSession(int eventId, SessionDTO segment);
    List<SessionDTO> getAllSessions(int eventId);
}
