package hcmute.fit.event_management.service.Impl;


import hcmute.fit.event_management.entity.Ticket;
import hcmute.fit.event_management.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.util.Random;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Override
    public void sendResetEmail(String to, String resetToken) {
        String subject = "Password Reset Request";
        String resetUrl =  "http://localhost:3000/reset-password?token=" + resetToken;
        String content =  "<p>Click the link below to reset it:</p>"
                + "<p><a href=\"" + resetUrl + "\">Reset my password</a></p>";
        sendHtmlEmail(to, subject, content);
    }
    @Override
    public void sendThanksPaymentEmail(String to, String eventName, String userName, List<Ticket> tickets) {
        try {
            String qrCodeBase64 = generateQrCodeBase64(tickets);

            String subject = "Your Event Ticket – " + eventName;
            String content = "<p>Dear " + userName + ",</p>"
                    + "<p>Thank you for purchasing a ticket to <strong>" + eventName + "</strong>.</p>"
                    + "<p>Your ticket code is: <strong>" + tickets + "</strong></p>"
                    + "<p>Please scan the QR code below at the event check-in:</p>"
                    + "<img src='data:image/png;base64," + qrCodeBase64 + "' alt='QR Code'/>"
                    + "<br><p>We look forward to seeing you there!</p>"
                    + "<p>Best regards,<br>The Event Team</p>";

            sendHtmlEmail(to, subject, content);
        } catch (Exception e) {
            System.err.println("Failed to generate QR code: " + e.getMessage());
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
    public String generateQrCodeBase64(String text) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);

        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "png", baos);

        byte[] imageBytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

}
