package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.BookingDetails;
import hcmute.fit.event_management.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Query("select t from Transaction t where t.booking.bookingCode = :orderCode")
    Optional<Transaction> findByOrderCode(@Param("orderCode") String orderCode);
}
