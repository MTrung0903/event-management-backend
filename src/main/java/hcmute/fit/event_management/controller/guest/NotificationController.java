package hcmute.fit.event_management.controller.guest;

import hcmute.fit.event_management.dto.NotificationDTO;
import hcmute.fit.event_management.service.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {
    @Autowired
    SimpMessagingTemplate template;

    @Autowired
    INotificationService notificationService;

    @MessageMapping("/private")
    public void sendToSpecificUser(@Payload NotificationDTO notificationDTO) {
        String message = notificationDTO.getMessage();
        System.out.println("Sending message to user " + notificationDTO.getUserId() + ": " + message);
        notificationService.createNotification(notificationDTO);
        // Gửi thông báo tới người dùng cụ thể
        template.convertAndSendToUser(String.valueOf(notificationDTO.getUserId()), "/specific", notificationDTO);
    }

}