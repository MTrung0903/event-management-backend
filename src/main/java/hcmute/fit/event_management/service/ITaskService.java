package hcmute.fit.event_management.service;

import hcmute.fit.event_management.dto.TaskDTO;
import hcmute.fit.event_management.entity.Task;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ITaskService {

    List<TaskDTO> getTasksOfEvent(int eventId);

    TaskDTO findTaskById(int subTaskId);

    boolean addTask(TaskDTO taskDTO);

    boolean updateTask(TaskDTO taskDTO);

    Map<String, Object> deleteTask(int taskId);


    boolean addTeamForTask(int taskId, int teamId);

    List<TaskDTO> getTasksNoTeam(int eventId);
}
