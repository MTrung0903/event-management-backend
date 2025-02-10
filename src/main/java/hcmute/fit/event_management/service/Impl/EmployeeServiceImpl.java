package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.dto.EmployeeDTO;
import hcmute.fit.event_management.entity.*;
import hcmute.fit.event_management.repository.*;
import hcmute.fit.event_management.service.IEmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements IEmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TeamEmployeeRepository teamEmployeeRepository;
    @Autowired
    private SubtaskRepository subtaskRepository;

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private TaskRepository taskRepository;


    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }



    @Override
    public List<EmployeeDTO> getEmployeeByTeamId(int teamId) {
        List<TeamEmployee> teamEmployees = teamEmployeeRepository.findByTeamId(teamId);
        List<EmployeeDTO> employeeDTOs = new ArrayList<EmployeeDTO>();
        for (TeamEmployee teamEmployee : teamEmployees) {
            Employee employee = employeeRepository.findById(teamEmployee.getEmployee().getId()).get();
            EmployeeDTO employeeDTO = new EmployeeDTO();
            BeanUtils.copyProperties(employee, employeeDTO);
            employeeDTOs.add(employeeDTO);
        }
        return employeeDTOs;
    }


    @Override
    public List<EmployeeDTO> getEmployeesJoinTeam(int eventId) {
        Set<Integer> listEmployeeIds = new HashSet<>();
        List<TeamEmployee> list = teamEmployeeRepository.findAll();
        for (TeamEmployee teamEmployee : list) {
            int eventTmp = teamRepository.findById(teamEmployee.getTeam().getTeamId()).get().getEvent().getEventID();
            if(eventTmp == eventId) {
                listEmployeeIds.add(teamEmployee.getEmployee().getId());
            }
        }
        List<Employee> employees = employeeRepository.findAll();
        List<EmployeeDTO> employeeDTOs = new ArrayList<>();
        for (Employee employee : employees) {
            if(!listEmployeeIds.contains(employee.getId())) {
                EmployeeDTO employeeDTO = new EmployeeDTO();
                BeanUtils.copyProperties(employee, employeeDTO);
                employeeDTOs.add(employeeDTO);
            }
        }
        return employeeDTOs;
    }
//    public List<EmployeeDTO> getEmployeesJoinTeam(int eventId) {
//        // Lấy danh sách employeeId tham gia eventId
//        Set<Integer> employeeIdsInEvent = teamEmployeeRepository.findByEventId(eventId).stream()
//                .map(teamEmployee -> teamEmployee.)
//                .collect(Collectors.toSet());
//
//        // Lấy danh sách employeeId tham gia team ở các event khác
//        Set<Integer> employeeIdsInOtherEvents = teamEmployeeRepository.findByNotEventId(eventId).stream()
//                .map(teamEmployee -> teamEmployee.getEmployee().getId())
//                .collect(Collectors.toSet());
//
//        // Kết hợp danh sách các nhân viên chưa tham gia team hoặc ở sự kiện khác
//        Set<Integer> excludedIds = new HashSet<>(employeeIdsInEvent);
//        excludedIds.addAll(employeeIdsInOtherEvents);
//
//        // Lấy danh sách nhân viên không nằm trong excludedIds
//        return employeeRepository.findAll().stream()
//                .filter(employee -> !excludedIds.contains(employee.getId()))
//                .map(employee -> {
//                    EmployeeDTO employeeDTO = new EmployeeDTO();
//                    BeanUtils.copyProperties(employee, employeeDTO);
//                    return employeeDTO;
//                })
//                .collect(Collectors.toList());
//    }

    @Override
    public List<EmployeeDTO> getEmployeeToAssignedSubTask(){
        List<Employee> list = employeeRepository.findAll();
        List<SubTask>  subTaskList =  subtaskRepository.findAll();
        Set<Integer> listEmployeeIds = new HashSet<>();
        for(SubTask subTask : subTaskList) {
            listEmployeeIds.add(subTask.getEmployee().getId());
        }
        List<EmployeeDTO> employeeDTOs = new ArrayList<>();
        for(Employee employee : list) {
            if(!listEmployeeIds.contains(employee.getId())) {
                EmployeeDTO employeeDTO = new EmployeeDTO();
                BeanUtils.copyProperties(employee, employeeDTO);
                employeeDTOs.add(employeeDTO);
            }
        }
        return employeeDTOs;
    }
    @Override
    public List<EmployeeDTO> findEligibleEmployees(int teamId) {
        List<Employee> list = employeeRepository.findEmployeesWithoutSubtasksInTeam(teamId);
        List<EmployeeDTO> employeeDTOs = new ArrayList<>();
        for (Employee employee : list) {
            EmployeeDTO employeeDTO = new EmployeeDTO();
            BeanUtils.copyProperties(employee, employeeDTO);
            employeeDTOs.add(employeeDTO);
        }
        return employeeDTOs;
    }




}
