package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.SubTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubtaskRepository extends JpaRepository<SubTask, Integer> {
    @Query("select sb from SubTask sb where sb.task.taskId = :taskId")
    List<SubTask> findByTaskId(int taskId);

    @Query("select sb from SubTask sb where sb.task.team.teamId = :teamId")
    List<SubTask> findByTeamId(int teamId);

    @Query("select sb from SubTask sb where sb.task.event.eventID = :eventId")
    List<SubTask> findByEventId(int eventId);

    @Query("select sb from SubTask sb where sb.employee.id = :employeeId")
    List<SubTask> findByEmployeeId(int employeeId);
}
