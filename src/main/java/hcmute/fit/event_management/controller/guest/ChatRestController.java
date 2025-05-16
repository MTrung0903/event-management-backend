package hcmute.fit.event_management.controller.guest;

import hcmute.fit.event_management.dto.MessageDTO;
import hcmute.fit.event_management.dto.UserDTO;
import hcmute.fit.event_management.service.IMessageService;
import hcmute.fit.event_management.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatRestController {

    @Autowired
    private IMessageService messageService;
    @Autowired
    private IUserService userService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = uploadToStorage(file);
            System.out.println("Uploaded file: " + fileName);
            return new ResponseEntity<>(fileName, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("File upload failed: " + e.getMessage());
            return new ResponseEntity<>("File upload failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String uploadToStorage(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get("D:/Uploads", fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());
        System.out.println("File saved at: " + filePath.toAbsolutePath());
        return fileName;
    }

    @GetMapping("/history/{user1Id}/{user2Id}")
    public ResponseEntity<List<MessageDTO>> getChatHistory(
            @PathVariable int user1Id,
            @PathVariable int user2Id) {
        try {
            List<MessageDTO> chatHistory = messageService.getChatHistory(user1Id, user2Id);
            System.out.println("Chat history fetched for users: " + user1Id + ", " + user2Id);
            messageService.markMessagesAsRead(user1Id, user2Id);
            return new ResponseEntity<>(chatHistory, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Failed to fetch chat history: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{userId}/list-chat")
    public ResponseEntity<List<UserDTO>> getChatHistory(@PathVariable int userId) {
        List<UserDTO> chatHistory = messageService.getListUserChat(userId);
        System.out.println("Chat list fetched for user: " + userId);
        return new ResponseEntity<>(chatHistory, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String query, @RequestParam int currentUserId) {
        List<UserDTO> findUser = userService.searchUserForChat(query, currentUserId);
        System.out.println("Search users with query: " + query);
        return new ResponseEntity<>(findUser, HttpStatus.OK);
    }
}