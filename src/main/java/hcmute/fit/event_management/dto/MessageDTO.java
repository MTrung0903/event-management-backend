package hcmute.fit.event_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class MessageDTO {
    private String content;
    private String senderEmail;
    private String recipientEmail;
    private String timestamp;
    private boolean isRead;
    public MessageDTO() {}
    public MessageDTO(String content, String senderEmail, String recipientEmail, String timestamp, boolean isRead) {
        this.content = content;
        this.senderEmail = senderEmail;
        this.recipientEmail = recipientEmail;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }
}