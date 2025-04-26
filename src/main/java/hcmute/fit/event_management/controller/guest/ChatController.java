package hcmute.fit.event_management.controller.guest;

import hcmute.fit.event_management.dto.MessageDTO;

import hcmute.fit.event_management.dto.TypingDTO;
import hcmute.fit.event_management.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private IMessageService messageService;

    @MessageMapping("/chat")
    public void sendMessage(@Payload MessageDTO messageDTO) {
        // Lưu tin nhắn vào cơ sở dữ liệu
        messageService.createMessage(messageDTO);

        // Gửi tin nhắn tới người nhận qua WebSocket
        template.convertAndSendToUser(
                messageDTO.getRecipientEmail(),
                "/chat",
                messageDTO
        );

        // Gửi lại tin nhắn tới người gửi để cập nhật giao diện
        template.convertAndSendToUser(
                messageDTO.getSenderEmail(),
                "/chat",
                messageDTO
        );
    }
    @MessageMapping("/typing")
    public void sendTypingNotification(@Payload TypingDTO typingDTO) {
        template.convertAndSendToUser(
                typingDTO.getRecipientEmail(),
                "/typing",
                typingDTO
        );
    }
}