package hcmute.fit.event_management.controller.manager;

import hcmute.fit.event_management.dto.TeamDTO;
import hcmute.fit.event_management.entity.Task;
import hcmute.fit.event_management.entity.Team;
import hcmute.fit.event_management.repository.TaskRepository;
import hcmute.fit.event_management.service.IEmployeeService;
import hcmute.fit.event_management.service.IEventService;
import hcmute.fit.event_management.service.ITaskService;
import hcmute.fit.event_management.service.ITeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payload.Response;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/man/team")
public class TeamController {
    @Autowired
    private ITeamService teamService;
    @Autowired
    private IEmployeeService employeeService;



    @GetMapping("/{teamId}")
    public ResponseEntity<?> findTeamById(@PathVariable int teamId){
        TeamDTO team = teamService.getDetailTeamById(teamId);
        Response response  = new Response();
        response.setData(team);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<?> addTeam(@RequestBody TeamDTO team){
        Response response  = new Response();
        response.setData(teamService.addTeam(team));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PutMapping("")
    public ResponseEntity<?> updateTeam(@RequestBody TeamDTO team){
        Response response  = new Response();
        response.setData(teamService.updateTeam(team));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/{teamId}/add/{employeeId}")
    public ResponseEntity<?> addEmployee(@PathVariable int teamId, @PathVariable int employeeId){
        Response response  = new Response();
        response.setData(teamService.addMemberToTeam(teamId, employeeId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @DeleteMapping("/{teamId}/del/{employeeId}")
    public ResponseEntity<?> deleteEmployee(@PathVariable int teamId, @PathVariable int employeeId){
        Map<String, Object> response = teamService.deleteMemberFromTeam(teamId, employeeId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/{teamId}/listassigned")
    public ResponseEntity<?> findEmplloyeeToAssignedSubtask(@PathVariable int teamId) {
        Response response = new Response();
        response.setData(employeeService.findEligibleEmployees(teamId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @DeleteMapping("/{teamId}")
    public ResponseEntity<?> deleteTeam(@PathVariable int teamId){
        Response response  = new Response();
        response.setData(teamService.deleteTeam(teamId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/{eventId}/detail")
    public ResponseEntity<?> findEventById(@PathVariable int eventId){
        Response response = new Response();
        response.setData(teamService.getDetailTeamInEvent(eventId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
