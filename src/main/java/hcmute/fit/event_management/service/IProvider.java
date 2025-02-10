package hcmute.fit.event_management.service;

import hcmute.fit.event_management.dto.ProviderDTO;
import hcmute.fit.event_management.entity.Provider;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface IProvider {

    List<ProviderDTO> getAllProviders();

    boolean updateProvider(ProviderDTO providerDTO);

    boolean addProvider(ProviderDTO providerDTO);

    ProviderDTO findProviderById(Integer providerId);

    boolean deleteProvider(Integer providerId);

    boolean addProviderForEvent(int proId, int eventId);

    List<ProviderDTO> listProviderInEvent(int eventId);

    List<ProviderDTO> listProviderForAdd(int eventId);

    boolean delProviderEvent(int eventId, int providerId);

    ProviderDTO providerDetailInEvent(int eventId, int providerId);
}
