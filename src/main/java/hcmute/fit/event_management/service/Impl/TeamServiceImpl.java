package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.dto.*;
import hcmute.fit.event_management.entity.*;
import hcmute.fit.event_management.entity.keys.TeamEmployeeId;
import hcmute.fit.event_management.repository.*;
import hcmute.fit.event_management.service.IEmployeeService;
import hcmute.fit.event_management.service.ITeamService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeamServiceImpl implements ITeamService {
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TeamEmployeeRepository teamEmployeeRepository;

    @Autowired
    private IEmployeeService employeeService;
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private SubtaskRepository subtaskRepository;

    public TeamServiceImpl(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public List<TeamDTO> getTeamsOfEvent(int eventId) {
        List<Team> teams = teamRepository.findByEventId(eventId);
        List<TeamDTO> teamDTOs = new ArrayList<>();

        for (Team team : teams) {
            TeamDTO teamDTO = new TeamDTO();
            teamDTO.setTeamId(team.getTeamId());
            teamDTO.setTeamName(team.getTeamName());
            List<Task> tasks = team.getListTasks();
            List<TaskDTO> taskDTOs = new ArrayList<>();
            for (Task task : tasks) {
                TaskDTO taskDTO = new TaskDTO();
                taskDTO.setTaskId(task.getTaskId());
                taskDTO.setTaskName(task.getTaskName());
                taskDTO.setTaskStatus(task.getTaskStatus());
                taskDTO.setTaskDesc(task.getTaskDesc());
                taskDTO.setTaskDl(task.getTaskDl().toString());
                taskDTOs.add(taskDTO);
            }
            teamDTO.setListTasks(taskDTOs);
            teamDTOs.add(teamDTO);
        }
        return teamDTOs;
    }
    @Override
    public TeamDTO getDetailTeamById(int teamId) {
        Optional<Team> cpm = teamRepository.findById(teamId);
        TeamDTO teamDTO = new TeamDTO();
        if(cpm.isPresent()) {
            Team team = cpm.get();
            teamDTO.setTeamId(team.getTeamId());
            teamDTO.setTeamName(team.getTeamName());
            teamDTO.setEventId(team.getEvent().getEventID());
            //List<TeamEmployee> teamEmployees = teamEmployeeRepository.findByTeamId(team.getTeamId());
            List<EmployeeDTO> listEmployee = employeeService.getEmployeeByTeamId(team.getTeamId());
            teamDTO.setListEmployees(listEmployee);

        }
        return teamDTO;
    }
    @Override
    public List<TeamDTO> getAllTeamsByEventId(int eventId) {
        List<Team> teams = teamRepository.findByEventId(eventId);
        List<TeamDTO> teamDTOs = new ArrayList<>();
        for (Team team : teams) {
            TeamDTO teamDTO = new TeamDTO();
            teamDTO.setTeamId(team.getTeamId());
            teamDTO.setTeamName(team.getTeamName());
            teamDTO.setEventId(team.getEvent().getEventID());
            teamDTO.setListEmployees(employeeService.getEmployeeByTeamId(team.getTeamId()));
            teamDTOs.add(teamDTO);
        }

        return teamDTOs;
    }
    @Override
    public boolean addTeam(TeamDTO teamDTO){
        boolean isSuccess = false;
        try{
            Team team = new Team();
            team.setTeamName(teamDTO.getTeamName());
            team.setEvent(eventRepository.findById(teamDTO.getEventId()).get());
            teamRepository.save(team);
            isSuccess = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return isSuccess;
    }
    @Override
    public boolean updateTeam(TeamDTO teamDTO){
        boolean isSuccess = false;
        try{
           if(teamRepository.findById(teamDTO.getTeamId()).isPresent()) {
               Team team = teamRepository.findById(teamDTO.getTeamId()).get();
               team.setTeamName(teamDTO.getTeamName());
               team.setEvent(eventRepository.findById(teamDTO.getEventId()).get());
               teamRepository.save(team);
               isSuccess = true;
           }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return isSuccess;
    }
    @Override
    public boolean deleteTeam(int teamId){
        boolean isSuccess = false;
        if(teamRepository.findById(teamId).isPresent()) {
            teamRepository.delete(teamRepository.findById(teamId).get());
            isSuccess = true;
        }
        return isSuccess;
    }
    @Override
    public boolean addMemberToTeam(int teamId, int employeeId){
        boolean isSuccess = false;
        try{
            if(teamRepository.findById(teamId).isPresent() && employeeRepository.findById(employeeId).isPresent()) {
                TeamEmployee teamEmployee = new TeamEmployee();
                TeamEmployeeId teamEmployeeId = new TeamEmployeeId(employeeId, teamId);
                teamEmployee.setId(teamEmployeeId);
                teamEmployee.setEmployee(employeeRepository.findById(employeeId).get());
                teamEmployee.setTeam(teamRepository.findById(teamId).get());
                teamEmployeeRepository.save(teamEmployee);
                isSuccess = true;
            }else{
                System.out.println("Add member to team failed");
            }
        } catch (Exception e) {
            System.out.println("Add member to team failed" + e.getMessage());
        }
        return isSuccess;
    }

    @Override
    public Map<String, Object> deleteMemberFromTeam(int teamId, int employeeId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (teamRepository.findById(teamId).isPresent() && employeeRepository.findById(employeeId).isPresent()) {
                TeamEmployee teamEmployee = teamEmployeeRepository.findByTeamIdAndEmployee(teamId, employeeId);
                List<SubTask> list = subtaskRepository.findByEmployeeId(employeeId);
                if (list == null || list.isEmpty()) {
                    teamEmployeeRepository.delete(teamEmployee);
                    response.put("status", "success");
                    response.put("message", "Employee deleted successfully!");
                } else {
                    response.put("status", "failed");
                    response.put("message", "Cannot delete employee while they are assigned to subtasks.");
                }
            } else {
                response.put("status", "failed");
                response.put("message", "Team or Employee not found.");
            }
        } catch (Exception e) {
            System.out.println("Delete member from team failed: " + e.getMessage());
            response.put("status", "error");
            response.put("message", "An error occurred while deleting the member.");
        }
        return response;
    }

    @Override
    public List<TeamDTO> getListTeamToAssignedTask(int taskId, int eventId) {
        try {
            Task taskAssigned = taskRepository.findById(taskId).get();
            List<Team> teams = teamRepository.findByEventId(eventId);
            List<Team> assignedTeams = new ArrayList<>();

            for (Team team : teams) {
                List<Task> tasks = taskRepository.findByTeamId(team.getTeamId());
                boolean canAssign = true;

                for (Task task : tasks) {
                    // Kiểm tra nếu deadline nằm trong khoảng [startDate, endDate]
                    if (!task.getTaskDl().before(taskAssigned.getCreateDate()) && !task.getTaskDl().after(taskAssigned.getTaskDl())) {
                        canAssign = false;
                        break;
                    }
                }

                if (canAssign) {
                    assignedTeams.add(team);
                }
            }
            List<TeamDTO> list = new ArrayList<>();
            for (Team team : assignedTeams) {
                TeamDTO teamDTO = new TeamDTO();
                BeanUtils.copyProperties(team, teamDTO);
                teamDTO.setEventId(eventId);

                list.add(teamDTO);
            }
            return list;

        } catch (Exception e) {
            throw new RuntimeException("Error while assigning teams to task", e);
        }
    }

    @Override
    public List<TeamDTO> getDetailTeamInEvent(int eventId) {
        List<Team> teams = teamRepository.findByEventIdWithEmployees(eventId);
        List<TeamDTO> teamDTOs = new ArrayList<>();

        for (Team team : teams) {
            TeamDTO teamDTO = new TeamDTO();
            BeanUtils.copyProperties(team, teamDTO);
            teamDTO.setEventId(team.getEvent().getEventID());

            // Map danh sách Employee
            List<EmployeeDTO> employeeDTOs = team.getListTeamEmployees()
                    .stream()
                    .map(te -> {
                        EmployeeDTO employeeDTO = new EmployeeDTO();
                        BeanUtils.copyProperties(te.getEmployee(), employeeDTO);

                        return employeeDTO;
                    }).collect(Collectors.toList());

            // Map danh sách Task
            List<TaskDTO> taskDTOs = team.getListTasks()
                    .stream()
                    .map(task -> {
                        TaskDTO taskDTO = new TaskDTO();
                        BeanUtils.copyProperties(task, taskDTO);
                        taskDTO.setTaskDl(task.getTaskDl().toString());
                        taskDTO.setEventId(task.getEvent().getEventID());
                        taskDTO.setTeamId(task.getTeam().getTeamId());
                        taskDTO.setTeamName(task.getTeam().getTeamName());

                        // Map danh sách SubTask
                        List<SubTaskDTO> subTaskDTOs = task.getListSubTasks()
                                .stream()
                                .map(subTask -> {
                                    SubTaskDTO subTaskDTO = new SubTaskDTO();
                                    BeanUtils.copyProperties(subTask, subTaskDTO);
                                    subTaskDTO.setSubTaskDeadline(subTask.getSubTaskDeadline().toString());
                                    subTaskDTO.setSubTaskStart(subTask.getCreateDate().toString());
                                    subTaskDTO.setEmployeeId(subTask.getEmployee().getId());
                                    subTaskDTO.setTaskId(subTask.getTask().getTaskId());
                                    return subTaskDTO;
                                }).collect(Collectors.toList());

                        taskDTO.setListSubTasks(subTaskDTOs);
                        return taskDTO;
                    }).collect(Collectors.toList());

            teamDTO.setListEmployees(employeeDTOs);
            teamDTO.setListTasks(taskDTOs);
            teamDTOs.add(teamDTO);
        }
        return teamDTOs;
    }


}
