package hcmute.fit.event_management.service;

public interface EmailService {
    void sendResetEmail(String to, String resetToken);
    void sendHtmlEmail(String to, String subject, String htmlContent);
}
