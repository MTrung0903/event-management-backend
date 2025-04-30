package hcmute.fit.event_management.controller.guest;

import hcmute.fit.event_management.dto.NotificationDTO;
import hcmute.fit.event_management.entity.Notification;
import hcmute.fit.event_management.entity.User;
import hcmute.fit.event_management.repository.NotificationRepository;
import hcmute.fit.event_management.repository.UserRepository;
import hcmute.fit.event_management.service.INotificationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import payload.Response;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/notify")
public class NotificationRestController {

   private NotificationRepository notificationRepository;

    private UserRepository userRepository;
    @Autowired
    private INotificationService notificationService;
    @Autowired
    SimpMessagingTemplate template;

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationDTO notificationDTO) {
        try {
            // Gửi thông báo qua WebSocket
            template.convertAndSendToUser(
                    String.valueOf(notificationDTO.getUserId()),
                    "/specific",
                    notificationDTO
            );
            return ResponseEntity.ok("Notification sent successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send notification.");
        }
    }
    @GetMapping("/{userId}")
    public ResponseEntity<?> getNotify(@PathVariable int userId) {
        User account = userRepository.findById(userId).orElse(null);
        List<Notification> listNotify =  notificationRepository.findByUserId(userId);
        List<NotificationDTO> notificationDTOList = new ArrayList<>();
        for (Notification notification : listNotify) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification, notificationDTO);
            notificationDTOList.add(notificationDTO);
        }
        Response response = new Response(200, "Success", notificationDTOList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PutMapping("/{notificationId}/read")
    public void markNotificationAsRead(@PathVariable int notificationId) {
        notificationService.markAsRead(notificationId);
    }
    @PutMapping("/readAll/{userId}")
    public void markAllNotificationAsRead(@PathVariable int userId) {
        notificationService.markAllAsRead(userId);
    }
}
