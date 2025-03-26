package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.entity.Speaker;
import hcmute.fit.event_management.repository.SpeakerRepository;
import hcmute.fit.event_management.service.ISpeakerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SpeakerServiceImpl implements ISpeakerService {
    @Autowired
    private SpeakerRepository speakerRepository;

    public SpeakerServiceImpl(SpeakerRepository speakerRepository) {
        this.speakerRepository = speakerRepository;
    }

    @Override
    public Optional<Speaker> findById(Integer integer) {
        return speakerRepository.findById(integer);
    }

    @Override
    public void deleteById(Integer integer) {
        speakerRepository.deleteById(integer);
    }
}
