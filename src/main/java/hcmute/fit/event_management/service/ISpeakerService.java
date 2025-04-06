package hcmute.fit.event_management.service;

import hcmute.fit.event_management.dto.SpeakerDTO;
import hcmute.fit.event_management.entity.Speaker;

import java.util.Optional;

public interface ISpeakerService {
    Optional<Speaker> findById(Integer integer);

    void deleteById(Integer integer);

    Speaker addSpeaker(SpeakerDTO speakerDTO);

    Speaker saveSpeakerEdit(SpeakerDTO speakerDTO);
}
