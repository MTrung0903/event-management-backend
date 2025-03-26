package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.repository.SponsorEventRepository;
import hcmute.fit.event_management.service.ISponsorEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SponsorEventServiceImpl implements ISponsorEventService {
    @Autowired
    SponsorEventRepository sponsorEventRepository;



}
