package hcmute.fit.event_management.controller.manager;

import com.cloudinary.Cloudinary;
import hcmute.fit.event_management.CloudinaryConfig;
import hcmute.fit.event_management.dto.SponsorDTO;
import hcmute.fit.event_management.dto.SponsorEventDTO;
import hcmute.fit.event_management.dto.SponsorShipDTO;
import hcmute.fit.event_management.entity.Event;
import hcmute.fit.event_management.entity.Sponsor;
import hcmute.fit.event_management.entity.SponsorEvent;
import hcmute.fit.event_management.entity.keys.SponsorEventId;
import hcmute.fit.event_management.service.IEventService;
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
import java.util.Map;

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
    @GetMapping("/myevent/sponsor")
    public ResponseEntity<?> getSponsorsByEventId(@RequestParam("eid") int eid) {
        List<SponsorEvent> sponsorEvents = sponsorEventService.findByEventId(eid);
        List<SponsorEventDTO> sponsorEventDTOs = new ArrayList<>();
        for (SponsorEvent sponsorEvent : sponsorEvents) {
            SponsorEventDTO sponsorEventDTO = new SponsorEventDTO();
            sponsorEventDTO.setSponsorId(sponsorEvent.getSponsor().getSponsorId());
            sponsorEventDTO.setSponsorName(sponsorEvent.getSponsor().getSponsorName());
            sponsorEventDTO.setSponsorEmail(sponsorEvent.getSponsor().getSponsorEmail());
            sponsorEventDTO.setSponsorAddress(sponsorEvent.getSponsor().getSponsorAddress());
            sponsorEventDTO.setSponsorLogo(cloudinaryService.getFileUrl(sponsorEvent.getSponsor().getSponsorLogo()));
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
        Response response = new Response(200,"",sponsorEventDTOs);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @PostMapping("/myevent/sponsor")
    public ResponseEntity<?> createSponsorByEventId(@RequestParam("eid") int eid,@ModelAttribute SponsorEventDTO sponsorEventDTO, // Nhận toàn bộ dữ liệu dạng text
                                                    @RequestParam(value = "sponsorLogoFile", required = false) MultipartFile sponsorLogoFile,
                                                    @RequestParam(value = "sponsorContractFile", required = false) MultipartFile sponsorContract) throws IOException {
        System.out.println("Nhận sponsorLogoFile: " + (sponsorLogoFile != null ? sponsorLogoFile.getOriginalFilename() : "Không có file"));
        System.out.println("Nhận sponsorContractFile: " + (sponsorContract != null ? sponsorContract.getOriginalFilename() : "Không có file"));
        Sponsor sponsor = sponsorService.findById(sponsorEventDTO.getSponsorId()).orElse(new Sponsor());
        String sponsorLogoUrl = null;
        String sponsorContractUrl = null;
        BeanUtils.copyProperties(sponsorEventDTO, sponsor);
        System.out.println("============" + sponsorEventDTO.getSponsorLogo() + "============");
        if (sponsorLogoFile != null && !sponsorLogoFile.isEmpty()) {
            sponsorLogoUrl = cloudinaryService.uploadFile(sponsorLogoFile);
        }
        if (sponsorContract != null && !sponsorContract.isEmpty()) {
            sponsorContractUrl = cloudinaryService.uploadFile(sponsorContract);
        }
        sponsor.setSponsorLogo(sponsorLogoUrl);
        sponsor = sponsorService.save(sponsor);
        SponsorEvent sponsorEvent = new SponsorEvent();
        BeanUtils.copyProperties(sponsorEventDTO, sponsorEvent);
        sponsorEvent.setSponsorContract(sponsorContractUrl);
        SponsorEventId sponsorEventId = new SponsorEventId();
        sponsorEventId.setSponsorId(sponsor.getSponsorId());
        sponsorEventId.setEventId(eid);
        sponsorEvent.setId(sponsorEventId);
        sponsorEventService.save(sponsorEvent);
        Response response = new Response(200,"",null);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @PutMapping("/myevent/sponsor")
    public ResponseEntity<?> updateSponsorByEventId(@RequestParam("eid") int eid, @ModelAttribute SponsorEventDTO sponsorEventDTO, // Nhận toàn bộ dữ liệu dạng text
                                                    @RequestParam(value = "sponsorLogoFile", required = false) MultipartFile sponsorLogoFile,
                                                    @RequestParam(value = "sponsorContractFile", required = false) MultipartFile sponsorContract) throws IOException {
        Sponsor sponsor = new Sponsor();
        SponsorEvent sponsorEvent = new SponsorEvent();
        String sponsorLogoUrl = null;
        String sponsorContractUrl = null;
        BeanUtils.copyProperties(sponsorEventDTO, sponsor);
        if (sponsorLogoFile != null && !sponsorLogoFile.isEmpty()) {
            sponsorLogoUrl = cloudinaryService.uploadFile(sponsorLogoFile);
            sponsor.setSponsorLogo(sponsorLogoUrl);
        }
        if (sponsorContract != null && !sponsorContract.isEmpty()) {
            sponsorContractUrl = cloudinaryService.uploadFile(sponsorContract);
            sponsorEvent.setSponsorContract(sponsorContractUrl);
        }
        sponsor = sponsorService.save(sponsor);
        BeanUtils.copyProperties(sponsorEventDTO, sponsorEvent);
        SponsorEventId sponsorEventId = new SponsorEventId();
        sponsorEventId.setSponsorId(sponsor.getSponsorId());
        sponsorEventId.setEventId(eid);
        sponsorEvent.setId(sponsorEventId);
        sponsorEventService.save(sponsorEvent);
        Response response = new Response(200,"",null);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @DeleteMapping("/myevent/sponsor")
    public ResponseEntity<?> deleteSponsorByEventId(@RequestParam("eid") int eid, @RequestParam("sponsorId") int sponsorId ) {
        sponsorService.deleteById(sponsorId);
        SponsorEventId sponsorEventId = new SponsorEventId();
        sponsorEventId.setSponsorId(sponsorId);
        sponsorEventId.setEventId(eid);
        sponsorEventService.deleteById(sponsorEventId);
        Response response = new Response(200,"",null);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
