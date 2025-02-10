package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.dto.ProviderServiceDTO;
import hcmute.fit.event_management.entity.ProviderService;
import hcmute.fit.event_management.repository.ProviderRepository;
import hcmute.fit.event_management.repository.ProviderServiceRepository;
import hcmute.fit.event_management.service.IProviderService;
import hcmute.fit.event_management.service.IServiceEventSerivce;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProviderServiceImpl implements IProviderService {
    @Autowired
    private ProviderServiceRepository providerServiceRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private IServiceEventSerivce serviceEventSerivce;
    public ProviderServiceImpl(ProviderServiceRepository providerServiceRepository) {
        this.providerServiceRepository = providerServiceRepository;
    }

    @Override
    public List<ProviderServiceDTO> getServiceProviders(int providerId) {
        List<ProviderService> providerServiceList = providerServiceRepository.findByProviderId(providerId);
        List<ProviderServiceDTO> providerServiceDTOList = new ArrayList<>();
        for (ProviderService providerService : providerServiceList) {
            ProviderServiceDTO serviceDTO = new ProviderServiceDTO();
            BeanUtils.copyProperties(providerService, serviceDTO);
            serviceDTO.setProviderId(providerService.getProvider().getId());
            providerServiceDTOList.add(serviceDTO);
        }
        return providerServiceDTOList;
    }
    @Override
    public ProviderServiceDTO findServiceById(int serviceId){
        Optional<ProviderService> providerService = providerServiceRepository.findById(serviceId);
        ProviderServiceDTO providerServiceDTO = new ProviderServiceDTO();
        if(providerService.isPresent()){
            BeanUtils.copyProperties(providerService.get(), providerServiceDTO);
            providerServiceDTO.setProviderId(providerService.get().getProvider().getId());
            return providerServiceDTO;
        }
        return null;
    }
    @Override
    public boolean addServiceProvider(ProviderServiceDTO serviceDTO) {
        boolean isSuccess = false;
        try{
            if(providerRepository.findById(serviceDTO.getProviderId()).isPresent()){
                ProviderService providerService = new ProviderService();
                providerService.setServiceName(serviceDTO.getServiceName());
                providerService.setServiceType(serviceDTO.getServiceType());
                providerService.setServiceDesc(serviceDTO.getServiceDesc());
                providerService.setPrice(serviceDTO.getPrice());
                providerService.setDuration(serviceDTO.getDuration());
                providerService.setProvider(providerRepository.findById(serviceDTO.getProviderId()).get());
                providerServiceRepository.save(providerService);
                isSuccess = true;
            }else{
                throw new Exception("Not found provider");
            }
        } catch (Exception e) {
            System.out.println("Add service failed "+ e.getMessage());
        }
        return isSuccess;
    }
    @Override
    public boolean updateServiceProvider(ProviderServiceDTO serviceDTO) {
        boolean isSuccess = false;
        try{
            if(providerServiceRepository.findById(serviceDTO.getId()).isPresent()){
                ProviderService providerService = providerServiceRepository.findById(serviceDTO.getId()).get();
                providerService.setServiceName(serviceDTO.getServiceName());
                providerService.setServiceType(serviceDTO.getServiceType());
                providerService.setServiceDesc(serviceDTO.getServiceDesc());
                providerService.setPrice(serviceDTO.getPrice());
                providerService.setDuration(serviceDTO.getDuration());
                providerService.setProvider(providerRepository.findById(serviceDTO.getProviderId()).get());
                providerServiceRepository.save(providerService);
                isSuccess = true;
            }else{
                throw new Exception("Not found service");
            }
        } catch (Exception e) {
            System.out.println("Update service failed "+ e.getMessage());
        }
        return isSuccess;
    }

    @Override
    public boolean deleteServiceProvider(int serviceId) {
        boolean isSuccess = false;
        try
        {
           if(providerServiceRepository.findById(serviceId).isPresent()){
               providerServiceRepository.deleteById(serviceId);
               isSuccess = true;
           }

        } catch (Exception e) {
            System.out.println("Delete service failed "+ e.getMessage());
        }
        return isSuccess;
    }
    @Override
    public List<ProviderServiceDTO> listServiceInEvent(int eventId, int providerId){
        List<ProviderService> list = providerServiceRepository.getServiceInEvent(providerId,eventId);
        List<ProviderServiceDTO> providerServiceDTOList = new ArrayList<>();
        for (ProviderService providerService : list) {
            ProviderServiceDTO providerServiceDTO = new ProviderServiceDTO();
            BeanUtils.copyProperties(providerService, providerServiceDTO);
            providerServiceDTO.setProviderId(providerService.getProvider().getId());
            providerServiceDTOList.add(providerServiceDTO);
        }
        return providerServiceDTOList;
    }
    @Override
    public List<ProviderServiceDTO> listServiceNotInEvent(int eventId, int providerId){
        List<ProviderService> list = providerServiceRepository.getServicesNotInEvent(providerId,eventId);
        List<ProviderServiceDTO> providerServiceDTOList = new ArrayList<>();
        for (ProviderService providerService : list) {
            ProviderServiceDTO providerServiceDTO = new ProviderServiceDTO();
            BeanUtils.copyProperties(providerService, providerServiceDTO);
            providerServiceDTO.setProviderId(providerService.getProvider().getId());
            providerServiceDTOList.add(providerServiceDTO);
        }
        return providerServiceDTOList;
    }


}
