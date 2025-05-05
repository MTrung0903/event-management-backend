package hcmute.fit.event_management.repository;

import feign.Param;
import hcmute.fit.event_management.entity.Booking;
import hcmute.fit.event_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Optional<Booking> findByBookingCode(String code);
    @Query("select b from Booking b where b.user.userId = :userId")
    List<Booking> findByUserId(@Param("userId") int userId);

}
