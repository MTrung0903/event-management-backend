package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.dto.MessageDTO;
import hcmute.fit.event_management.entity.Message;
import hcmute.fit.event_management.entity.User;
import hcmute.fit.event_management.repository.MessageRepository;
import hcmute.fit.event_management.repository.UserRepository;
import hcmute.fit.event_management.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements IMessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;


    @Override
    public Message createMessage(MessageDTO messageDTO) {
        User sender = userRepository.findByEmail(messageDTO.getSenderEmail())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User recipient = userRepository.findByEmail(messageDTO.getRecipientEmail())
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent(messageDTO.getContent());
        message.setTimestamp(parseTimestamp(messageDTO.getTimestamp()));

        return messageRepository.save(message);
    }
    private LocalDateTime parseTimestamp(String timestamp) {
        try {
            // Parse chuỗi ISO 8601 với múi giờ (ZonedDateTime)
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME);
            // Chuyển thành LocalDateTime (bỏ múi giờ)
            return zonedDateTime.toLocalDateTime();
        } catch (DateTimeParseException e) {
            // Log lỗi và ném ngoại lệ hoặc trả về giá trị mặc định
            throw new IllegalArgumentException("Invalid timestamp format: " + timestamp, e);
        }
    }

    @Override
    public List<MessageDTO> getChatHistory(int user1Id, int user2Id) {
        List<Message> messages = messageRepository.findChatHistoryBetweenUsers(user1Id, user2Id);
        return messages.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setContent(message.getContent());
        dto.setSenderEmail(message.getSender().getEmail());
        dto.setRecipientEmail(message.getRecipient().getEmail());
        dto.setTimestamp(message.getTimestamp().toString());
        return dto;
    }
}
