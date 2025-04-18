package hcmute.fit.event_management.controller.manager;

import hcmute.fit.event_management.dto.SpeakerDTO;
import hcmute.fit.event_management.dto.SpeakerEventDTO;
import hcmute.fit.event_management.dto.SpeakerEventDTO;
import hcmute.fit.event_management.entity.*;
import hcmute.fit.event_management.entity.Speaker;
import hcmute.fit.event_management.entity.SpeakerEvent;
import hcmute.fit.event_management.entity.keys.SpeakerEventId;
import hcmute.fit.event_management.entity.keys.SpeakerEventId;
import hcmute.fit.event_management.service.ISpeakerEventService;
import hcmute.fit.event_management.service.ISpeakerService;
import hcmute.fit.event_management.service.Impl.CloudinaryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import payload.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class SpeakerController {
    @Autowired
    ISpeakerService speakerService;
    @Autowired
    ISpeakerEventService speakerEventService;
    @Autowired
    CloudinaryService cloudinaryService;
    @GetMapping("/myevent/{eid}/speaker")
    public ResponseEntity<?> getSpeakersByEventId(@PathVariable("eid") int eid) {
        List<SpeakerEvent> speakerEvents = speakerEventService.findByEventId(eid);
        List<SpeakerDTO> speakerDTOS = new ArrayList<>();
        for (SpeakerEvent speakerEvent : speakerEvents) {
            SpeakerDTO speakerDTO = new SpeakerDTO();
            BeanUtils.copyProperties(speakerEvent.getSpeaker(), speakerDTO);
            speakerDTO.setSpeakerImage(cloudinaryService.getFileUrl(speakerEvent.getSpeaker().getSpeakerImage()));
            speakerDTOS.add(speakerDTO);
        }
        Response response = new Response(200, "", speakerDTOS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/myevent/{eid}/speaker")
    public ResponseEntity<?> createSpeakerByEventId(@PathVariable("eid") int eid, @ModelAttribute SpeakerDTO speakerDTO, // Nhận toàn bộ dữ liệu dạng text
                                                    @RequestParam(value = "speakerImageFile", required = false) MultipartFile speakerImageFile)
            throws IOException {
        Speaker speaker = new Speaker();
        String speakerImageUrl = null;
        BeanUtils.copyProperties(speakerDTO, speaker);
        if (speakerImageFile != null && !speakerImageFile.isEmpty()) {
            speakerImageUrl = cloudinaryService.uploadFile(speakerImageFile);
        }
        speaker.setSpeakerImage(speakerImageUrl);
        speaker = speakerService.save(speaker);
        SpeakerEvent speakerEvent = new SpeakerEvent();
        SpeakerEventId speakerEventId = new SpeakerEventId();
        speakerEventId.setSpeakerId(speaker.getSpeakerId());
        speakerEventId.setEventId(eid);
        speakerEvent.setId(speakerEventId);
        speakerEventService.save(speakerEvent);
        Response response = new Response(200, "", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PutMapping("/myevent/{eid}/speaker")
    public ResponseEntity<?> updateSpeakerByEventId(@PathVariable("eid") int eid, @ModelAttribute SpeakerDTO speakerDTO, // Nhận toàn bộ dữ liệu dạng text
                                                    @RequestParam(value = "speakerImageFile", required = false) MultipartFile speakerImageFile)
            throws IOException {
        Speaker speaker = speakerService.findById(speakerDTO.getSpeakerId()).orElse(new Speaker());
        String speakerImageUrl = null;
        BeanUtils.copyProperties(speakerDTO, speaker);
        if (speakerImageFile != null && !speakerImageFile.isEmpty()) {
            speakerImageUrl = cloudinaryService.uploadFile(speakerImageFile);
            speaker.setSpeakerImage(speakerImageUrl);
        }
        speaker = speakerService.save(speaker);
        SpeakerEvent speakerEvent = new SpeakerEvent();
        SpeakerEventId speakerEventId = new SpeakerEventId();
        speakerEventId.setSpeakerId(speaker.getSpeakerId());
        speakerEventId.setEventId(eid);
        speakerEvent.setId(speakerEventId);
        speakerEventService.save(speakerEvent);
        Response response = new Response(200, "", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @DeleteMapping("/myevent/{eid}/speaker/{speakerId}")
    public ResponseEntity<?> deleteSpeakerByEventId(@PathVariable("eid") int eid, @PathVariable("speakerId") int speakerId) throws IOException {
        SpeakerEventId speakerEventId = new SpeakerEventId();
        speakerEventId.setSpeakerId(speakerId);
        speakerEventId.setEventId(eid);
        speakerEventService.deleteById(speakerEventId);
        Speaker speaker = speakerService.findById(speakerId).orElse(new Speaker());
        cloudinaryService.deleteFile(speaker.getSpeakerImage());
        speakerService.deleteById(speakerId);
        Response response = new Response(200, "", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
