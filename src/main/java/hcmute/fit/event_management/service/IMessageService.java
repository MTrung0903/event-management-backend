package hcmute.fit.event_management.service;

import hcmute.fit.event_management.dto.MessageDTO;

import java.util.List;

public interface IMessageService {
    void saveMessage(MessageDTO dto);

    List<MessageDTO> getChatHistory(int user1Id, int user2Id);
}
