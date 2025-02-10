package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.entity.SponsorEvent;
import hcmute.fit.event_management.entity.keys.SponsorEventId;
import hcmute.fit.event_management.repository.SponsorEventRepository;
import hcmute.fit.event_management.service.ISponsorEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SponsorEventServiceImpl implements ISponsorEventService {
    @Autowired
    SponsorEventRepository sponsorEventRepository;



}
