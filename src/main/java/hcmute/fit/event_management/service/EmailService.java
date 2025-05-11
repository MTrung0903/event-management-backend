package hcmute.fit.event_management.service;

import hcmute.fit.event_management.entity.Ticket;
import jakarta.mail.MessagingException;

import java.util.List;

public interface EmailService {
    void sendResetEmail(String to, String resetToken);

    void sendThanksPaymentEmail(String to, String eventName, String orderCode, String userName, List<Ticket> tickets);

    void sendHtmlEmail(String to, String subject, String htmlContent);

    String sendVerificationCode(String email) throws MessagingException;
}
