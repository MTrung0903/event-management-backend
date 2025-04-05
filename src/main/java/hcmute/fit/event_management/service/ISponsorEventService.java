package hcmute.fit.event_management.service;


import hcmute.fit.event_management.entity.SponsorEvent;
import hcmute.fit.event_management.entity.keys.SponsorEventId;

import java.util.List;
import java.util.Optional;

public interface ISponsorEventService {

    List<SponsorEvent> findByEventId(int eventId);

    void deleteById(SponsorEventId sponsorEventId);

    <S extends SponsorEvent> S save(S entity);

    Optional<SponsorEvent> findById(SponsorEventId sponsorEventId);
}
