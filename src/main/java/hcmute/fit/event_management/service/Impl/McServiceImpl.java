package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.dto.EventDTO;
import hcmute.fit.event_management.dto.McDTO;
import hcmute.fit.event_management.entity.Event;
import hcmute.fit.event_management.entity.Mc;
import hcmute.fit.event_management.repository.EventRepository;
import hcmute.fit.event_management.repository.McRepository;
import hcmute.fit.event_management.service.IFileService;
import hcmute.fit.event_management.service.IMcService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class McServiceImpl implements IMcService {
    @Autowired
    private McRepository mcRepository;



    @Autowired
    private IFileService fileService;

    public McServiceImpl(McRepository mcRepository) {
        this.mcRepository = mcRepository;
    }

    @Override
    public List<McDTO> getListMc(){
        List<McDTO> listMc = new ArrayList<>();
        List<Mc> mcList = mcRepository.findAll();
        for (Mc mc : mcList) {
            McDTO mcDTO = new McDTO();
            BeanUtils.copyProperties(mc,mcDTO);
            mcDTO.setImage(mc.getImage());
            List<EventDTO> listeventdto = new ArrayList<>();
            for(Event e : mc.getListEvents() ){
                EventDTO eventDTO = new EventDTO();
                BeanUtils.copyProperties(e,eventDTO);
                listeventdto.add(eventDTO);
            }
            mcDTO.setListEvents(listeventdto);
            listMc.add(mcDTO);
        }
        return listMc;
    }
    @Override
    public boolean addMc(MultipartFile image,McDTO mcDto){
        boolean isSucess = false;
        boolean isUoloadImg = fileService.saveFiles(image);
        if(isUoloadImg){
            try{
                Mc mc = new Mc();


                BeanUtils.copyProperties(mcDto,mc);
                mc.setImage(image.getOriginalFilename());
                mcRepository.save(mc);
                isSucess = true;
            } catch (Exception e) {
                System.out.println("Add MC failed" + e.getMessage());
            }
        }

        return isSucess;
    }
    @Override
    public boolean updateMc(MultipartFile image,McDTO mcDto){
        boolean isSucess = false;
        boolean isUoloadImg = fileService.saveFiles(image);
        if(isUoloadImg){
            try{
                Optional<Mc> mc = mcRepository.findById(mcDto.getMcID());
                if(mc.isPresent()){
                    Mc newMc = mc.get();
                    BeanUtils.copyProperties(mcDto,newMc);
                    newMc.setImage(image.getOriginalFilename());
                    mcRepository.save(newMc);
                    isSucess = true;
                }

            } catch (Exception e) {
                System.out.println("Update MC failed" + e.getMessage());
            }
        }

        return isSucess;
    }
    @Override
    public boolean deleteMc(Integer mcId){
        boolean isSucess = false;
        try{
            Optional<Mc> mc = mcRepository.findById(mcId);
            if(mc.isPresent()){
                mcRepository.deleteById(mcId);
                isSucess = true;
            }

        } catch (Exception e) {
            System.out.println("Delete MC failed" + e.getMessage());
        }
        return isSucess;
    }
    @Override
    public McDTO findMcById(Integer mcId){
        Optional<Mc> mc = mcRepository.findById(mcId);
        McDTO mcDTO = new McDTO();
        try{
            if(mc.isPresent()){
                BeanUtils.copyProperties(mc.get(),mcDTO);
                mcDTO.setImage(mc.get().getImage());
                List<EventDTO> listeventdto = new ArrayList<>();
                for(Event e : mc.get().getListEvents()){
                    EventDTO eventDTO = new EventDTO();
                    BeanUtils.copyProperties(e,eventDTO);
                    listeventdto.add(eventDTO);
                }
                mcDTO.setListEvents(listeventdto);
            }
        } catch (Exception e) {
            System.out.println("Find MC failed" + e.getMessage());
        }
       return mcDTO;
    }

}
