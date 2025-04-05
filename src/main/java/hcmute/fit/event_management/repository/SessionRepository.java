package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {
    @Query("select s from Session s where s.event.eventID = :eventId")
    List<Session> findByEventId(@Param("eventId") int eventId);
}
