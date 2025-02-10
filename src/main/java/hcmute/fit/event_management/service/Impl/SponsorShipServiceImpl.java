package hcmute.fit.event_management.service.Impl;


import hcmute.fit.event_management.dto.SponsorDTO;
import hcmute.fit.event_management.dto.SponsorShipDTO;
import hcmute.fit.event_management.entity.Sponsor;
import hcmute.fit.event_management.entity.SponsorShip;
import hcmute.fit.event_management.repository.SponsorShipRepository;
import hcmute.fit.event_management.service.ISponsorShipService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SponsorShipServiceImpl implements ISponsorShipService {
    @Autowired
    private SponsorShipRepository sponsorShipRepository;

    public SponsorShipServiceImpl(SponsorShipRepository sponsorShipRepository) {
        this.sponsorShipRepository = sponsorShipRepository;
    }

    @Override
    public List<SponsorShipDTO> getAllSponsorShips() {
        List<SponsorShip> sponsorShips = sponsorShipRepository.findAll();
        List<SponsorShipDTO> sponsorShipDTOs = new ArrayList<>();
        for (SponsorShip sponsorShip : sponsorShips) {
            SponsorShipDTO sponsorShipDTO = new SponsorShipDTO();
            sponsorShipDTO.setSponsorShipID(sponsorShip.getSponsorShipID());
            sponsorShipDTO.setLevel(sponsorShip.getLevel());
            sponsorShipDTO.setBenefit(sponsorShip.getBenefit());
            List<SponsorDTO> list = new ArrayList<>();
            for (Sponsor s : sponsorShip.getListSponsors()){
                SponsorDTO sponsorDTO = new SponsorDTO();
                BeanUtils.copyProperties(s, sponsorDTO);
                list.add(sponsorDTO);
            }
            sponsorShipDTO.setListSponsors(list);
            sponsorShipDTOs.add(sponsorShipDTO);
        }
        return sponsorShipDTOs;
    }
    @Override
    public SponsorShipDTO getSponsorShipByID(int sponsorShipID) {
        if(sponsorShipRepository.findById(sponsorShipID).isPresent()){
            SponsorShipDTO sponsorShipDTO = new SponsorShipDTO();
            SponsorShip sponsorShip = sponsorShipRepository.findById(sponsorShipID).get();
            sponsorShipDTO.setSponsorShipID(sponsorShip.getSponsorShipID());
            sponsorShipDTO.setLevel(sponsorShip.getLevel());
            sponsorShipDTO.setBenefit(sponsorShip.getBenefit());
            List<SponsorDTO> list = new ArrayList<>();
            for (Sponsor s : sponsorShip.getListSponsors()){
                SponsorDTO sponsorDTO = new SponsorDTO();
                BeanUtils.copyProperties(s, sponsorDTO);
                list.add(sponsorDTO);
            }
            sponsorShipDTO.setListSponsors(list);
            return sponsorShipDTO;
        }
        return null;
    }
    @Override
    public boolean addSponsorShip(SponsorShipDTO sponsorShipDTO) {
        boolean isSuccess = false;
        try {
            SponsorShip sponsorShip = new SponsorShip();
            sponsorShip.setLevel(sponsorShipDTO.getLevel());
            sponsorShip.setBenefit(sponsorShipDTO.getBenefit());
            sponsorShipRepository.save(sponsorShip);
            isSuccess = true;
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return isSuccess;
    }
    @Override
    public boolean updateSponsorShip(SponsorShipDTO sponsorShipDTO) {
        boolean isSuccess = false;
        try {
            if(sponsorShipRepository.findById(sponsorShipDTO.getSponsorShipID()).isPresent()){
                SponsorShip sponsorShip = sponsorShipRepository.findById(sponsorShipDTO.getSponsorShipID()).get();
                sponsorShip.setLevel(sponsorShipDTO.getLevel());
                sponsorShip.setBenefit(sponsorShipDTO.getBenefit());
                sponsorShipRepository.save(sponsorShip);
                isSuccess = true;
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return isSuccess;
    }
    @Override
    public boolean deleteSponsorShip(int id) {
        boolean isSuccess = false;
        if(sponsorShipRepository.findById(id).isPresent()){
            sponsorShipRepository.delete(sponsorShipRepository.findById(id).get());
            isSuccess = true;
        }
        return isSuccess;
    }

}
