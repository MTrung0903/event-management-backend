package hcmute.fit.event_management.service;

import hcmute.fit.event_management.dto.NotificationDTO;
import hcmute.fit.event_management.entity.Notification;

public interface INotificationService {
    Notification createNotification(NotificationDTO notificationDTO);

    void markAsRead(int notificationId);

    void markAllAsRead(int userId);
}
