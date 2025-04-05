package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.SpeakerEvent;
import hcmute.fit.event_management.entity.SponsorEvent;
import hcmute.fit.event_management.entity.keys.SpeakerEventId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpeakerEventRepository extends JpaRepository<SpeakerEvent, SpeakerEventId> {
    @Query("select sp from SpeakerEvent sp where sp.event.eventID = :eventId")
    List<SpeakerEvent> findByEventId(@Param("eventId") int eventId);
}
