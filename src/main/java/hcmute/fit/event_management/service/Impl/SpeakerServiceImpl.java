package hcmute.fit.event_management.service.Impl;


import hcmute.fit.event_management.dto.SpeakerDTO;

import hcmute.fit.event_management.entity.Speaker;
import hcmute.fit.event_management.repository.SpeakerRepository;
import hcmute.fit.event_management.service.IFileService;
import hcmute.fit.event_management.service.ISpeakerService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SpeakerServiceImpl implements ISpeakerService {
    @Autowired
    private SpeakerRepository speakerRepository;

    @Autowired
    private IFileService fileService;


    public SpeakerServiceImpl(SpeakerRepository speakerRepository) {
        this.speakerRepository = speakerRepository;
    }

    @Override
    public List<SpeakerDTO> getAllSpeakers() {
        List<Speaker> speakers = speakerRepository.findAll();
        List<SpeakerDTO> speakerDTOs = new ArrayList<>();
        for (Speaker speaker : speakers) {
            SpeakerDTO speakerDTO = new SpeakerDTO();
            speakerDTO.setImage(speaker.getImage());
            BeanUtils.copyProperties(speaker, speakerDTO);
            speakerDTOs.add(speakerDTO);
        }
        return speakerDTOs;
    }
    @Override
    public SpeakerDTO getSpeakerById(int id) {
        Optional<Speaker> speaker = speakerRepository.findById(id);
        SpeakerDTO speakerDTO = new SpeakerDTO();
        if (speaker.isPresent()) {
            BeanUtils.copyProperties(speaker.get(), speakerDTO);
            speakerDTO.setImage(speaker.get().getImage());
            speakerDTO.setImage(speaker.get().getImage());
            return speakerDTO;
        }
        return null;
    }
    @Override
    public boolean addSpeaker(MultipartFile imageSpeaker, SpeakerDTO speakerDTO){
        boolean isSuccess = false;
        boolean isUoloadImg = fileService.saveFiles(imageSpeaker);
        if(isUoloadImg) {
            try{
                Speaker speaker = new Speaker();
                speaker.setImage(imageSpeaker.getOriginalFilename());
                speaker.setName(speakerDTO.getName());
                speaker.setEmail(speakerDTO.getEmail());
                speaker.setTitle(speakerDTO.getTitle());
                speaker.setPhone(speakerDTO.getPhone());
                speaker.setAddress(speakerDTO.getAddress());
                speaker.setDescription(speakerDTO.getDescription());
                speakerRepository.save(speaker);
                isSuccess = true;
            } catch (Exception e) {
                System.out.println("add speaker failed" + e.getMessage());
            }
        }

        return isSuccess;
    }
    @Override
    public boolean updateSpeaker(MultipartFile imageSpeaker, SpeakerDTO speakerDTO){
        boolean isSuccess = false;
        boolean isUoloadImg = fileService.saveFiles(imageSpeaker);
        if(isUoloadImg) {
            try{
                if(speakerRepository.findById(speakerDTO.getId()).isPresent()){
                    Speaker speaker = speakerRepository.findById(speakerDTO.getId()).get();
                    speaker.setImage(imageSpeaker.getOriginalFilename());
                    speaker.setName(speakerDTO.getName());
                    speaker.setEmail(speakerDTO.getEmail());
                    speaker.setTitle(speakerDTO.getTitle());
                    speaker.setPhone(speakerDTO.getPhone());
                    speaker.setAddress(speakerDTO.getAddress());
                    speaker.setDescription(speakerDTO.getDescription());
                    speakerRepository.save(speaker);
                    isSuccess = true;
                }else
                    throw new Exception("Speaker not found");

            } catch (Exception e) {
                System.out.println("add speaker failed" + e.getMessage());
            }
        }

        return isSuccess;
    }
    @Override
    public boolean deleteSpeaker(int id){
        boolean isSuccess = false;
        try
        {
            if(speakerRepository.findById(id).isPresent()){
                speakerRepository.deleteById(id);
                isSuccess = true;
            }
        } catch (Exception e) {
            System.out.println("Delete speaker failed" + e.getMessage());
        }
        return isSuccess;
    }
}
