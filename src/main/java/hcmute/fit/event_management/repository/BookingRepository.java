package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Optional<Booking> findByBookingCode(String code);

    @Query("select b from Booking b where b.user.userId = :userId")
    Optional<Booking> findByUserId(@Param("userId") Integer userId);
}
