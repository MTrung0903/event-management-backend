package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.dto.ProviderDTO;
import hcmute.fit.event_management.dto.ProviderServiceDTO;
import hcmute.fit.event_management.entity.Event;
import hcmute.fit.event_management.entity.Provider;
import hcmute.fit.event_management.entity.ProviderEvent;
import hcmute.fit.event_management.entity.ProviderService;
import hcmute.fit.event_management.entity.keys.ProviderEventId;
import hcmute.fit.event_management.repository.EventRepository;
import hcmute.fit.event_management.repository.ProviderEventRepository;
import hcmute.fit.event_management.repository.ProviderRepository;
import hcmute.fit.event_management.service.IProvider;
import hcmute.fit.event_management.service.IProviderService;
import hcmute.fit.event_management.service.IServiceEventSerivce;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProviderImpl implements IProvider {
    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private ProviderEventRepository providerEventRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private IServiceEventSerivce serviceEventSerivce;

    @Autowired
    private IProviderService providerService;

    public ProviderImpl(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @Override
    public List<ProviderDTO> getAllProviders() {
        List<Provider> providers = providerRepository.findAll();
        List<ProviderDTO> providerDTOs = new ArrayList<>();

        for (Provider provider : providers) {
            ProviderDTO providerDTO = new ProviderDTO();
            BeanUtils.copyProperties(provider, providerDTO);

            List<ProviderServiceDTO> providerServiceDTOs = new ArrayList<>();
            for (ProviderService service : provider.getListProviderServices()) {
                ProviderServiceDTO serviceDTO = new ProviderServiceDTO();
                BeanUtils.copyProperties(service, serviceDTO);
                providerServiceDTOs.add(serviceDTO);
            }

            providerDTO.setListProviderServices(providerServiceDTOs);
            providerDTOs.add(providerDTO);
        }

        return providerDTOs;
    }

    @Override
    public boolean updateProvider(ProviderDTO providerDTO) {
        boolean isUpdated = false;
        if(providerRepository.findById(providerDTO.getId()).isPresent()) {
            Provider provider = providerRepository.findById(providerDTO.getId()).get();
            provider.setName(providerDTO.getName());
            provider.setContact(providerDTO.getContact());
            provider.setEmail(providerDTO.getEmail());
            provider.setPhone(providerDTO.getPhone());
            provider.setAddress(providerDTO.getAddress());
            provider.setWebsite(providerDTO.getWebsite());
            providerRepository.save(provider);
            isUpdated = true;
        }
        return isUpdated;
    }
    @Override
    public boolean addProvider(ProviderDTO providerDTO) {
        boolean isSuccess = false;
        try {
            Provider provider = new Provider();
            provider.setName(providerDTO.getName());
            provider.setContact(providerDTO.getContact());
            provider.setEmail(providerDTO.getEmail());
            provider.setPhone(providerDTO.getPhone());
            provider.setAddress(providerDTO.getAddress());
            provider.setWebsite(providerDTO.getWebsite());
            providerRepository.save(provider);
            isSuccess = true;
        } catch (Exception e) {
            System.out.println("Add provider failed" + e.getMessage());
        }

        return isSuccess;
    }

    @Override
    public ProviderDTO findProviderById(Integer providerId) {
        Optional<Provider> provider = providerRepository.findById(providerId);
        ProviderDTO providerDTO = new ProviderDTO();
        if(provider.isPresent()){
            BeanUtils.copyProperties(provider.get(), providerDTO);
            List<ProviderServiceDTO> providerServiceDTOs = new ArrayList<>();
            for(ProviderService service : provider.get().getListProviderServices()){
                ProviderServiceDTO serviceDTO = new ProviderServiceDTO();
                BeanUtils.copyProperties(service, serviceDTO);
                providerServiceDTOs.add(serviceDTO);
            }
            providerDTO.setListProviderServices(providerServiceDTOs);
        }
        return providerDTO;
    }
    @Override
    public boolean deleteProvider(Integer providerId) {
        boolean isDeleted = false;
        if(providerRepository.findById(providerId).isPresent()) {
            providerRepository.deleteById(providerId);
            isDeleted = true;
        }
        return isDeleted;
    }
    @Override
    public boolean addProviderForEvent(int proId, int eventId) {
        boolean isSuccess = false;

        try {
            Optional<Provider> providerOptional = providerRepository.findById(proId);
            Optional<Event> eventOptional = eventRepository.findById(eventId);

            if (providerOptional.isPresent() && eventOptional.isPresent()) {
                ProviderEvent providerEvent = new ProviderEvent();
                ProviderEventId providerEventId = new ProviderEventId(proId, eventId);

                providerEvent.setId(providerEventId);
                providerEvent.setProvider(providerOptional.get());
                providerEvent.setEvent(eventOptional.get());

                providerEventRepository.save(providerEvent);
                isSuccess = true;
            } else {
                System.out.println("Provider or Event not found for the given IDs.");
            }
        } catch (Exception e) {
            System.out.println("Add provider for event failed: " + e.getMessage());
        }

        return isSuccess;
    }

    @Override
    public List<ProviderDTO> listProviderInEvent(int eventId){
        List<ProviderDTO> providerDTOs = new ArrayList<>();
        List<ProviderEvent> list = providerEventRepository.findByEventId(eventId);
        for (ProviderEvent providerEvent : list) {
            ProviderDTO providerDTO = new ProviderDTO();
            Provider provider = providerRepository.findById(providerEvent.getProvider().getId()).get();
            BeanUtils.copyProperties(provider, providerDTO);
            List<ProviderServiceDTO> listSer = providerService.listServiceInEvent(eventId,provider.getId());
            providerDTO.setListProviderServices(listSer);
            providerDTOs.add(providerDTO);
        }
        return providerDTOs;
    }
    @Override
    public List<ProviderDTO> listProviderForAdd(int eventId) {
        List<ProviderDTO> providerDTOs = new ArrayList<>();

        // 1. Lấy danh sách các provider đã tham gia event
        List<ProviderEvent> providerEvents = providerEventRepository.findByEventId(eventId);
        Set<Integer> providerIdsInEvent = providerEvents.stream()
                .map(pe -> pe.getProvider().getId())
                .collect(Collectors.toSet());

        // 2. Lấy danh sách provider đã tham gia event và danh sách dịch vụ chưa được sử dụng
        for (Integer providerId : providerIdsInEvent) {
            Provider provider = providerRepository.findById(providerId).orElse(null);
            if (provider != null) {
                // Lấy danh sách dịch vụ chưa được sử dụng của provider
                List<ProviderServiceDTO> unusedServices = providerService.listServiceNotInEvent(eventId, providerId);

                // Kiểm tra danh sách dịch vụ chưa được sử dụng
                if (!unusedServices.isEmpty()) {
                    ProviderDTO providerDTO = new ProviderDTO();
                    BeanUtils.copyProperties(provider, providerDTO);
                    providerDTO.setListProviderServices(unusedServices);
                    providerDTOs.add(providerDTO);
                }
            }
        }

        // 3. Lấy danh sách provider chưa từng tham gia event
        List<Provider> providersNotInEvent = providerRepository.findAll().stream()
                .filter(provider -> !providerIdsInEvent.contains(provider.getId()))
                .collect(Collectors.toList());

        for (Provider provider : providersNotInEvent) {
            ProviderDTO providerDTO = new ProviderDTO();
            BeanUtils.copyProperties(provider, providerDTO);

            // Lấy tất cả dịch vụ của provider
            List<ProviderServiceDTO> allServices = provider.getListProviderServices().stream()
                    .map(service -> {
                        ProviderServiceDTO dto = new ProviderServiceDTO();
                        BeanUtils.copyProperties(service, dto);
                        dto.setProviderId(provider.getId());
                        return dto;
                    })
                    .collect(Collectors.toList());

            providerDTO.setListProviderServices(allServices);
            providerDTOs.add(providerDTO);
        }

        return providerDTOs;
    }

    @Override
    public boolean delProviderEvent(int eventId, int providerId) {
        boolean isSuccess = false;
        try{
            if(!providerEventRepository.findByProviderId(providerId).isEmpty() &&
                    !providerEventRepository.findByEventId(eventId).isEmpty()) {
                ProviderEvent providerEvent = providerEventRepository.providerEvent(providerId,eventId);
                providerEventRepository.delete(providerEvent);
                isSuccess = true;
            }
        }catch (Exception e){
            System.out.println("Del provider event failed: " + e.getMessage());
        }
        return isSuccess;
    }

    @Override
    public ProviderDTO providerDetailInEvent(int eventId, int providerId) {
        Optional<Provider> provider = providerRepository.findById(providerId);
        ProviderDTO providerDTO = new ProviderDTO();
        if(provider.isPresent()){
            BeanUtils.copyProperties(provider.get(), providerDTO);
            List<ProviderServiceDTO> providerServiceDTOs = providerService.listServiceInEvent(eventId, providerId);
            providerDTO.setListProviderServices(providerServiceDTOs);
        }
        return providerDTO;
    }

}
