package hcmute.fit.event_management.controller.guest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hcmute.fit.event_management.dto.ErrorMessage;
import hcmute.fit.event_management.dto.MessageDTO;

import hcmute.fit.event_management.entity.User;
import hcmute.fit.event_management.repository.UserRepository;
import hcmute.fit.event_management.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ChatController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpUserRegistry simpUserRegistry;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MessageMapping("/chat.join")
    public void join(@Payload MessageDTO chatMessage) {
        // Log SimpUserRegistry
        System.out.println("SimpUserRegistry users: " + simpUserRegistry.getUsers().stream()
                .map(SimpUser::getName)
                .collect(Collectors.joining(", ")));
        System.out.println("SimpUserRegistry user count: " + simpUserRegistry.getUserCount());
        for (SimpUser user : simpUserRegistry.getUsers()) {
            System.out.println("User: " + user.getName() + ", Sessions: " + user.getSessions());
        }
        // Log SecurityContext
        System.out.println("SecurityContext user: " + (SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName() : "none"));

        String senderEmail = chatMessage.getSenderEmail().toLowerCase().trim();
        String recipientEmail = chatMessage.getRecipientEmail() != null ? chatMessage.getRecipientEmail().toLowerCase().trim() : null;
        System.out.println("Received /app/chat.join: " + chatMessage);
        chatMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        chatMessage.setContent(senderEmail + " joined!");
        try {
            System.out.println("Sending JSON to /user/" + senderEmail + "/queue/private: " + objectMapper.writeValueAsString(chatMessage));
            messagingTemplate.convertAndSendToUser(senderEmail, "/queue/private", chatMessage);
        } catch (JsonProcessingException e) {
            System.err.println("Error serializing join message: " + e.getMessage());
        }

        if (recipientEmail != null && !recipientEmail.isEmpty()) {
            User sender = userRepository.findByEmail(senderEmail)
                    .orElseThrow(() -> new RuntimeException("Sender not found"));
            User recipient = userRepository.findByEmail(recipientEmail)
                    .orElseThrow(() -> new RuntimeException("Recipient not found"));
            List<MessageDTO> history = messageService.getChatHistory(sender.getUserId(), recipient.getUserId());
            System.out.println("Chat history size for " + sender.getEmail() + " and " + recipient.getEmail() + ": " + history.size());
            history.forEach(msg -> {
                try {
                    System.out.println("Sending history JSON to /user/" + senderEmail + "/queue/private: " + objectMapper.writeValueAsString(msg));
                    messagingTemplate.convertAndSendToUser(senderEmail, "/queue/private", msg);
                    System.out.println("Sending history JSON to /user/" + recipientEmail + "/queue/private: " + objectMapper.writeValueAsString(msg));
                    messagingTemplate.convertAndSendToUser(recipientEmail, "/queue/private", msg);
                } catch (JsonProcessingException e) {
                    System.err.println("Error serializing history message: " + e.getMessage());
                }
            });
        }
    }

    @MessageMapping("/chat.sendPrivateMessage")
    public void sendPrivateMessage(@Payload MessageDTO chatMessage) {
        try {
            String senderEmail = chatMessage.getSenderEmail().toLowerCase().trim();
            String recipientEmail = chatMessage.getRecipientEmail().toLowerCase().trim();
            System.out.println("Received /app/chat.sendPrivateMessage: " + chatMessage);
            chatMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            messageService.saveMessage(chatMessage);
            System.out.println("Sending JSON to /user/" + recipientEmail + "/queue/private: " + objectMapper.writeValueAsString(chatMessage));
            messagingTemplate.convertAndSendToUser(recipientEmail, "/queue/private", chatMessage);
            System.out.println("Sending JSON to /user/" + senderEmail + "/queue/private: " + objectMapper.writeValueAsString(chatMessage));
            messagingTemplate.convertAndSendToUser(senderEmail, "/queue/private", chatMessage);
        } catch (Exception e) {
            System.err.println("Error processing /app/chat.sendPrivateMessage: " + e.getMessage());
            messagingTemplate.convertAndSendToUser(
                    chatMessage.getSenderEmail().toLowerCase().trim(),
                    "/queue/error",
                    new ErrorMessage("Error sending message: " + e.getMessage())
            );
        }
    }
}