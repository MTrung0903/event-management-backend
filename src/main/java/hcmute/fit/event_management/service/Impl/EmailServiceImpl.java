package hcmute.fit.event_management.service.Impl;


import hcmute.fit.event_management.entity.Ticket;
import hcmute.fit.event_management.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendResetEmail(String to, String resetToken) {
        String subject = "Password Reset Request";
        String resetUrl = "http://localhost:3000/reset-password?token=" + resetToken;
        String content = "<p>Click the link below to reset it:</p>"
                + "<p><a href=\"" + resetUrl + "\">Reset my password</a></p>";
        sendHtmlEmail(to, subject, content);
    }

    @Override
    public void sendThanksPaymentEmail(String to, String eventName, String orderCode, String userName, List<Ticket> tickets) {
        try {
            StringBuilder content = new StringBuilder();
            content.append("<p>Dear ").append(userName).append(",</p>")
                    .append("<p>Thank you for purchasing tickets to <strong>").append(eventName).append("</strong>.</p>")
                    .append("<p>You can view and download your QR ticket <a href=\"https://localhost:3000/view-tickets/")
                    .append(orderCode).append("\">here</a>.</p>");
            content.append("<br><p>We look forward to seeing you there!</p>")
                    .append("<p>Best regards,<br>The Event Team</p>");
            String subject = "Your Event Tickets – " + eventName;
            sendHtmlEmail("sidedlove03@gmail.com", subject, content.toString());
        } catch (Exception e) {
            System.err.println("Failed to send QR code email: " + e.getMessage());
        }
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true để gửi nội dung dưới dạng HTML
            mailSender.send(mimeMessage);
            System.out.println("Reset password email sent successfully.");
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    public String sendVerificationCode(String email) throws MessagingException {

        String code = String.format("%06d", new Random().nextInt(999999));


        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false);
        helper.setTo(email);
        helper.setSubject("Mã Xác Minh Đăng Ký");
        helper.setText(code);
        mailSender.send(message);

        return code;
    }


}
