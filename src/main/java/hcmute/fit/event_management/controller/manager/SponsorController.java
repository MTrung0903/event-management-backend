package hcmute.fit.event_management.controller.manager;

import hcmute.fit.event_management.dto.SponsorDTO;
import hcmute.fit.event_management.dto.SponsorEventDTO;
import hcmute.fit.event_management.entity.Event;
import hcmute.fit.event_management.entity.Sponsor;
import hcmute.fit.event_management.entity.SponsorEvent;
import hcmute.fit.event_management.entity.keys.SponsorEventId;
import hcmute.fit.event_management.service.IEventService;
import hcmute.fit.event_management.service.IFileService;
import hcmute.fit.event_management.service.ISponsorEventService;
import hcmute.fit.event_management.service.ISponsorService;

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
    CloudinaryService cloudinaryService;
    @Autowired
    IFileService fileService;

    @GetMapping("/myevent/{eid}/sponsor")
    public ResponseEntity<?> getSponsorsByEventId(@PathVariable("eid") int eid) {
        List<SponsorEvent> sponsorEvents = sponsorEventService.findByEventId(eid);
        List<SponsorEventDTO> sponsorEventDTOs = new ArrayList<>();
        for (SponsorEvent sponsorEvent : sponsorEvents) {
            SponsorEventDTO sponsorEventDTO = new SponsorEventDTO();
            sponsorEventDTO.setSponsorId(sponsorEvent.getSponsor().getSponsorId());
            sponsorEventDTO.setSponsorName(sponsorEvent.getSponsor().getSponsorName());
            sponsorEventDTO.setSponsorEmail(sponsorEvent.getSponsor().getSponsorEmail());
            sponsorEventDTO.setSponsorAddress(sponsorEvent.getSponsor().getSponsorAddress());
            sponsorEventDTO.setSponsorLogo(sponsorEvent.getSponsor().getSponsorLogo());
            sponsorEventDTO.setSponsorPhone(sponsorEvent.getSponsor().getSponsorPhone());
            sponsorEventDTO.setSponsorWebsite(sponsorEvent.getSponsor().getSponsorWebsite());
            sponsorEventDTO.setSponsorRepresentativeName(sponsorEvent.getSponsor().getSponsorRepresentativeName());
            sponsorEventDTO.setSponsorRepresentativeEmail(sponsorEvent.getSponsor().getSponsorRepresentativeEmail());
            sponsorEventDTO.setSponsorRepresentativePhone(sponsorEvent.getSponsor().getSponsorRepresentativePhone());
            sponsorEventDTO.setSponsorRepresentativePosition(sponsorEvent.getSponsor().getSponsorRepresentativePosition());
            sponsorEventDTO.setSponsorType(sponsorEvent.getSponsorType());
            sponsorEventDTO.setSponsorLevel(sponsorEvent.getSponsorLevel());
            sponsorEventDTO.setSponsorAmount(sponsorEvent.getSponsorAmount());
            sponsorEventDTO.setSponsorContract(sponsorEvent.getSponsorContract());
            sponsorEventDTO.setSponsorContribution(sponsorEvent.getSponsorContribution());
            sponsorEventDTO.setSponsorStartDate(sponsorEvent.getSponsorStartDate());
            sponsorEventDTO.setSponsorEndDate(sponsorEvent.getSponsorEndDate());
            sponsorEventDTO.setSponsorStatus(sponsorEvent.getSponsorStatus());
            sponsorEventDTOs.add(sponsorEventDTO);
        }
        Response response = new Response(1, "SUCCESSFULLY", sponsorEventDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/myevent/{eid}/sponsor")
    public ResponseEntity<?> createSponsorByEventId(
            @PathVariable("eid") int eid,
            @ModelAttribute SponsorEventDTO sponsorEventDTO,
            @RequestParam(value = "sponsorLogoFile", required = false) MultipartFile sponsorLogoFile,
            @RequestParam(value = "sponsorContractFile", required = false) MultipartFile sponsorContract) throws IOException {

        Response response;
        Optional<Event> eventOptional = eventService.findById(eid);
        if (eventOptional.isEmpty()) {
            response = new Response(0, "Event not exist", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        Event event = eventOptional.get();
        List<SponsorEvent> sponsors = event.getSponsorEvents();
        for (SponsorEvent sponsorEvent : sponsors) {
            if (sponsorEvent.getSponsor().getSponsorEmail().equals(sponsorEventDTO.getSponsorEmail()) ||
                    sponsorEvent.getSponsor().getSponsorPhone().equals(sponsorEventDTO.getSponsorPhone())
            ) {
                response = new Response(0, "The sponsor has existed in this event.", null);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
        String sponsorLogoUrl = null;
        String sponsorContractUrl = null;
        Sponsor sponsor = new Sponsor();
        BeanUtils.copyProperties(sponsorEventDTO, sponsor);
        if (sponsorLogoFile != null && !sponsorLogoFile.isEmpty()) {
            sponsorLogoUrl = fileService.saveFiles(sponsorLogoFile);
            sponsor.setSponsorLogo(sponsorLogoUrl);
        }
        // Lưu hoặc cập nhật sponsor
        sponsor = sponsorService.save(sponsor);
        // Kiểm tra sponsor đã tồn tại trong sự kiện chưa

        // Upload hợp đồng nếu có
        if (sponsorContract != null && !sponsorContract.isEmpty()) {
            sponsorContractUrl = fileService.saveFiles(sponsorContract);
        }
        // Tạo sponsorEvent
        SponsorEvent sponsorEvent = new SponsorEvent();
        BeanUtils.copyProperties(sponsorEventDTO, sponsorEvent);
        sponsorEvent.setSponsor(sponsor);
        sponsorEvent.setSponsorContract(sponsorContractUrl);

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
            sponsor_event.setSponsorAmount(dto.getSponsorAmount());
            sponsor_event.setSponsorContribution(dto.getSponsorContribution());
            sponsor_event.setSponsorStartDate(dto.getSponsorStartDate());
            sponsor_event.setSponsorEndDate(dto.getSponsorEndDate());
            sponsor_event.setSponsorStatus(dto.getSponsorStatus());
            sponsor_event.setSponsorType(dto.getSponsorType());
            sponsor_event.setSponsorContract(dto.getSponsorContract());
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

    @PutMapping("/myevent/{eid}/sponsor")
    public ResponseEntity<?> updateSponsorByEventId(@PathVariable("eid") int eid, @ModelAttribute SponsorEventDTO sponsorEventDTO, // Nhận toàn bộ dữ liệu dạng text
                                                    @RequestParam(value = "sponsorLogoFile", required = false) MultipartFile sponsorLogoFile,
                                                    @RequestParam(value = "sponsorContractFile", required = false) MultipartFile sponsorContract) throws IOException {
        Response response = new Response();
        Optional<Sponsor> sponsorOpt = sponsorService.findById(sponsorEventDTO.getSponsorId());
        if (sponsorOpt.isEmpty()) {
            response = new Response(0, "Sponsor not exist.", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        Sponsor sponsor = sponsorOpt.get();
        BeanUtils.copyProperties(sponsorEventDTO, sponsor);

        if (sponsorLogoFile != null && !sponsorLogoFile.isEmpty()) {
            String sponsorLogoUrl = fileService.saveFiles(sponsorLogoFile);
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

        if (sponsorContract != null && !sponsorContract.isEmpty()) {
            String sponsorContractUrl = fileService.saveFiles(sponsorContract);
            sponsorEvent.setSponsorContract(sponsorContractUrl);
        }

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
