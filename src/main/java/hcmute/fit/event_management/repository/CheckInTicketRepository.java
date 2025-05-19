package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.Booking;
import hcmute.fit.event_management.entity.CheckInTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckInTicketRepository extends JpaRepository<CheckInTicket, String> {
    List<CheckInTicket> findByBookingDetailsBookingEventEventID(int eventID);
}
