package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    @Query("""
    SELECT DISTINCT e 
    FROM Employee e
    JOIN e.listTeamEmployees te
    JOIN te.team t
    JOIN t.event ev
    LEFT JOIN e.listSubTasks st
    WHERE ev.eventID = :eventId
      AND (st IS NULL OR st NOT IN (
          SELECT s 
          FROM SubTask s 
          WHERE s.employee = e
      ))
""")
    List<Employee> findEligibleEmployees(@Param("eventId") int eventId);

    @Query("""
    SELECT DISTINCT e
    FROM Employee e
    JOIN e.listTeamEmployees te
    JOIN te.team t
    LEFT JOIN e.listSubTasks st
    WHERE t.teamId = :teamId
      AND st IS NULL
""")
    List<Employee> findEmployeesWithoutSubtasksInTeam(@Param("teamId") int teamId);



}
