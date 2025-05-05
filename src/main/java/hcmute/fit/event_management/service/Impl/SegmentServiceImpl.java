package hcmute.fit.event_management.service.Impl;


import com.cloudinary.Cloudinary;
import hcmute.fit.event_management.dto.SegmentDTO;
import hcmute.fit.event_management.dto.SpeakerDTO;
import hcmute.fit.event_management.entity.Segment;
import hcmute.fit.event_management.entity.Speaker;
import hcmute.fit.event_management.repository.EventRepository;
import hcmute.fit.event_management.repository.SegmentRepository;
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
    private EventRepository eventRepository;
    @Autowired
    private ISpeakerService speakerService;
    @Autowired
    private Cloudinary cloudinary;

    public SegmentServiceImpl(SegmentRepository segmentRepository) {
        this.segmentRepository = segmentRepository;
    }

    @Override
    public void addSegment(int eventId, SegmentDTO segment) throws Exception {
        if (segment.getSpeaker() != null) {
            SpeakerDTO speakerDTO = new SpeakerDTO();
            speakerDTO.setSpeakerName(segment.getSpeaker().getSpeakerName());
            speakerDTO.setSpeakerDesc(segment.getSpeaker().getSpeakerDesc());
            speakerDTO.setSpeakerImage(segment.getSpeaker().getSpeakerImage());
            Speaker speaker = speakerService.addSpeaker(speakerDTO);
            Segment newSegment = new Segment();
            BeanUtils.copyProperties(segment, newSegment);
            newSegment.setEvent(eventRepository.findById(eventId).orElseThrow(
                    () -> new Exception("Not found event by eventId " + eventId)));
            newSegment.setSpeaker(speaker);
        } else {
            Segment newSegment = new Segment();
            BeanUtils.copyProperties(segment, newSegment);
            newSegment.setEvent(eventRepository.findById(eventId).orElseThrow(
                    () -> new Exception("Not found event by eventId " + eventId)));

            segmentRepository.save(newSegment);
        }

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
            // Tạo URL từ public_id cho speakerImage
            String urlImage = cloudinary.url().generate(speaker.getSpeakerImage());
            System.out.println("day la url image cua speaker : " + urlImage);

            speakerDTO.setSpeakerImage(urlImage);

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

    @Override
    public void deleteById(Integer integer) {
        segmentRepository.deleteById(integer);
    }

    @Override
    public void saveEditSegment(int eventId, SegmentDTO segmentDTO) throws Exception {
        SpeakerDTO speakerDTO = new SpeakerDTO();
        speakerDTO.setSpeakerName(segmentDTO.getSpeaker().getSpeakerName());
        speakerDTO.setSpeakerDesc(segmentDTO.getSpeaker().getSpeakerDesc());
        speakerDTO.setSpeakerImage(segmentDTO.getSpeaker().getSpeakerImage());

        Speaker speaker = speakerService.saveSpeakerEdit(speakerDTO);

        Segment newSegment = new Segment();
        BeanUtils.copyProperties(segmentDTO, newSegment);
        newSegment.setEvent(eventRepository.findById(eventId).orElseThrow(()-> new Exception("Not found event by eventId "+eventId)));
        newSegment.setSpeaker(speaker);
        segmentRepository.save(newSegment);
    }
    @Override
    public void deleteSegmentByEventId(int eventId){
        List<Segment> list = segmentRepository.findByEventId(eventId);
        if(!list.isEmpty()){
            for(Segment segment : list){
                speakerService.deleteById(segment.getSpeaker().getSpeakerId());
                segmentRepository.delete(segment);
            }
        }

    }
}