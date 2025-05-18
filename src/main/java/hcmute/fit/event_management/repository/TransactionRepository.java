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
    @Query("select t from Transaction t where t.referenceCode = :orderCode")
    Optional<Transaction> findByOrderCode(@Param("orderCode") String orderCode);
    @Query(value = "SELECT SUM(transaction_amount)*0.05 FROM transaction WHERE SUBSTRING(transaction_date, 1, 6) = :yearMonth AND transaction_status = 'SUCCESSFULLY'", nativeQuery = true)
    Double getRevenueByMonth(@Param("yearMonth") String yearMonth); // Ví dụ: "202505"

    @Query(value = "SELECT SUM(transaction_amount)*0.05 FROM transaction WHERE transaction_status = 'SUCCESSFULLY'", nativeQuery = true)
    Double getRevenue();

    @Query("select t from Transaction t where t.booking.event.eventID = :eventId")
    List<Transaction> transactions(@Param("eventId") int eventId);

    @Query("SELECT SUM(t.transactionAmount) FROM Transaction t WHERE t.booking.event.user.userId = :userId")
    double sumRevenueByOrganizer(int userId);

    @Query("SELECT t FROM Transaction t WHERE t.booking.event.user.userId = :userId")
    List<Transaction> findByOrganizer(int userId);
}
