package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.ServiceEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceEventRepository extends JpaRepository<ServiceEvent, Integer> {
    @Query("select p from ServiceEvent p where p.event.eventID = :eventId")
    List<ServiceEvent> findByEventId(@Param("eventId") int eventId);

    @Query("select p from ServiceEvent p where p.service.id = :serviceId")
    List<ServiceEvent> findByServiceId(@Param("serviceId") int serviceId);

    @Query("select p from ServiceEvent p where p.event.eventID = :eventId and p.service.id = :serviceId")
    ServiceEvent serviceEvent(@Param("eventId") int eventId, @Param("serviceId") int serviceId);
}
