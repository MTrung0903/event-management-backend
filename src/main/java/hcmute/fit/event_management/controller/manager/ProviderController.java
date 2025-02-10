package hcmute.fit.event_management.controller.manager;

import hcmute.fit.event_management.dto.ProviderDTO;
import hcmute.fit.event_management.dto.ProviderServiceDTO;
import hcmute.fit.event_management.entity.Provider;
import hcmute.fit.event_management.entity.ProviderService;
import hcmute.fit.event_management.service.IProvider;
import hcmute.fit.event_management.service.IProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payload.Response;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/man/provider")
public class ProviderController {
    @Autowired
    private IProvider providerImpl;

    @Autowired
    private IProviderService providerService;


    @GetMapping("")
    public ResponseEntity<?> getAllProviders() {
        List<ProviderDTO> listProvider = providerImpl.getAllProviders();
        return new ResponseEntity<>(listProvider, HttpStatus.OK);
    }

    @GetMapping("/{providerId}")
    public ResponseEntity<?> getProviderById(@PathVariable("providerId") int providerId) {
        ProviderDTO provider = providerImpl.findProviderById(providerId);
        return new ResponseEntity<>(provider, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<?> addProvider(@RequestBody ProviderDTO provider) {
        Response response = new Response();
        response.setData(providerImpl.addProvider(provider));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping()
    public ResponseEntity<?> updateProvider(@RequestBody ProviderDTO provider) {
        Response response = new Response();
        response.setData(providerImpl.updateProvider(provider));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{providerId}")
    public ResponseEntity<?> deleteProvider(@PathVariable int providerId) {
        Response response = new Response();
        response.setData(providerImpl.deleteProvider(providerId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/{providerId}/service")
    public ResponseEntity<?> getListServiceOfProvider(@PathVariable int providerId) {
        Response response = new Response();
        List<ProviderServiceDTO> list = providerService.getServiceProviders(providerId);
        response.setData(list);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{providerId}/ser-in-event/{eventId}")
    public ResponseEntity<?> getListServiceInEvent(@PathVariable int providerId, @PathVariable int eventId) {
        Response response = new Response();
        response.setData(providerService.listServiceInEvent(eventId, providerId));
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{providerId}/ser-not-in-event/{eventId}")
    public ResponseEntity<?> getListServiceNotInEvent(@PathVariable int providerId, @PathVariable int eventId) {
        Response response = new Response();
        response.setData(providerService.listServiceNotInEvent(eventId, providerId));
        return ResponseEntity.ok(response);
    }
}

