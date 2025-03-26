package hcmute.fit.event_management.service;

import hcmute.fit.event_management.entity.Speaker;

import java.util.Optional;

public interface ISpeakerService {
    Optional<Speaker> findById(Integer integer);

    void deleteById(Integer integer);
}
