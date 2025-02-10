package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.Mc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface McRepository extends JpaRepository<Mc,Integer> {

   @Query("select m from Mc m WHERE m.mcName = :name OR m.email = :name ")
   Mc findByMcName(@Param("name") String name);

}
