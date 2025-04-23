package hcmute.fit.event_management.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendResetEmail(String to, String resetToken);
    void sendHtmlEmail(String to, String subject, String htmlContent);

    String sendVerificationCode(String email) throws MessagingException;
}
