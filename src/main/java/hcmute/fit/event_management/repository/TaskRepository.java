package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    @Query("select t from Task t where t.event.eventID = :eventId")
    List<Task> findByEventId(@Param("eventId") int eventId);

    @Query("select t from Task t where t.team.teamId = :teamId")
    List<Task> findByTeamId(@Param("teamId") int teamId);

}
