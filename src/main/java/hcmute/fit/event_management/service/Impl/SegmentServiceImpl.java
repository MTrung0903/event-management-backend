package hcmute.fit.event_management.service.Impl;

import com.cloudinary.Cloudinary;
import hcmute.fit.event_management.dto.SegmentDTO;
import hcmute.fit.event_management.dto.SpeakerDTO;
import hcmute.fit.event_management.entity.Segment;
import hcmute.fit.event_management.entity.Speaker;
import hcmute.fit.event_management.repository.SegmentRepository;
import hcmute.fit.event_management.service.IEventService;
import hcmute.fit.event_management.service.ISegmentService;
import hcmute.fit.event_management.service.ISpeakerService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SegmentServiceImpl implements ISegmentService {
    @Autowired
    private SegmentRepository segmentRepository;
    @Autowired
    private IEventService eventService;
    @Autowired
    private ISpeakerService speakerService;
    @Autowired
    private CloudinaryService cloudinary;

    public SegmentServiceImpl(SegmentRepository segmentRepository) {
        this.segmentRepository = segmentRepository;
    }

    @Override
    public void addSegment(int eventId, SegmentDTO segment) {
        SpeakerDTO speakerDTO = new SpeakerDTO();
        speakerDTO.setSpeakerName(segment.getSpeaker().getSpeakerName());
        speakerDTO.setSpeakerDesc(segment.getSpeaker().getSpeakerDesc());
        speakerDTO.setSpeakerImage(segment.getSpeaker().getSpeakerImage());
        Speaker speaker = speakerService.addSpeaker(speakerDTO);

        Segment newSegment = new Segment();
        BeanUtils.copyProperties(segment, newSegment);
        newSegment.setEvent(eventService.findById(eventId).get());
        newSegment.setSpeaker(speaker);
        segmentRepository.save(newSegment);
    }
    @Override
    public List<SegmentDTO> getAllSegments(int eventId) {
        List<Segment> list = segmentRepository.findByEventId(eventId);
        List<SegmentDTO> dtos = new ArrayList<>();
        for (Segment segment : list) {
            SegmentDTO dto = new SegmentDTO();
            Speaker speaker = segment.getSpeaker();
            SpeakerDTO speakerDTO = new SpeakerDTO();
            BeanUtils.copyProperties(speaker, speakerDTO);
            BeanUtils.copyProperties(segment, dto);
            dto.setEventID(eventId);
            dto.setStartTime(segment.getStartTime());
            dto.setEndTime(segment.getEndTime());
            dto.setSegmentId(segment.getSegmentId());
            dto.setSpeaker(speakerDTO);
            dtos.add(dto);
        }
        return dtos;

    }
}
