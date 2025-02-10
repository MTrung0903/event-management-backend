package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.dto.SubTaskDTO;
import hcmute.fit.event_management.entity.SubTask;
import hcmute.fit.event_management.entity.Task;
import hcmute.fit.event_management.repository.EmployeeRepository;
import hcmute.fit.event_management.repository.SubtaskRepository;
import hcmute.fit.event_management.repository.TaskRepository;
import hcmute.fit.event_management.service.ISubtaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SubtaskServiceImpl implements ISubtaskService {
    @Autowired
    private SubtaskRepository subtaskRepository;

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    public SubtaskServiceImpl(SubtaskRepository subtaskRepository) {
        this.subtaskRepository = subtaskRepository;
    }

    @Override
    public List<SubTaskDTO> getAllSubtasksOfTask(int taskId) {
        List<SubTask> subtasks = subtaskRepository.findByTaskId(taskId);
        List<SubTaskDTO> subtaskDTOs = new ArrayList<>();
        for (SubTask subtask : subtasks) {
            SubTaskDTO subtaskDTO = new SubTaskDTO();
            BeanUtils.copyProperties(subtask, subtaskDTO);
            subtaskDTO.setEmployeeId(subtask.getEmployee().getId());
            subtaskDTO.setSubTaskDeadline(subtask.getSubTaskDeadline().toString());
            subtaskDTO.setSubTaskStart(subtask.getCreateDate().toString());
            subtaskDTOs.add(subtaskDTO);
        }
        return subtaskDTOs;
    }
    @Override
    public List<SubTaskDTO> listSubtaskFromEvent(int eventId){
        List<Task> taskOfEvent = taskRepository.findByEventId(eventId);
        Set<Integer> taskIds = new HashSet<>();
        for (Task task : taskOfEvent) {
            taskIds.add(task.getTaskId());
        }
        List<SubTask> subtasks = subtaskRepository.findAll();
        List<SubTaskDTO> subtaskDTOs = new ArrayList<>();
        for (SubTask subtask : subtasks) {
            if(taskIds.contains(subtask.getTask().getTaskId())){
                SubTaskDTO subtaskDTO = new SubTaskDTO();
                BeanUtils.copyProperties(subtask, subtaskDTO);
                subtaskDTO.setTaskId(subtask.getTask().getTaskId());
                subtaskDTO.setEmployeeId(subtask.getEmployee().getId());
                subtaskDTO.setSubTaskDeadline(subtask.getSubTaskDeadline().toString());
                subtaskDTO.setSubTaskStart(subtask.getCreateDate().toString());
                subtaskDTOs.add(subtaskDTO);
            }

        }
        return subtaskDTOs;
    }
    @Override
    public SubTaskDTO getSubtaskById(int subtaskId) {
        Optional<SubTask> subtask = subtaskRepository.findById(subtaskId);
        SubTaskDTO subtaskDTO = new SubTaskDTO();
        if(subtask.isPresent()) {
            BeanUtils.copyProperties(subtask.get(), subtaskDTO);
            subtaskDTO.setEmployeeId(subtask.get().getEmployee().getId());
        }
        return subtaskDTO;
    }
    @Override
    public boolean addSubtask(int taskId, SubTaskDTO subtaskDTO) {
        boolean isSuccess = false;
        try {
            if(taskRepository.findById(taskId).isPresent() && employeeRepository.findById(subtaskDTO.getEmployeeId()).isPresent()) {
                SubTask subtask = new SubTask();
                subtask.setSubTaskName(subtaskDTO.getSubTaskName());
                subtask.setSubTaskDesc(subtaskDTO.getSubTaskDesc());
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String nowAsString = formatter.format(new Date());
                Date startDate = formatter.parse(nowAsString);
                subtask.setCreateDate(startDate);
                Date date = formatter.parse(subtaskDTO.getSubTaskDeadline().trim());
                subtask.setSubTaskDeadline(date);
                subtask.setStatus(subtaskDTO.getStatus());
                subtask.setEmployee(employeeRepository.findById(subtaskDTO.getEmployeeId()).get());
                subtask.setTask(taskRepository.findById(taskId).get());
                subtaskRepository.save(subtask);
                isSuccess = true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return isSuccess;
    }

    @Override
    public boolean updateSubtask(int taskId, SubTaskDTO subtaskDTO) {
        boolean isSuccess = false;
        try {
            if(subtaskRepository.findById(subtaskDTO.getSubTaskId()).isPresent()) {
                if(taskRepository.findById(taskId).isPresent() && employeeRepository.findById(subtaskDTO.getEmployeeId()).isPresent()) {
                    SubTask subtask = subtaskRepository.findById(subtaskDTO.getSubTaskId()).get();
                    subtask.setSubTaskName(subtaskDTO.getSubTaskName());
                    subtask.setSubTaskDesc(subtaskDTO.getSubTaskDesc());
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = formatter.parse(subtaskDTO.getSubTaskDeadline().trim());
                    subtask.setSubTaskDeadline(date);
                    subtask.setStatus(subtaskDTO.getStatus());
                    subtask.setEmployee(employeeRepository.findById(subtaskDTO.getEmployeeId()).get());
                    subtask.setTask(taskRepository.findById(taskId).get());
                    subtaskRepository.save(subtask);
                    isSuccess = true;
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return isSuccess;
    }

    @Override
    public boolean deleteSubtask(int subtaskId) {
        boolean isSuccess = false;
        try {
            if(subtaskRepository.findById(subtaskId).isPresent()) {
                subtaskRepository.deleteById(subtaskId);
                isSuccess = true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return isSuccess;
    }
    @Override
    public boolean actionSubtask(int employeeId, int subtaskId, String action) {
        boolean result = false;
        try {
            if(!employeeRepository.existsById(employeeId)) {
                throw new Exception("employee does not exist");
            } else if (!subtaskRepository.existsById(subtaskId)) {
                throw new Exception("subtask does not exist");
            }else if(action.equals("join")){
                    SubTask subtask = subtaskRepository.findById(subtaskId).get();
                    subtask.setEmployee(employeeRepository.findById(employeeId).get());
                    subtaskRepository.save(subtask);
            }else if(action.equals("leave")){
                SubTask subtask = subtaskRepository.findById(subtaskId).get();
                subtask.setEmployee(null);
                subtaskRepository.save(subtask);
            }
        } catch (Exception e) {
            System.out.println("join subtask failed" + e.getMessage());
        }
        return result;
    }

    @Override
    public boolean assignedSubtask(int employeeId, int subtaskId) {
        boolean result = false;
        try{
            SubTask subtask = subtaskRepository.findById(subtaskId).get();
            subtask.setEmployee(employeeRepository.findById(employeeId).get());
            subtaskRepository.save(subtask);
            result = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
    @Override
    public boolean assignedUpdate(int subtaskId, int employeeId) {
        boolean result = false;
        try
        {
            SubTask subtask = subtaskRepository.findById(subtaskId).get();
            subtask.setEmployee(employeeRepository.findById(employeeId).get());
            subtaskRepository.save(subtask);
            result = true;
        } catch (Exception e) {

            System.out.println(e.getMessage());
        }
        return result;
    }

    @Override
    public boolean changeStatus(int subtaskId, String status){
        boolean result = false;
        try {
            SubTask subtask = subtaskRepository.findById(subtaskId).get();
            subtask.setStatus(status);
            subtaskRepository.save(subtask);
            result = true;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
    @Override
    public boolean changeEmployeeAssigned(int subtaskId, int employeeId){
        boolean result = false;
        try {
            SubTask subTask = subtaskRepository.findById(subtaskId).get();
            subTask.setEmployee(employeeRepository.findById(employeeId).get());
            result = true;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

}