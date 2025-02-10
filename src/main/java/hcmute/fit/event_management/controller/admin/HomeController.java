package hcmute.fit.event_management.controller.admin;

import hcmute.fit.event_management.service.IEventService;
import hcmute.fit.event_management.service.IServiceEventSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import payload.Response;

@RestController
@RequestMapping("/admin")
public class HomeController {

    @Autowired
    IEventService eventService;

    @Autowired
    IServiceEventSerivce serviceEventSerivce;

    @GetMapping("/events/overview")
    public ResponseEntity<?> overview() {
        Response response= new Response(401, "Unauthorized", "Invalid credentials");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/provider/status")
    public String provider() {
        return "Hello World";
    }
    @GetMapping("/stats/overview")
    public String stats() {
        return "Hello World";
    }
}
