package hcmute.fit.event_management.controller.employee;

import hcmute.fit.event_management.dto.EventDTO;
import hcmute.fit.event_management.service.IEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payload.Response;

import java.util.List;

@RestController
@RequestMapping("/emp")
public class HomeEmployeeController {
    @Autowired
    IEventService eventService;

    @GetMapping("/event/{empId}")
    public ResponseEntity<?> findEventId(@PathVariable int empId) {
        List<EventDTO> events = eventService.getAllEventByEmp(empId);
        Response response = new Response();
        response.setData(events);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
