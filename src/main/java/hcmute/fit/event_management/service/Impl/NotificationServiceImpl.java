package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.dto.NotificationDTO;
import hcmute.fit.event_management.entity.Notification;
import hcmute.fit.event_management.entity.User;
import hcmute.fit.event_management.repository.NotificationRepository;
import hcmute.fit.event_management.repository.UserRepository;
import hcmute.fit.event_management.service.INotificationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NotificationServiceImpl implements INotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public Notification createNotification(NotificationDTO notificationDTO) {
        Notification notification = new Notification();
        BeanUtils.copyProperties(notificationDTO, notification);
        notification.setRead(false);
        notification.setUser(userRepository.findById(notificationDTO.getUserId()).orElse(new User()));
        notification.setCreatedAt(new Date());
        return notificationRepository.save(notification);
    }
    @Override
    public void markAsRead(int notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
    @Override
    public void markAllAsRead(int userId) {

        List<Notification> notifications = notificationRepository.findByUserId(userId);
        for (Notification notification : notifications) {
            notification.setRead(true);
        }
        notificationRepository.saveAll(notifications);
    }
}
