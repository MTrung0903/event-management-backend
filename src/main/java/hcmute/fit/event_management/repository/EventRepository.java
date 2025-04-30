package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.Event;
import hcmute.fit.event_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

    List<Event> findByEventNameContainingIgnoreCase(String eventName);
    List<Event> findByEventStart(LocalDateTime eventStart);
    List<Event> findByEventHostContainingIgnoreCase(String eventHost);
    List<Event> findByTagsContainingIgnoreCase(String tag);
    List<Event> findByEventTypeContainingIgnoreCase(String eventType);
    List<Event> findByEventLocationCityContainingIgnoreCase(String city);
    List<Event> findByEventLocationVenueNameContainingIgnoreCase(String venueName);
    List<Event> findByUser(User user);
    List<Event> findByEventHost(String eventHost);

}
