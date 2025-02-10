package hcmute.fit.event_management.service;

import hcmute.fit.event_management.dto.SpeakerDTO;
import hcmute.fit.event_management.entity.Speaker;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ISpeakerService {

    List<SpeakerDTO> getAllSpeakers();

    SpeakerDTO getSpeakerById(int id);

    boolean addSpeaker(MultipartFile imageSpeaker, SpeakerDTO speakerDTO);

    boolean updateSpeaker(MultipartFile imageSpeaker, SpeakerDTO speakerDTO);

    boolean deleteSpeaker(int id);
}
