package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.userId = :user1Id AND m.recipient.userId = :user2Id) OR " +
            "(m.sender.userId = :user2Id AND m.recipient.userId = :user1Id) " +
            "ORDER BY m.timestamp ASC")
    List<Message> findChatHistoryBetweenUsers(
            @Param("user1Id") int user1Id,
            @Param("user2Id") int user2Id);
}
