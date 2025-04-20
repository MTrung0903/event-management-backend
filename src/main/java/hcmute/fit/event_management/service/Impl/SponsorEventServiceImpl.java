package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.entity.SponsorEvent;
import hcmute.fit.event_management.entity.keys.SponsorEventId;
import hcmute.fit.event_management.repository.SponsorEventRepository;
import hcmute.fit.event_management.service.ISponsorEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SponsorEventServiceImpl implements ISponsorEventService {
    @Autowired
    SponsorEventRepository sponsorEventRepository;

    @Override
    public List<SponsorEvent> findByEventId(int eventId) {
        return sponsorEventRepository.findByEventId(eventId);
    }

    @Override
    public void deleteById(SponsorEventId sponsorEventId) {
        sponsorEventRepository.deleteById(sponsorEventId);
    }

    @Override
    public <S extends SponsorEvent> S save(S entity) {
        return sponsorEventRepository.save(entity);
    }
    @Override
    public Optional<SponsorEvent> findById(SponsorEventId sponsorEventId) {
        return sponsorEventRepository.findById(sponsorEventId);
    }
}
