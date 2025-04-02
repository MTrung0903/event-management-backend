package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findByEventNameContainingIgnoreCase(String eventName);

    List<Event> findByEventStart(String eventStart);

    List<Event> findByEventHost(String eventHost);

    List<Event> findByEventLocationContainingIgnoreCase(String eventLocation);

    List<Event> findByTagsContainingIgnoreCase(String tag);

    List<Event> findByEventType(String eventType);
}
