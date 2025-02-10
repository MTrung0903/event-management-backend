package hcmute.fit.event_management.controller.manager;

import hcmute.fit.event_management.dto.*;
import hcmute.fit.event_management.repository.ProviderRepository;
import hcmute.fit.event_management.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import payload.Response;

import java.util.List;

@RestController
@RequestMapping("man/event")
public class EventController {

    @Autowired
    private IInviteService inviteService;



    @Autowired
    private IAttendeeService attendeeService;

    @Autowired
    private IProvider providerImpl;

    @Autowired
    private ISponsorService sponsorService;

    @Autowired
    private IEventService eventService;

    @Autowired
    private ISectionService sectionService;

    @Autowired
    private ITeamService teamService;

    @Autowired
    private ITaskService taskService;

    @Autowired
    private ISubtaskService subtaskService;

    @Autowired
    private IEmployeeService employeeService;

    @Autowired
    private IProviderService providerService;

    @Autowired
    private IServiceEventSerivce serviceEventSerivce;


    @PostMapping("")
    public ResponseEntity<?> addEvent(@RequestParam("image") MultipartFile image,
                                      @ModelAttribute EventDTO event){
        System.out.println("EventDTO received: " + event);
        Response response = new Response();
        response.setData(eventService.addEvent(image,event));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("")
    public ResponseEntity<?> updateEvent(@RequestParam("image") MultipartFile image,
                                         @ModelAttribute EventDTO event){
        Response response = new Response();
        response.setData(eventService.updateEvent(image,event));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable int eventId){
        Response response = new Response();
        response.setData(eventService.deleteEvent(eventId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PutMapping("/{eventId}/mc/{mcId}")
    public ResponseEntity<?> addMcEvent(@PathVariable Integer eventId, @PathVariable Integer mcId){
        Response response = new Response();
        response.setData(eventService.addMc(eventId,mcId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("")
    public ResponseEntity<?> getAllEvent(){
        List<EventDTO> events = eventService.getAllEvents();
        Response response = new Response();
        response.setData(events);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEventById(@PathVariable("eventId") int eventId){
        EventDTO event = eventService.getEventById(eventId);
        Response response = new Response();
        response.setData(event);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/{eventId}/providers/{providerId}")
    public ResponseEntity<?> addProviderForEvent(@PathVariable int eventId, @PathVariable int providerId) {
        Response response = new Response();
        response.setData(providerImpl.addProviderForEvent(providerId,eventId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{eventId}/providers")
    public ResponseEntity<?> getProvidersByEventId(@PathVariable int eventId){
        Response response = new Response();
        response.setData(providerImpl.listProviderInEvent(eventId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/{eventId}/listprovider")
    public ResponseEntity<?> getListProvidersForAdd(@PathVariable int eventId){
        Response response = new Response();
        response.setData(providerImpl.listProviderForAdd(eventId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{eventId}/sponsors/{sponsorId}")
    public ResponseEntity<?> addSponsorForEvent(@PathVariable int eventId,@PathVariable int sponsorId) {
        Response response = new Response();
        response.setData(sponsorService.addSponsorForEvent(sponsorId,eventId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/{eventId}/sponsors")
    public ResponseEntity<?> getAllSponsorByEventId(@PathVariable int eventId){
        Response response = new Response();
        response.setData(sponsorService.getAllSponsorByEventId(eventId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    //Lấy danh sách các nhà tài trợ chưa tài trợ cho sự kiện
    @GetMapping("/{eventId}/listponsor")
    public ResponseEntity<?> getNewSponsorsForEvent(@PathVariable int eventId){
        Response response = new Response();
        response.setData(sponsorService.getSponsorForAddNew(eventId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    //Thêm mới 1 sponsor chưa có trong db cho event
    @PostMapping("/{eventId}/add-new-sponsor")
    public ResponseEntity<?> addNewSponsorForEvent(@PathVariable int eventId,@RequestParam("image") MultipartFile image,
                                                   @ModelAttribute SponsorDTO sponsorDTO){
        Response response = new Response();
        response.setData(sponsorService.addNewSponsorForEvent(eventId,image,sponsorDTO));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
//    @GetMapping("/{eventId}/attendee")
//    public ResponseEntity<?> getListAttendees(@PathVariable("eventId") int eventId) {
//        List<AttendeeDTO> listAttendee = attendeeService.getListAttendeeByEventId(eventId);
//        Response response = new Response();
//        response.setData(listAttendee);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
    @GetMapping("/{eventId}/attendee")
    public ResponseEntity<?> getListAttendees(@PathVariable("eventId") int eventId) {
        String listAttendee = eventService.getListAttendeeByEventId(eventId);
        Response response = new Response();
        response.setData(listAttendee);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PutMapping("/{eventId}/attendee")
    public ResponseEntity<?> uploadAttendee(@PathVariable("eventId") int eventId, @RequestParam("file") MultipartFile file) {
        String listAttendee = eventService.addAttendee(eventId,file);
        Response response = new Response();
        response.setData(listAttendee);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{eventId}/list-invite")
    public ResponseEntity<?> getListInviteByEventId(@PathVariable int eventId) {
        List<InviteDTO> listInvites= inviteService.getListInvitesByEventId(eventId);
        Response response = new Response();
        response.setData(listInvites);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/{eventId}/section")
    public ResponseEntity<?> getSectionOfEvent(@PathVariable int eventId) {
        List<SectionDTO> listSection = sectionService.getSectionOfEvent(eventId);
        Response response = new Response();
        response.setData(listSection);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/{eventId}/team")
    public ResponseEntity<?> getTeamsOfEvent(@PathVariable  int eventId){
        List<TeamDTO> teams = teamService.getTeamsOfEvent(eventId);
        Response response  = new Response();
        response.setData(teams);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/{eventId}/team-member")
    public ResponseEntity<?> findEmployeeByTeamId(@PathVariable int eventId){
        Response response  = new Response();
        response.setData(teamService.getAllTeamsByEventId(eventId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/{eventId}/tasks")
    public ResponseEntity<?> getTaskOfEvent(@PathVariable int eventId){
        List<TaskDTO> listTask = taskService.getTasksOfEvent(eventId);
        Response response = new Response();
        response.setData(listTask);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("{eventId}/teams/{taskId}")
    public ResponseEntity<?> assignedTeamForTask(@PathVariable int taskId, @PathVariable int eventId){

        Response response  = new Response();
        response.setData(teamService.getListTeamToAssignedTask(taskId, eventId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{eventId}/subtasks")
    public ResponseEntity<?> getSubtasks(@PathVariable int eventId) {
        Response response = new Response();
        response.setData(subtaskService.listSubtaskFromEvent(eventId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @DeleteMapping("/{eventId}/del-provider/{providerId}")
    public ResponseEntity<?> delProviderEvent(@PathVariable int eventId, @PathVariable int providerId){
        Response response = new Response();
        response.setData(providerImpl.delProviderEvent(eventId, providerId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @DeleteMapping("/{eventId}/del-sponsor/{sponsorId}")
    public ResponseEntity<?> delSponsorEvent(@PathVariable int eventId, @PathVariable int sponsorId){
        Response response = new Response();
        response.setData(sponsorService.deleteSponsorEvent(eventId, sponsorId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{eventId}/add-ser/{serviceId}")
    public ResponseEntity<?> addServiceToEvent(@PathVariable int eventId, @PathVariable int serviceId) {
        Response response = new Response();
        response.setData(serviceEventSerivce.addServiceEvent(eventId, serviceId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @DeleteMapping("/{eventId}/del-ser/{serviceId}")
    public ResponseEntity<?> delServiceFromEvent(@PathVariable int eventId, @PathVariable int serviceId) {
        Response response = new Response();
        response.setData(serviceEventSerivce.delServiceEvent(eventId, serviceId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{eventId}/detail-ser/{providerId}")
    public ResponseEntity<?> getDetailSerEvent(@PathVariable int eventId, @PathVariable int providerId) {
        Response response = new Response();
        response.setData(providerImpl.providerDetailInEvent(eventId, providerId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
