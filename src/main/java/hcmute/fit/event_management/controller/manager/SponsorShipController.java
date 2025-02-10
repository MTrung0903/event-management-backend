package hcmute.fit.event_management.controller.manager;

import hcmute.fit.event_management.dto.SponsorShipDTO;
import hcmute.fit.event_management.entity.SponsorShip;
import hcmute.fit.event_management.service.ISponsorShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payload.Response;

import java.util.List;

@RestController
@RequestMapping("/man/sponsorship")
public class SponsorShipController {
    @Autowired
    private ISponsorShipService sponsorShipService;

    @GetMapping("")
    public ResponseEntity<?> getAllSponsorShips() {
        List<SponsorShipDTO> list = sponsorShipService.getAllSponsorShips();
        Response response  = new Response();
        response.setData(list);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findSponsorById(@PathVariable int id){
        SponsorShipDTO shipDTO = sponsorShipService.getSponsorShipByID(id);
        Response response  = new Response();
        response.setData(shipDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("")
    public ResponseEntity<?> addSponsorShip(@RequestBody SponsorShipDTO sponsorShipDTO){
        Response response = new Response();
        response.setData(sponsorShipService.addSponsorShip(sponsorShipDTO));
        return ResponseEntity.ok(response);
    }

    @PutMapping("")
    public ResponseEntity<?> updateSponsorShip(@RequestBody SponsorShipDTO sponsorShipDTO){
        Response response = new Response();
        response.setData(sponsorShipService.updateSponsorShip(sponsorShipDTO));
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSponsorShip(@PathVariable int id){
        Response response = new Response();
        response.setData(sponsorShipService.deleteSponsorShip(id));
        return ResponseEntity.ok(response);
    }
}
