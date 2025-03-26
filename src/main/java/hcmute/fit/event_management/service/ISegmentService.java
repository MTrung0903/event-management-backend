package hcmute.fit.event_management.service;

import hcmute.fit.event_management.dto.SegmentDTO;

public interface ISegmentService {
    void addSegment(int eventId, SegmentDTO segment);
}
