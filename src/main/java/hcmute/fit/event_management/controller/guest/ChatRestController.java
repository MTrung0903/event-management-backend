package hcmute.fit.event_management.controller.guest;

import hcmute.fit.event_management.dto.MessageDTO;
import hcmute.fit.event_management.dto.UserDTO;
import hcmute.fit.event_management.service.IMessageService;
import hcmute.fit.event_management.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatRestController {

    @Autowired
    private IMessageService messageService;
    @Autowired
    private IUserService userService;

    @GetMapping("/history/{user1Id}/{user2Id}")
    public ResponseEntity<List<MessageDTO>> getChatHistory(
            @PathVariable int user1Id,
            @PathVariable int user2Id) {
        try {
            List<MessageDTO> chatHistory = messageService.getChatHistory(user1Id, user2Id);
            // Mark messages as read when fetching history
            messageService.markMessagesAsRead(user1Id, user2Id);
            return new ResponseEntity<>(chatHistory, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{userId}/list-chat")
    public ResponseEntity<List<UserDTO>> getChatHistory(@PathVariable int userId) {
        List<UserDTO> chatHistory = messageService.getListUserChat(userId);
        return new ResponseEntity<>(chatHistory, HttpStatus.OK);
    }
    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers( @RequestParam String query, @RequestParam int currentUserId) {
        List<UserDTO> findUser = userService.searchUserForChat(query, currentUserId);
        return new ResponseEntity<>(findUser, HttpStatus.OK);
    }
}
