package hcmute.fit.event_management.service;

import hcmute.fit.event_management.dto.ProviderServiceDTO;
import hcmute.fit.event_management.entity.ProviderService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface IProviderService {

    List<ProviderServiceDTO> getServiceProviders(int providerId);

    ProviderServiceDTO findServiceById(int serviceId);

    boolean addServiceProvider(ProviderServiceDTO serviceDTO);

    public boolean updateServiceProvider(ProviderServiceDTO serviceDTO);

    boolean deleteServiceProvider(int serviceId);

    List<ProviderServiceDTO> listServiceInEvent(int eventId, int providerId);


    List<ProviderServiceDTO> listServiceNotInEvent(int eventId, int providerId);
}
