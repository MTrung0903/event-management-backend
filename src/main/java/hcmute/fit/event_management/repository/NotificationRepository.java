package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository< Notification,Integer> {
    @Query("select n from Notification n where n.user.userId =:userId")
    List<Notification> findByUserId(@Param("userId") int userId );
}
