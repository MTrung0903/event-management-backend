package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    @Query("select t from Ticket t where t.event.eventID = :eventId")
    List<Ticket> findByEventID(@Param("eventId") int eventId);

    List<Ticket> findByEventUserUserId(int userId);
    List<Ticket> findByEventEventID(int eventId);

}
