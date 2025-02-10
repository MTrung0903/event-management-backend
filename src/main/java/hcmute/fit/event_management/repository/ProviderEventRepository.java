package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.ProviderEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderEventRepository extends JpaRepository<ProviderEvent, Integer> {
    @Query("select pv from ProviderEvent pv where pv.event.eventID = :eventId")
    List<ProviderEvent> findByEventId(@Param("eventId") int eventId);

    @Query("select pv from ProviderEvent pv where pv.provider.id = :providerId")
    List<ProviderEvent> findByProviderId(@Param("providerId") int providerId);

    @Query("select pv from ProviderEvent pv where pv.provider.id = :providerId and pv.event.eventID = :eventId" )
    ProviderEvent providerEvent(@Param("providerId") int providerId, @Param("eventId") int eventId);

}
