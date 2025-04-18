package hcmute.fit.event_management.service;

import hcmute.fit.event_management.dto.SegmentDTO;

import java.util.List;

public interface ISegmentService {
    void addSegment(int eventId, SegmentDTO segment);
    List<SegmentDTO> getAllSegments(int eventId);
}
