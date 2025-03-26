package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.Sponsor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SponsorRepository extends JpaRepository<Sponsor, Integer> {


    @Query("select s from Sponsor s where s.sponsorship.sponsorShipID = :id")
    List<Sponsor> findSponsorsBySponsorshipId(@Param("id") int id);
}
