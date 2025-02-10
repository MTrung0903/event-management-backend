package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.Attendee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendeeRepository extends JpaRepository<Attendee, Integer> {
    @Query(value = "SELECT a FROM Attendee a  WHERE a.invite.event.eventID = :eventId")
    List<Attendee> findAttendeesByEventId(@Param("eventId") int eventId);
}
