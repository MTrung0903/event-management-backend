package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.BookingDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingDetailsRepository extends JpaRepository<BookingDetails, Integer> {
    @Query("select bd from BookingDetails bd where bd.ticket.ticketId = :ticketId")
    List<BookingDetails> findByTicketId(@Param("ticketId") int ticketId);
    @Query("select bd from BookingDetails bd where bd.booking.bookingId = :bookingId")
    List<BookingDetails> findByBookingId(@Param("bookingId") int bookingId);
}
