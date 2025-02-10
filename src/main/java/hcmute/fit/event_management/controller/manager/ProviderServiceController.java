package hcmute.fit.event_management.controller.manager;

import hcmute.fit.event_management.dto.ProviderServiceDTO;
import hcmute.fit.event_management.service.IProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payload.Response;

import java.util.List;

@RestController
@RequestMapping("/man/proService")
public class ProviderServiceController {
    @Autowired
    private IProviderService providerService;


    @GetMapping("/{serviceId}")
    public ResponseEntity<?> findServiceById(@PathVariable int serviceId) {
        Response response = new Response();
        ProviderServiceDTO service = providerService.findServiceById(serviceId);
        response.setData(service);
        return ResponseEntity.ok(response);
    }

    @PostMapping("")
    public ResponseEntity<?> addService(@RequestBody ProviderServiceDTO serviceDTO) {
        Response response = new Response();
        response.setData(providerService.addServiceProvider(serviceDTO));
        return ResponseEntity.ok(response);
    }

    @PutMapping("")
    public ResponseEntity<?> updateService(@RequestBody ProviderServiceDTO serviceDTO) {
        Response response = new Response();
        response.setData(providerService.updateServiceProvider(serviceDTO));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<?> deleteService(@PathVariable int serviceId) {
        Response response = new Response();
        response.setData(providerService.deleteServiceProvider(serviceId));
        return ResponseEntity.ok(response);
    }

}
