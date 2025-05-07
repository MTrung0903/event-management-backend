package hcmute.fit.event_management.controller.manager;

import hcmute.fit.event_management.dto.*;
import hcmute.fit.event_management.repository.AssignedRoleRepository;
import hcmute.fit.event_management.service.IEventService;
import hcmute.fit.event_management.service.IRoleAssignmentService;
import hcmute.fit.event_management.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import payload.Response;
import java.util.List;

@RestController
@RequestMapping("/api/role-assignment")
public class RoleAssignmentController {
    @Autowired
    private IRoleAssignmentService roleAssignmentService;

    @PostMapping("/assign-role")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<Response> assignRole(@RequestBody AssignRoleRequestDTO assignRoleRequest) {
        return roleAssignmentService.assignRoleToEvent(assignRoleRequest.getEmail(),assignRoleRequest.getRoleId(),assignRoleRequest.getEventId());
    }

    @GetMapping("/team/{userId}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<List<AssignedRoleDTO>> getRoleAssignInEvent(@PathVariable int userId) {
        List<AssignedRoleDTO> list = roleAssignmentService.getRoleAssignInEvent(userId);

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{userId}/my-assigned-events")
    @PreAuthorize("hasAnyRole('ORGANIZER','ATTENDEE')")
    public ResponseEntity<List<AssignedEventDTO>> getAssignedEvents(@PathVariable int userId) {
        return ResponseEntity.ok(roleAssignmentService.getAssignedEvents(userId));
    }
    @GetMapping("/{eventId}/my-team-events")
    @PreAuthorize("hasAnyRole('ORGANIZER','ATTENDEE')")
    public ResponseEntity<AssignedEventTeamDTO> getTeamInEvent(@PathVariable int eventId) {
        return ResponseEntity.ok(roleAssignmentService.getTeam(eventId));
    }
}
