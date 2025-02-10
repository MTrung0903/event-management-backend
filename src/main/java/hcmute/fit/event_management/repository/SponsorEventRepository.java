package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.SponsorEvent;
import hcmute.fit.event_management.entity.keys.SponsorEventId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SponsorEventRepository extends JpaRepository<SponsorEvent, SponsorEventId> {
    @Query("select sp from SponsorEvent sp where sp.event.eventID = :eventId")
    List<SponsorEvent> findByEventId(@Param("eventId") int eventId);

    @Query("select sp from  SponsorEvent sp where sp.event.eventID = :id and  sp.sponsor.id = :sponsorId")
    SponsorEvent findSponsorEvent(@Param("id") int id, @Param("sponsorId") int sponsorId);
}
