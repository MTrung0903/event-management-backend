package hcmute.fit.event_management.service;

import hcmute.fit.event_management.entity.SpeakerEvent;
import hcmute.fit.event_management.entity.SponsorEvent;
import hcmute.fit.event_management.entity.keys.SpeakerEventId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface ISpeakerEventService {
    List<SpeakerEvent> findAll();

    List<SpeakerEvent> findAllById(Iterable<SpeakerEventId> speakerEventIds);

    <S extends SpeakerEvent> S save(S entity);

    Optional<SpeakerEvent> findById(SpeakerEventId speakerEventId);

    long count();

    boolean existsById(SpeakerEventId speakerEventId);

    void deleteById(SpeakerEventId speakerEventId);

    void delete(SpeakerEvent entity);

    List<SpeakerEvent> findAll(Sort sort);

    Page<SpeakerEvent> findAll(Pageable pageable);


    List<SpeakerEvent> findByEventId(int eventId);
}
