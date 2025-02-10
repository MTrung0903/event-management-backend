package hcmute.fit.event_management.service;

import hcmute.fit.event_management.dto.ServiceEventDTO;
import hcmute.fit.event_management.entity.ServiceEvent;

import java.util.List;

public interface IServiceEventSerivce {
    List<ServiceEventDTO> findServiceByEventId(int eventId);

    boolean addServiceEvent(int eventId, int serviceId);

    boolean delServiceEvent(int eventId, int serviceId);
}
