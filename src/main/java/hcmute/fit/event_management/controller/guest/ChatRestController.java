package hcmute.fit.event_management.controller.guest;

import hcmute.fit.event_management.dto.MessageDTO;
import hcmute.fit.event_management.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatRestController {

    @Autowired
    private IMessageService messageService;

    @GetMapping("/history/{user1Id}/{user2Id}")
    public ResponseEntity<List<MessageDTO>> getChatHistory(
            @PathVariable int user1Id,
            @PathVariable int user2Id) {
        try {
            List<MessageDTO> chatHistory = messageService.getChatHistory(user1Id, user2Id);
            return new ResponseEntity<>(chatHistory, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
