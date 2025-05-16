package hcmute.fit.event_management.service.Impl;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import hcmute.fit.event_management.entity.Ticket;
import hcmute.fit.event_management.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

import java.util.List;

import static hcmute.fit.event_management.util.QRCodeUtil.generateQrCodeBase64;
@Slf4j
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
    public void sendThanksPaymentEmail(String to, String eventName, String orderCode, String userName, List<Ticket> tickets) throws Exception {
        if (to == null || !to.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            log.error("Địa chỉ email không hợp lệ: {}", to);
            throw new IllegalArgumentException("Địa chỉ email không hợp lệ");
        }
        if (tickets == null || tickets.isEmpty()) {
            log.error("Danh sách vé rỗng hoặc null cho đơn hàng: {}", orderCode);
            throw new IllegalArgumentException("Danh sách vé không được rỗng");
        }
        String safeUserName = userName != null && !userName.trim().isEmpty() ? StringEscapeUtils.escapeHtml4(userName) : "Khách hàng";
        String safeEventName = StringEscapeUtils.escapeHtml4(eventName);

        StringBuilder content = new StringBuilder();
        content.append("<!DOCTYPE html>")
                .append("<html lang='vi'>")
                .append("<head>")
                .append("<meta charset='UTF-8'>")
                .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                .append("<title>Vé sự kiện ").append(safeEventName).append("</title>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; }")
                .append("ul { list-style-type: none; padding: 0; }")
                .append("li { margin-bottom: 15px; border-bottom: 1px solid #eee; padding-bottom: 10px; }")
                .append("img { margin: 10px 0; display: block; max-width: 200px; border: 1px solid #ddd; }")
                .append("a { color: #007bff; text-decoration: none; }")
                .append("a:hover { text-decoration: underline; }")
                .append(".fallback { color: #555; font-style: italic; }")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<p>Kính gửi ").append(safeUserName).append(",</p>")
                .append("<p>Cảm ơn bạn đã mua vé tham gia sự kiện <strong>").append(safeEventName).append("</strong>.</p>")
                .append("<h3>Thông tin vé của bạn:</h3>")
                .append("<ul>");
        String qrUrl = "http://localhost:3000"  + "/view-tickets/" + orderCode;
        for (Ticket ticket : tickets) {

            try {
                String qrBase64 = generateQrCodeBase64("http://localhost:3000" + "/check-in/" + ticket.getTicketId());
                content.append("<li>")
                        .append("<strong>Mã vé:</strong> ").append(StringEscapeUtils.escapeHtml4(String.valueOf(ticket.getTicketId()))).append("<br>")
                        .append("<img src='data:image/png;base64,").append(qrBase64).append("' alt='Mã QR cho vé ").append(StringEscapeUtils.escapeHtml4(String.valueOf(ticket.getTicketId()))).append("' width='200' style='display: block; margin: 10px 0;' />")
                        .append("<p class='fallback'>Nếu mã QR không hiển thị, vui lòng kiểm tra tệp đính kèm hoặc truy cập liên kết bên dưới.</p>")
                        .append("<strong>Sự kiện:</strong> ").append(safeEventName).append("<br>")
                        .append("<strong>Giá vé:</strong> $").append(String.format("%.2f", ticket.getPrice())).append("<br>")
                        .append("</li>");
            } catch (Exception e) {
                log.error("Lỗi khi tạo mã QR cho vé {}: {}", ticket.getTicketId(), e.getMessage(), e);
                content.append("<li>")
                        .append("<strong>Mã vé:</strong> ").append(StringEscapeUtils.escapeHtml4(String.valueOf(ticket.getTicketId()))).append("<br>")
                        .append("<p style='color: red;'>Lỗi: Không thể tạo mã QR cho vé này. Vui lòng kiểm tra tệp đính kèm.</p>")
                        .append("<strong>Sự kiện:</strong> ").append(safeEventName).append("<br>")
                        .append("<strong>Giá vé:</strong> $").append(String.format("%.2f", ticket.getPrice())).append("<br>")
                        .append("</li>");
            }
        }
        content.append("</ul>")
                .append("<p>Xem vé của bạn tại đây:</p>")
                .append("<p><a href='").append(qrUrl).append("' style='color: #007bff;'>Nhấn vào đây để xem vé</a></p>")
                .append("<br><p>Chúng tôi rất mong được gặp bạn tại sự kiện!</p>")
                .append("<p>Trân trọng,<br>Đội ngũ tổ chức sự kiện</p>")
                .append("</body>")
                .append("</html>");

        String subject = "Vé sự kiện của bạn – " + safeEventName;
        sendHtmlEmail(to, subject, content.toString(), tickets);
    }

    private String generateQrCodeBase64(String text) throws WriterException, IOException {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(pngData);
            if (base64 == null || base64.isEmpty()) {
                log.error("Chuỗi Base64 của mã QR rỗng cho URL: {}", text);
                throw new IOException("Không thể tạo mã QR Base64");
            }
            log.info("Tạo mã QR Base64 thành công cho URL: {}, độ dài chuỗi: {}", text, base64.length());
            return base64;
        } catch (WriterException | IOException e) {
            log.error("Lỗi khi tạo mã QR cho URL {}: {}", text, e.getMessage(), e);
            throw e;
        }
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent, List<Ticket> tickets) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom("tungvladgod@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            if (tickets != null) {
                for (Ticket ticket : tickets) {

                    try {
                        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
                        MatrixToImageWriter.writeToStream(
                                new QRCodeWriter().encode("http://localhost:3000"  + "/check-in/" + ticket.getTicketId(), BarcodeFormat.QR_CODE, 200, 200),
                                "PNG", pngOutputStream
                        );
                        helper.addAttachment("qr_" + ticket.getTicketId() + ".png", new ByteArrayResource(pngOutputStream.toByteArray()));
                    } catch (WriterException | IOException e) {
                        log.error("Lỗi khi đính kèm mã QR cho vé {}: {}", ticket.getTicketId(), e.getMessage(), e);
                    }
                }
            }
            mimeMessage.setHeader("Content-Transfer-Encoding", "base64");
            mailSender.send(mimeMessage);
            log.info("Gửi email thành công tới: {}", to);
        } catch (MessagingException e) {
            log.error("Lỗi khi gửi email tới {}: {}", to, e.getMessage(), e);
            throw e;
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
