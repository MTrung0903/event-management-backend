package hcmute.fit.event_management.service;

import hcmute.fit.event_management.dto.OrganizerDTO;

public interface IOrganizerService {
    OrganizerDTO getOrganizerInforByEventHost(String eventHost);
}
