package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.SponsorShip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SponsorShipRepository extends JpaRepository<SponsorShip, Integer> {
}
