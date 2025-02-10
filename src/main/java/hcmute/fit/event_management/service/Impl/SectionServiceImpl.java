package hcmute.fit.event_management.service.Impl;


import hcmute.fit.event_management.dto.SectionDTO;

import hcmute.fit.event_management.entity.Section;
import hcmute.fit.event_management.entity.Speaker;
import hcmute.fit.event_management.repository.EventRepository;
import hcmute.fit.event_management.repository.SectionRepository;
import hcmute.fit.event_management.repository.SpeakerRepository;
import hcmute.fit.event_management.service.IFileService;
import hcmute.fit.event_management.service.ISectionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hcmute.fit.event_management.util.TimeUtil;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SectionServiceImpl implements ISectionService {
    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private SpeakerRepository speakerRepository;
    @Autowired
    private IFileService fileService;

    public SectionServiceImpl(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    @Override
    public List<SectionDTO> getSectionOfEvent(int eventId) {
        if (eventRepository.findById(eventId).isPresent()) {
            List<Section> sections = sectionRepository.findByEventId(eventId);
            List<SectionDTO> sectionDTOs = new ArrayList<>();
            for (Section section : sections) {
                SectionDTO sectionDTO = new SectionDTO();
                sectionDTO.setSectionId(section.getId());
                sectionDTO.setStartTime(section.getStartTime().toString());
                sectionDTO.setEndTime(section.getEndTime().toString());
                sectionDTO.setEventId(eventId);
                if (section.getSpeaker() != null) {
                    sectionDTO.setSpeakerId(section.getSpeaker().getId());
                }
                sectionDTO.setSectionTitle(section.getSectionTitle());
                sectionDTO.setSectionDescription(section.getSectionDescription());
                sectionDTO.setHandOut(section.getHandOut());
                sectionDTOs.add(sectionDTO);
                Optional<Speaker> speaker = speakerRepository.findById(section.getSpeaker().getId());
                speaker.ifPresent(value -> sectionDTO.setSpeakerName(value.getName()));
            }
            return sectionDTOs;
        }
        return null;
    }

    @Override
    public SectionDTO getSectionById(int sectionId) {
        Optional<Section> section = sectionRepository.findById(sectionId);
        SectionDTO sectionDTO = new SectionDTO();
        if (section.isPresent()) {
            sectionDTO.setSectionId(section.get().getId());
            sectionDTO.setStartTime(section.get().getStartTime().toString());
            sectionDTO.setEndTime(section.get().getEndTime().toString());
            sectionDTO.setSectionTitle(section.get().getSectionTitle());
            sectionDTO.setSectionDescription(section.get().getSectionDescription());
            sectionDTO.setHandOut(section.get().getHandOut());
            if (section.get().getSpeaker() != null) {
                sectionDTO.setSpeakerId(section.get().getSpeaker().getId());
            }
            if (section.get().getEvent() != null) {
                sectionDTO.setEventId(section.get().getEvent().getEventID());
            }

        } else
            System.console().printf("Section not found");
        return sectionDTO;
    }

    @Override
    public SectionDTO addSection(MultipartFile handout, SectionDTO sectionDTO) {
        try {
            Section section = new Section();
            Time start = TimeUtil.parseTime(sectionDTO.getStartTime());
            Time end = TimeUtil.parseTime(sectionDTO.getEndTime());
            section.setStartTime(start);
            section.setEndTime(end);
            section.setSectionTitle(sectionDTO.getSectionTitle());
            section.setSectionDescription(sectionDTO.getSectionDescription());
            if (eventRepository.findById(sectionDTO.getEventId()).isPresent()) {
                section.setEvent(eventRepository.findById(sectionDTO.getEventId()).get());
            }
            if (speakerRepository.findById(sectionDTO.getSpeakerId()).isPresent()) {
                section.setSpeaker(speakerRepository.findById(sectionDTO.getSpeakerId()).get());
            }
            boolean isUoloadImg = fileService.saveFiles(handout);
            if (isUoloadImg) {
                section.setHandOut(handout.getOriginalFilename());
            }
            sectionRepository.save(section);
            sectionDTO.setHandOut(handout.getOriginalFilename());
            sectionDTO.setSectionId(section.getId());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return sectionDTO;
    }

    @Override
    public boolean updateSection(MultipartFile handout, SectionDTO sectionDTO) {
        boolean isSuccess = false;
        try {
            Section section = new Section();
            section.setId(sectionDTO.getSectionId());
            Time start = TimeUtil.parseTime(sectionDTO.getStartTime());
            Time end = TimeUtil.parseTime(sectionDTO.getEndTime());
            section.setStartTime(start);
            section.setEndTime(end);
            section.setSectionTitle(sectionDTO.getSectionTitle());
            section.setSectionDescription(sectionDTO.getSectionDescription());
            if (eventRepository.findById(sectionDTO.getEventId()).isPresent()) {
                section.setEvent(eventRepository.findById(sectionDTO.getEventId()).get());
            }
            if (speakerRepository.findById(sectionDTO.getSpeakerId()).isPresent()) {
                section.setSpeaker(speakerRepository.findById(sectionDTO.getSpeakerId()).get());
            }
            boolean isUoloadImg = fileService.saveFiles(handout);
            if (isUoloadImg) {
                section.setHandOut(handout.getOriginalFilename());
            }
            sectionRepository.save(section);
            isSuccess = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return isSuccess;
    }

    @Override
    public boolean deleteSection(int sectionId) {
        boolean isSuccess = false;
        Optional<Section> section = sectionRepository.findById(sectionId);
        if (section.isPresent()) {
            sectionRepository.deleteById(sectionId);
            isSuccess = true;
        }
        return isSuccess;
    }
}
