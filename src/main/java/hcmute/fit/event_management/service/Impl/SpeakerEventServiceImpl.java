package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.entity.SpeakerEvent;
import hcmute.fit.event_management.entity.keys.SpeakerEventId;
import hcmute.fit.event_management.repository.SpeakerEventRepository;
import hcmute.fit.event_management.service.ISpeakerEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SpeakerEventServiceImpl implements ISpeakerEventService {
    @Autowired
    private SpeakerEventRepository speakerEventRepository;

    @Override
    public List<SpeakerEvent> findAll() {
        return speakerEventRepository.findAll();
    }

    @Override
    public List<SpeakerEvent> findAllById(Iterable<SpeakerEventId> speakerEventIds) {
        return speakerEventRepository.findAllById(speakerEventIds);
    }

    @Override
    public <S extends SpeakerEvent> S save(S entity) {
        return speakerEventRepository.save(entity);
    }

    @Override
    public Optional<SpeakerEvent> findById(SpeakerEventId speakerEventId) {
        return speakerEventRepository.findById(speakerEventId);
    }

    @Override
    public long count() {
        return speakerEventRepository.count();
    }

    @Override
    public boolean existsById(SpeakerEventId speakerEventId) {
        return speakerEventRepository.existsById(speakerEventId);
    }

    @Override
    public void deleteById(SpeakerEventId speakerEventId) {
        speakerEventRepository.deleteById(speakerEventId);
    }

    @Override
    public void delete(SpeakerEvent entity) {
        speakerEventRepository.delete(entity);
    }

    @Override
    public List<SpeakerEvent> findAll(Sort sort) {
        return speakerEventRepository.findAll(sort);
    }

    @Override
    public Page<SpeakerEvent> findAll(Pageable pageable) {
        return speakerEventRepository.findAll(pageable);
    }

    @Override
    public List<SpeakerEvent> findByEventId(int eventId) {
        return speakerEventRepository.findByEventId(eventId);
    }
}
