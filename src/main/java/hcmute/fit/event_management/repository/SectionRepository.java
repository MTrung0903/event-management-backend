package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Integer> {
    @Query("select s from Section s where s.event.eventID = :eventId")
    List<Section> findByEventId(@Param("eventId") int eventId);
}
