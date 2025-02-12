package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.dto.SponsorDTO;
import hcmute.fit.event_management.dto.SponsorEventDTO;
import hcmute.fit.event_management.entity.Sponsor;
import hcmute.fit.event_management.entity.SponsorEvent;
import hcmute.fit.event_management.entity.keys.SponsorEventId;
import hcmute.fit.event_management.repository.EventRepository;
import hcmute.fit.event_management.repository.SponsorEventRepository;
import hcmute.fit.event_management.repository.SponsorRepository;
import hcmute.fit.event_management.repository.SponsorShipRepository;
import hcmute.fit.event_management.service.IFileService;
import hcmute.fit.event_management.service.ISponsorService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class SponsorServiceImpl implements ISponsorService {
    @Autowired
    private SponsorRepository sponsorRepository;

    @Autowired
    private SponsorShipRepository sponsorShipRepository;

    @Autowired
    private SponsorEventRepository sponsorEventRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private IFileService fileService;

    public SponsorServiceImpl(SponsorRepository sponsorRepository) {
        this.sponsorRepository = sponsorRepository;
    }
    @Override
    public List<SponsorDTO> getAllSponsors() {
        List<Sponsor> sponsors = sponsorRepository.findAll();
        List<SponsorDTO> sponsorDTOs = new ArrayList<>();
        for (Sponsor sponsor : sponsors) {
            SponsorDTO sponsorDTO = new SponsorDTO();
            sponsorDTO.setSponsorId(sponsor.getSponsorId());

            BeanUtils.copyProperties(sponsor, sponsorDTO);
            sponsorDTO.setSponsorLogo(sponsor.getSponsorLogo());
            sponsorDTO.setSponsorshipId(sponsor.getSponsorship().getSponsorShipID());
            sponsorDTO.setSponsorshipLevel(sponsor.getSponsorship().getLevel());
            sponsorDTOs.add(sponsorDTO);

        }
        return sponsorDTOs;
    }
    @Override
    public SponsorDTO getSponsorById(int id) {
        Optional<Sponsor> sponsor = sponsorRepository.findById(id);
        SponsorDTO sponsorDTO = new SponsorDTO();
        if(sponsor.isPresent()){
            BeanUtils.copyProperties(sponsor.get(), sponsorDTO);
            sponsorDTO.setSponsorLogo(sponsor.get().getSponsorLogo());
            sponsorDTO.setSponsorshipId(sponsor.get().getSponsorship().getSponsorShipID());
            sponsorDTO.setSponsorshipLevel(sponsor.get().getSponsorship().getLevel());
            List<SponsorEventDTO> dtoList = new ArrayList<>();
            for(SponsorEvent se : sponsor.get().getListSponsorEvents()){
                SponsorEventDTO seDto = new SponsorEventDTO();
                seDto.setEventId(se.getEvent().getEventID());
                seDto.setEventName(se.getEvent().getEventName());
                dtoList.add(seDto);
            }
            sponsorDTO.setListSponsorEvents(dtoList);
        }
        return sponsorDTO;
    }
    @Override
    public boolean addSponsor(MultipartFile sponsorLogo, SponsorDTO sponsorDTO) {
        boolean isSuccess = false;
        boolean isUoloadImg = fileService.saveFiles(sponsorLogo);
        if(isUoloadImg){
            try{
                if(sponsorShipRepository.findById(sponsorDTO.getSponsorshipId()).isPresent()){
                    Sponsor sponsor = new Sponsor();
                    sponsor.setSponsorName(sponsorDTO.getSponsorName());
                    sponsor.setSponsorLogo(sponsorLogo.getOriginalFilename());
                    sponsor.setSponsorContact(sponsorDTO.getSponsorContact());
                    sponsor.setSponsorEmail(sponsorDTO.getSponsorEmail());
                    sponsor.setSponsorPhone(sponsorDTO.getSponsorPhone());
                    sponsor.setSponsorWebsite(sponsorDTO.getSponsorWebsite());
                    sponsor.setSponsorAddress(sponsorDTO.getSponsorAddress());
                    sponsor.setSponsorship(sponsorShipRepository.findById(sponsorDTO.getSponsorshipId()).get());
                    sponsorRepository.save(sponsor);
                    isSuccess = true;
                }
            }
            catch(Exception e){
                System.out.println("add sponsor failed"+ e.getMessage());
            }
        }

        return isSuccess;
    }
    @Override
    public boolean updateSponsor(MultipartFile sponsorLogo, SponsorDTO sponsorDTO) {
        boolean isSuccess = false;
        boolean isUoloadImg = fileService.saveFiles(sponsorLogo);
        if(isUoloadImg){
            try{
                if(sponsorRepository.findById(sponsorDTO.getSponsorId()).isPresent()
                        &&sponsorShipRepository.findById(sponsorDTO.getSponsorshipId()).isPresent()){
                    Sponsor sponsor = sponsorRepository.findById(sponsorDTO.getSponsorId()).get();
                    sponsor.setSponsorLogo(sponsorLogo.getOriginalFilename());
                    sponsor.setSponsorName(sponsorDTO.getSponsorName());
                    sponsor.setSponsorLogo(sponsorLogo.getOriginalFilename());
                    sponsor.setSponsorContact(sponsorDTO.getSponsorContact());
                    sponsor.setSponsorEmail(sponsorDTO.getSponsorEmail());
                    sponsor.setSponsorPhone(sponsorDTO.getSponsorPhone());
                    sponsor.setSponsorWebsite(sponsorDTO.getSponsorWebsite());
                    sponsor.setSponsorAddress(sponsorDTO.getSponsorAddress());
                    sponsor.setSponsorship(sponsorShipRepository.findById(sponsorDTO.getSponsorshipId()).get());
                    sponsorRepository.save(sponsor);
                    isSuccess = true;
                }
            }
            catch(Exception e){
                System.out.println("update sponsor failed"+ e.getMessage());
            }
        }

        return isSuccess;
    }
    @Override
    public boolean updateSponsor(SponsorDTO sponsorDTO) {
        boolean isSuccess = false;

            try{
                if(sponsorRepository.findById(sponsorDTO.getSponsorId()).isPresent()
                        &&sponsorShipRepository.findById(sponsorDTO.getSponsorshipId()).isPresent()){
                    Sponsor sponsor = sponsorRepository.findById(sponsorDTO.getSponsorId()).get();

                    sponsor.setSponsorLogo(sponsorDTO.getSponsorLogo());
                    sponsor.setSponsorName(sponsorDTO.getSponsorName());

                    sponsor.setSponsorContact(sponsorDTO.getSponsorContact());
                    sponsor.setSponsorEmail(sponsorDTO.getSponsorEmail());
                    sponsor.setSponsorPhone(sponsorDTO.getSponsorPhone());
                    sponsor.setSponsorWebsite(sponsorDTO.getSponsorWebsite());
                    sponsor.setSponsorAddress(sponsorDTO.getSponsorAddress());
                    sponsor.setSponsorship(sponsorShipRepository.findById(sponsorDTO.getSponsorshipId()).get());
                    sponsorRepository.save(sponsor);
                    isSuccess = true;
                }
            }
            catch(Exception e){
                System.out.println("update sponsor failed"+ e.getMessage());
            }


        return isSuccess;
    }
    @Override
    public boolean deleteSponsor(int id) {
        boolean isSuccess = false;
        if(sponsorRepository.findById(id).isPresent()){
            sponsorRepository.deleteById(id);
            isSuccess = true;
        }
        return isSuccess;
    }

    @Override
    public boolean addSponsorForEvent(int sponsorId, int eventId) {
        boolean isSuccess = false;
        try{

            if(sponsorRepository.findById(sponsorId).isPresent() ){
                SponsorEvent sponsorEvent = new SponsorEvent();
                SponsorEventId sponsorEventId = new SponsorEventId(sponsorId, eventId);
                sponsorEvent.setId(sponsorEventId);
                sponsorEvent.setEvent(eventRepository.findById(eventId).get());
                sponsorEvent.setSponsor(sponsorRepository.findById(sponsorId).get());
                sponsorEventRepository.save(sponsorEvent);
                isSuccess = true;
            }
        } catch (Exception e) {
            System.out.println("add sponsor failed"+ e.getMessage());
        }
        return isSuccess;
    }
    @Override
    public boolean addNewSponsorForEvent(int eventId, MultipartFile logo, SponsorDTO sponsorDTO) {
        boolean isSuccess = false;
        try{
            addSponsor(logo,sponsorDTO);
            addSponsorForEvent(eventId,sponsorDTO.getSponsorId());
            isSuccess = true;

        } catch (Exception e) {
            System.out.println("add sponsor failed"+ e.getMessage());
        }
        return isSuccess;
    }
    @Override
    public List<SponsorDTO> getAllSponsorByEventId(int eventId) {
        try{
            List<SponsorEvent> list = sponsorEventRepository.findByEventId(eventId);
            List<SponsorDTO> listDTO = new ArrayList<>();
            for(SponsorEvent sponsorEvent : list){
                Optional<Sponsor> sponsor = sponsorRepository.findById(sponsorEvent.getSponsor().getSponsorId());
                if(sponsor.isPresent()){
                   SponsorDTO sponsorDTO = new SponsorDTO();
                   BeanUtils.copyProperties(sponsor.get(), sponsorDTO);
                   sponsorDTO.setSponsorshipLevel(sponsor.get().getSponsorship().getLevel());
                   sponsorDTO.setSponsorLogo(sponsor.get().getSponsorLogo());
                   listDTO.add(sponsorDTO);
                }
            }
            return listDTO;
        } catch (Exception e) {
            System.out.println("getAllSponsorByEventId failed"+ e.getMessage());
        }
        return  null;
    }
    @Override
    public List<SponsorDTO> getSponsorForAddNew(int eventId) {
        Set<Integer> oldSponsor = new HashSet<>();
        List<SponsorDTO> nSponsorList = new ArrayList<>();
        try{
            List<SponsorEvent> list = sponsorEventRepository.findByEventId(eventId);
            for (SponsorEvent sponsorEvent : list) {
                oldSponsor.add(sponsorEvent.getSponsor().getSponsorId());
            }
            List<Sponsor> sponsorList = sponsorRepository.findAll();

            for(Sponsor sponsor : sponsorList){
                if(!oldSponsor.contains(sponsor.getSponsorId())){
                    SponsorDTO sponsorDTO = new SponsorDTO();
                    BeanUtils.copyProperties(sponsor, sponsorDTO);
                    sponsorDTO.setSponsorshipLevel(sponsor.getSponsorship().getLevel());
                    sponsorDTO.setSponsorLogo(sponsor.getSponsorLogo());
                    nSponsorList.add(sponsorDTO);
                }
            }
            return nSponsorList;

        } catch (Exception e) {
            System.out.println("add sponsor failed"+ e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public boolean deleteSponsorEvent(int eventId, int sponsorId){
        boolean isSuccess = false;
        try{
            SponsorEvent sponsorEvent = sponsorEventRepository.findSponsorEvent(eventId,sponsorId);
            if(sponsorEvent != null){
                sponsorEventRepository.delete(sponsorEvent);
                isSuccess = true;
            }
        }catch(Exception e){
            System.out.println("delete sponsor failed"+ e.getMessage());
        }
        return isSuccess;
    }

}
