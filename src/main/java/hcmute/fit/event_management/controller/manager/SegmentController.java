package hcmute.fit.event_management.controller.manager;

import hcmute.fit.event_management.dto.SegmentDTO;
import hcmute.fit.event_management.service.ISegmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/segment")
public class SegmentController {
    @Autowired
    private ISegmentService segmentService;

    @GetMapping("/{eventId}")
    public ResponseEntity<List<SegmentDTO>> getSegmentByEventId(@PathVariable("eventId") int eventId) {
        List<SegmentDTO> list = segmentService.getAllSegments(eventId);
        return ResponseEntity.ok(list);
    }
}
