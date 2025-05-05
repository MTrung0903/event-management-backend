package hcmute.fit.event_management.service;

import hcmute.fit.event_management.dto.SegmentDTO;

import java.util.List;

public interface ISegmentService {
    void addSegment(int eventId, SegmentDTO segment) throws Exception;

    List<SegmentDTO> getAllSegments(int eventId);

    void deleteById(Integer integer);

    void saveEditSegment(int eventId, SegmentDTO segmentDTO) throws Exception;

    void deleteSegmentByEventId(int eventId);

}