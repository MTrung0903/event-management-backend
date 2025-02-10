package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.TeamEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamEmployeeRepository extends JpaRepository<TeamEmployee, Integer> {
    @Query("select p from TeamEmployee p where p.team.teamId = :teamId")
    public List<TeamEmployee> findByTeamId(int teamId);

    @Query("select p from TeamEmployee p where p.employee.id = :employeeId")
    public List<TeamEmployee> findByEmployee(int employeeId);

    @Query("select p from  TeamEmployee p where p.employee.id = :employeeId and p.team.teamId = :teamId")
    public TeamEmployee findByTeamIdAndEmployee(int teamId, int employeeId);
}
