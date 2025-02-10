package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    @Query("select e from Event e where e.manager.manID = :id")
    List<Event> findByManagerId(int id);

    @Query("select e from Event e where e.mc.mcID = :id")
    List<Event> findByMcId(int id);

    @Query("select e from Event e where e.eventLocation = :location")
    List<Event> findByLocation(String location);

    @Query("select e from Event e Join Task t on t.event.eventID = e.eventID Join SubTask st on st.task.taskId = t.taskId where st.employee.id = :id")
    List<Event> findByEmpId(int id);
}
