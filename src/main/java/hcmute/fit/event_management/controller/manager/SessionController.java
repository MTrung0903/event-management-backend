package hcmute.fit.event_management.controller.manager;

import hcmute.fit.event_management.dto.SessionDTO;
import hcmute.fit.event_management.service.ISessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/segment")
public class SessionController {
    @Autowired
    private ISessionService segmentService;

//    @GetMapping("/{eventId}/getSegment")
//    public ResponseEntity<List<SessionDTO>> getSegmentByEventId(@PathVariable("eventId") int eventId) {
//        List<SessionDTO> list = segmentService.getAllSegments(eventId);
//        return ResponseEntity.ok(list);
//    }
//    @PostMapping("/{eventId}")
//    public ResponseEntity<SessionDTO> createSegment(@PathVariable("eventId") int eventId, @RequestBody SessionDTO sessionDTO) {
//        segmentService.addSegment(eventId, sessionDTO);
//        return ResponseEntity.ok(sessionDTO);
//    }
}



