package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    // Tìm kiếm theo tên sự kiện (không phân biệt hoa thường)
    List<Event> findByEventNameContainingIgnoreCase(String eventName);

    // Tìm kiếm theo ngày bắt đầu sự kiện
    List<Event> findByEventStart(String eventStart);

    // Tìm kiếm theo người tổ chức (không phân biệt hoa thường)
    List<Event> findByEventHostContainingIgnoreCase(String eventHost);

    // Tìm kiếm theo tag (không phân biệt hoa thường)
    List<Event> findByTagsContainingIgnoreCase(String tag);

    // Tìm kiếm theo loại sự kiện (không phân biệt hoa thường)
    List<Event> findByEventTypeContainingIgnoreCase(String eventType);

    // Tìm kiếm theo thành phố trong eventLocation (không phân biệt hoa thường)
    List<Event> findByEventLocationCityContainingIgnoreCase(String city);

    // Tìm kiếm theo tên địa điểm trong eventLocation (không phân biệt hoa thường)
    List<Event> findByEventLocationVenueNameContainingIgnoreCase(String venueName);

}
