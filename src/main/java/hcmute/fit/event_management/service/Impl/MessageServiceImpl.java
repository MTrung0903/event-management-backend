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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements IMessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;
    @Override
    public void saveMessage(MessageDTO dto) {
        User sender = userRepository.findByEmail(dto.getSenderEmail())
                .orElseThrow(() -> new RuntimeException("Sender not found: " + dto.getSenderEmail()));
        User recipient = userRepository.findByEmail(dto.getRecipientEmail())
                .orElseThrow(() -> new RuntimeException("Recipient not found: " + dto.getRecipientEmail()));

        Message entity = new Message();
        entity.setSender(sender);
        entity.setRecipient(recipient);
        entity.setContent(dto.getContent());
        entity.setTimestamp(LocalDateTime.now());
        messageRepository.save(entity);
    }

    @Override
    public List<MessageDTO> getChatHistory(int user1Id, int user2Id) {
        List<Message> entities = messageRepository.findChatHistoryBetweenUsers(user1Id, user2Id);
        return entities.stream().map(entity -> new MessageDTO(
                entity.getContent(),
                entity.getSender().getEmail(),
                entity.getRecipient().getEmail(),
                entity.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        )).collect(Collectors.toList());
    }
}
