package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.ProviderService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderServiceRepository extends JpaRepository<ProviderService, Integer> {

    @Query("select p from ProviderService p where p.provider.id = :id")
    List<ProviderService> findByProviderId(int id);
    @Query("SELECT ps \n" +
            "FROM ProviderService ps\n" +
            "JOIN ServiceEvent se ON ps.id = se.service.id\n" +
            "JOIN ProviderEvent pe ON se.event.eventID = pe.event.eventID\n" +
            "WHERE ps.provider.id = pe.provider.id\n" +
            "  AND pe.event.eventID = :eventId\n" +
            "  AND ps.provider.id = :providerId\n")
    List<ProviderService> getServiceInEvent(@Param("providerId")int providerId, @Param("eventId") int eventId);

    @Query("SELECT ps " +
            "FROM ProviderService ps " +
            "LEFT JOIN ProviderEvent pe ON ps.provider.id = pe.provider.id AND pe.event.eventID = :eventId " +
            "WHERE ps.provider.id = :providerId " +
            "AND ( " +
            "    (pe.event.eventID IS NOT NULL AND ps.id NOT IN ( " +
            "        SELECT se.service.id " +
            "        FROM ServiceEvent se " +
            "        WHERE se.event.eventID = :eventId " +
            "    )) " +
            "    OR " +
            "    (pe.event.eventID IS NULL) " +
            ")")
    List<ProviderService> getServicesNotInEvent(
            @Param("providerId") int providerId,
            @Param("eventId") int eventId
    );

}
