package hcmute.fit.event_management.controller.manager;

import hcmute.fit.event_management.dto.AttendeeDTO;
import hcmute.fit.event_management.entity.Attendee;
import hcmute.fit.event_management.service.IAttendeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payload.Response;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("man/attendee")
public class AttendeeController {
    @Autowired
    private IAttendeeService attendeeService;

    @GetMapping("/{id}")
    public ResponseEntity<?> findAttendee(@PathVariable("id") int id) {
        AttendeeDTO attendee = attendeeService.findAttendee(id);
        Response response = new Response();
        response.setData(attendee);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStatus(@RequestParam("id") int id,
                                            @RequestParam String status) {
        Response response = new Response();
        response.setData(attendeeService.updateStatus(id,status));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
