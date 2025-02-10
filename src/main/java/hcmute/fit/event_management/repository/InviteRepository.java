package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.Invite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InviteRepository extends JpaRepository<Invite, Integer> {
    @Query("select i from Invite i where i.event.eventID = :eventId")
    List<Invite> findByEventId(int eventId);

    @Query("select i from Invite i where i.name = :cmp or i.email = :cmp")
    Invite findByNameOrEmail(String cmp);
}
