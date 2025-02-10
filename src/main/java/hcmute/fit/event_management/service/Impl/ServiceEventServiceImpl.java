package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.dto.ServiceEventDTO;
import hcmute.fit.event_management.entity.Event;
import hcmute.fit.event_management.entity.ProviderService;
import hcmute.fit.event_management.entity.ServiceEvent;
import hcmute.fit.event_management.entity.keys.ProviderServiceEventId;
import hcmute.fit.event_management.repository.EventRepository;
import hcmute.fit.event_management.repository.ProviderServiceRepository;
import hcmute.fit.event_management.repository.ServiceEventRepository;
import hcmute.fit.event_management.service.IServiceEventSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceEventServiceImpl implements IServiceEventSerivce {

    @Autowired
    private ServiceEventRepository serviceEventRepository;
    @Autowired
    private ProviderServiceRepository providerServiceRepository;
    @Autowired
    private EventRepository eventRepository;

    public ServiceEventServiceImpl(ServiceEventRepository serviceEventRepository) {
        this.serviceEventRepository = serviceEventRepository;
    }

    @Override
    public List<ServiceEventDTO> findServiceByEventId(int eventId) {
        List<ServiceEvent> list =  serviceEventRepository.findByEventId(eventId);
        List<ServiceEventDTO> dtoList = new ArrayList<ServiceEventDTO>();
        for (ServiceEvent serviceEvent : list) {
            ServiceEventDTO dto = new ServiceEventDTO();
            dto.setServiceId(serviceEvent.getService().getId());
            dto.setEventId(serviceEvent.getEvent().getEventID());
            dtoList.add(dto);
        }
        return dtoList;
    }
    @Override
    public boolean addServiceEvent(int eventId, int serviceId) {
        boolean result = false;
        try {
            Optional<ProviderService> service = providerServiceRepository.findById(serviceId);
            Optional<Event> event = eventRepository.findById(eventId);
            ServiceEvent serviceEvent = new ServiceEvent();
            ProviderServiceEventId providerServiceEventId = new ProviderServiceEventId( serviceId,eventId);
            serviceEvent.setId(providerServiceEventId);
            serviceEvent.setService(service.get());
            serviceEvent.setEvent(event.get());

            serviceEventRepository.save(serviceEvent);
            result = true;

        } catch (Exception e) {
            System.out.println("Save failed"+e.getMessage());
        }
        return result;
    }
    @Override
    public boolean delServiceEvent(int eventId, int serviceId) {
        boolean result = false;
        try {
            ServiceEvent serviceEvent = serviceEventRepository.serviceEvent(eventId, serviceId);
            if (serviceEvent != null) {
                serviceEventRepository.delete(serviceEvent);
                result = true;
            }
        } catch (Exception e) {
            System.out.println("Delete failed"+e.getMessage());
        }
        return result;
    }

}
