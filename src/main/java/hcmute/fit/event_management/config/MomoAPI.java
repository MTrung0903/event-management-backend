package hcmute.fit.event_management.config;

import hcmute.fit.event_management.dto.MomoRequestPayment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="momo", url = "${momo.end-point}")
public interface MomoAPI {
    @PostMapping(value = "/create")
    ResponseEntity<?> createMomoQR(@RequestBody MomoRequestPayment request);
}
