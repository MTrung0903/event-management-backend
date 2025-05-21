package hcmute.fit.event_management.controller.manager;

import hcmute.fit.event_management.dto.SponsorDTO;
import hcmute.fit.event_management.dto.SponsorEventDTO;
import hcmute.fit.event_management.entity.Event;
import hcmute.fit.event_management.entity.Sponsor;
import hcmute.fit.event_management.entity.SponsorEvent;
import hcmute.fit.event_management.entity.keys.SponsorEventId;
import hcmute.fit.event_management.service.*;

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
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class SponsorController {
    @Autowired
    ISponsorService sponsorService;
    @Autowired
    ISponsorEventService sponsorEventService;
    @Autowired
    IEventService eventService;
    @Autowired
    IFileService fileService;
    @Autowired
    SponsorEventMapper sponsorEventMapper;
    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("/myevent/{eid}/sponsor")
    public ResponseEntity<?> getSponsorsByEventId(@PathVariable("eid") int eid) {
        List<SponsorEvent> sponsorEvents = sponsorEventService.findByEventId(eid);
        List<SponsorEventDTO> sponsorEventDTOs = sponsorEvents.stream()
                .map(sponsorEventMapper::toSponsorEventDTO)
                .toList();

        Response response = new Response(1, "SUCCESSFULLY", sponsorEventDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{userId}/sponsor")
    public ResponseEntity<?> getAllSponsors(@PathVariable("userId") int userId) {
        List<SponsorEvent> sponsorEvents = sponsorEventService.findByEventUserUserId(userId);
        List<SponsorEventDTO> sponsorEventDTOs = sponsorEvents.stream()
                .map(sponsorEventMapper::toSponsorEventDTO)
                .toList();

        Response response = new Response(1, "SUCCESSFULLY", sponsorEventDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/myevent/{eid}/sponsor")
    public ResponseEntity<?> createSponsorByEventId(
            @PathVariable("eid") int eid,
            @ModelAttribute SponsorEventDTO sponsorEventDTO,
            @RequestParam(value = "sponsorLogoFile", required = false) MultipartFile sponsorLogoFile) throws IOException {
        System.out.println(sponsorEventDTO);
        Response response;
        Optional<Event> eventOptional = eventService.findById(eid);
        if (eventOptional.isEmpty()) {
            response = new Response(0, "Event not exist", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        String sponsorLogoUrl;
        Sponsor sponsor = new Sponsor();
        System.out.println(sponsor);
        BeanUtils.copyProperties(sponsorEventDTO, sponsor);
        if (sponsorLogoFile != null && !sponsorLogoFile.isEmpty()) {
//            sponsorLogoUrl = fileService.saveFiles(sponsorLogoFile);
            sponsorLogoUrl = cloudinaryService.uploadFile(sponsorLogoFile);
            sponsor.setSponsorLogo(sponsorLogoUrl);
        }
        // Lưu hoặc cập nhật sponsor
        sponsor = sponsorService.save(sponsor);
        // Kiểm tra sponsor đã tồn tại trong sự kiện chưa

        // Upload hợp đồng nếu có

        // Tạo sponsorEvent
        SponsorEvent sponsorEvent = new SponsorEvent();
        BeanUtils.copyProperties(sponsorEventDTO, sponsorEvent);
        sponsorEvent.setSponsor(sponsor);


        SponsorEventId sponsorEventId = new SponsorEventId();
        sponsorEventId.setSponsorId(sponsor.getSponsorId());
        sponsorEventId.setEventId(eid);
        sponsorEvent.setId(sponsorEventId);

        sponsorEventService.save(sponsorEvent);
        response = new Response(1, "Success added sponsor at the event.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/myevent/{eid}/sponsors/import")
    public ResponseEntity<?> importSponsor(
            @PathVariable("eid") int eid,
            @RequestBody List<SponsorEventDTO> sponsorEventDTOS
    ) {
        Response response;
        Optional<Event> eventOpt = eventService.findById(eid);
        if (eventOpt.isEmpty()) {
            response = new Response(0, "Event not exist!.", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        Event event = eventOpt.get();
        List<SponsorEvent> savedSponsors = new ArrayList<>();
        List<SponsorEvent> sponsors = event.getSponsorEvents();
        for (SponsorEventDTO dto : sponsorEventDTOS) {
            Sponsor sponsor = new Sponsor();
            SponsorEvent sponsor_event = new SponsorEvent();
            for (SponsorEvent sponsorEvent : sponsors) {
                if (sponsorEvent.getSponsor().getSponsorEmail().equals(dto.getSponsorEmail()) ||
                        sponsorEvent.getSponsor().getSponsorPhone().equals(dto.getSponsorPhone())
                ) {
                    sponsor = sponsorEvent.getSponsor();
                    sponsor_event = sponsorEvent;
                    break;
                }
            }
            sponsor.setSponsorName(dto.getSponsorName());
            sponsor.setSponsorEmail(dto.getSponsorEmail());
            sponsor.setSponsorPhone(dto.getSponsorPhone());
            sponsor.setSponsorWebsite(dto.getSponsorWebsite());
            sponsor.setSponsorAddress(dto.getSponsorAddress());
            sponsor.setSponsorLogo(dto.getSponsorLogo());
            sponsor.setSponsorRepresentativeName(dto.getSponsorRepresentativeName());
            sponsor.setSponsorRepresentativeEmail(dto.getSponsorRepresentativeEmail());
            sponsor.setSponsorRepresentativePhone(dto.getSponsorRepresentativePhone());
            sponsor.setSponsorRepresentativePosition(dto.getSponsorRepresentativePosition());
            sponsor_event.setSponsorLevel(dto.getSponsorLevel());
            sponsor_event.setSponsorStartDate(dto.getSponsorStartDate());
            sponsor_event.setSponsorEndDate(dto.getSponsorEndDate());
            sponsor_event.setSponsorStatus(dto.getSponsorStatus());
            sponsor_event.setSponsorType(dto.getSponsorType());
            SponsorEventId sponsorEventId = new SponsorEventId();
            sponsorEventId.setSponsorId(sponsor.getSponsorId());
            sponsorEventId.setEventId(eid);
            sponsor_event.setId(sponsorEventId);
            sponsorEventService.save(sponsor_event);
            savedSponsors.add(sponsor_event);
        }
        sponsorEventService.saveAll(savedSponsors);
        if (savedSponsors.isEmpty()) {
            response = new Response(0, "All sponsors were in this event.", null);
        } else {
            response = new Response(1, "Import successful", null);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/myevent/{eid}/sponsor/{sponsorId}")
    public ResponseEntity<?> updateSponsorByEventId(@PathVariable("eid") int eid, @PathVariable("sponsorId") int sponsorId, @ModelAttribute SponsorEventDTO sponsorEventDTO, // Nhận toàn bộ dữ liệu dạng text
                                                    @RequestParam(value = "sponsorLogoFile", required = false) MultipartFile sponsorLogoFile) throws IOException {
        Response response = new Response();
        Optional<Sponsor> sponsorOpt = sponsorService.findById(sponsorId);
        if (sponsorOpt.isEmpty()) {
            response = new Response(0, "Sponsor not exist.", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        Sponsor sponsor = sponsorOpt.get();
        BeanUtils.copyProperties(sponsorEventDTO, sponsor);
        if (sponsorLogoFile != null && !sponsorLogoFile.isEmpty()) {
//            String sponsorLogoUrl = fileService.saveFiles(sponsorLogoFile);
            String sponsorLogoUrl = cloudinaryService.uploadFile(sponsorLogoFile);
            sponsor.setSponsorLogo(sponsorLogoUrl);
        }
        sponsor = sponsorService.save(sponsor);
        SponsorEventId sponsorEventId = new SponsorEventId();
        sponsorEventId.setSponsorId(sponsor.getSponsorId());
        sponsorEventId.setEventId(eid);
        SponsorEvent sponsorEvent = sponsorEventService.findById(sponsorEventId).orElse(new SponsorEvent());
        BeanUtils.copyProperties(sponsorEventDTO, sponsorEvent);
        // gán ID và quan hệ
        sponsorEvent.setId(sponsorEventId);
        sponsorEvent.setSponsor(sponsor);
        Event event = eventService.findById(eid).orElseThrow(() -> new RuntimeException("Event not found"));
        sponsorEvent.setEvent(event);

        sponsorEventService.save(sponsorEvent);
        response = new Response(1, "SUCCESSFULLY", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/myevent/{eid}/sponsor/{sponsorId}")
    public ResponseEntity<?> deleteSponsorByEventId(@PathVariable("eid") int eid, @PathVariable("sponsorId") int sponsorId) throws IOException {
        SponsorEventId sponsorEventId = new SponsorEventId();
        sponsorEventId.setSponsorId(sponsorId);
        sponsorEventId.setEventId(eid);
        sponsorEventService.deleteById(sponsorEventId);
        sponsorService.deleteById(sponsorId);
        Response response = new Response(1, "SUCCESSFULLY", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
