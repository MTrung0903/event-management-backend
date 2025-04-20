package hcmute.fit.event_management.controller.guest;

import hcmute.fit.event_management.dto.MessageDTO;
import hcmute.fit.event_management.entity.User;
import hcmute.fit.event_management.repository.UserRepository;
import hcmute.fit.event_management.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class ChatController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private UserRepository userRepository;

    @MessageMapping("/chat.join")
    public void join(@Payload MessageDTO chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        chatMessage.setContent(chatMessage.getSenderEmail() + " joined!");
        // Gửi thông báo "joined" đến người dùng
        messagingTemplate.convertAndSendToUser(
                chatMessage.getSenderEmail(),
                "/queue/private",
                chatMessage
        );
        // Gửi lịch sử chat nếu có recipient
        if (chatMessage.getRecipientEmail() != null && !chatMessage.getRecipientEmail().isEmpty()) {
            User sender = userRepository.findByEmail(chatMessage.getSenderEmail())
                    .orElseThrow(() -> new RuntimeException("Sender not found"));
            User recipient = userRepository.findByEmail(chatMessage.getRecipientEmail())
                    .orElseThrow(() -> new RuntimeException("Recipient not found"));
            List<MessageDTO> history = messageService.getChatHistory(sender.getUserId(), recipient.getUserId());
            // Gửi lịch sử chat đến người dùng hiện tại
            history.forEach(msg -> messagingTemplate.convertAndSendToUser(
                    chatMessage.getSenderEmail(),
                    "/queue/private",
                    msg
            ));
        }
    }

    @MessageMapping("/chat.sendPrivateMessage")
    public void sendPrivateMessage(@Payload MessageDTO chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        // Lưu tin nhắn vào database
        messageService.saveMessage(chatMessage);
        // Gửi tin nhắn đến người nhận
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientEmail(),
                "/queue/private",
                chatMessage
        );
        // Gửi tin nhắn đến người gửi (để hiển thị trên giao diện)
        messagingTemplate.convertAndSendToUser(
                chatMessage.getSenderEmail(),
                "/queue/private",
                chatMessage
        );
    }
}
