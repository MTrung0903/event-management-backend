package hcmute.fit.event_management.service.Impl;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import hcmute.fit.event_management.entity.CheckInTicket;
import hcmute.fit.event_management.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import java.util.List;


import static org.unbescape.html.HtmlEscape.escapeHtml4;

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
    public void sendThanksPaymentEmail(String to, String eventName, String orderCode, String userName, List<CheckInTicket> tickets) throws Exception {
        if (to == null || !to.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            log.error("Địa chỉ email không hợp lệ: {}", to);
            throw new IllegalArgumentException("Địa chỉ email không hợp lệ");
        }
        if (tickets == null || tickets.isEmpty()) {
            log.error("Danh sách vé rỗng hoặc null cho đơn hàng: {}", orderCode);
            throw new IllegalArgumentException("Danh sách vé không được rỗng");
        }
        String safeUserName = userName != null && !userName.trim().isEmpty() ? escapeHtml4(userName) : "Khách hàng";
        String safeEventName = escapeHtml4(eventName);

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
                .append("a:hover { text-decoration: underline; }")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<p>Kính gửi ").append(safeUserName).append(",</p>")
                .append("<p>Cảm ơn bạn đã mua vé tham gia sự kiện <strong>").append(safeEventName).append("</strong>.</p>")
                .append("<h3>Thông tin vé của bạn:</h3>")
                .append("<ul>");
        String qrUrl = "http://localhost:3000"  + "/view-tickets/" + orderCode;
        for (CheckInTicket ticket : tickets) {
            try {
                content.append("<li>")
                        .append("<strong>Mã vé:</strong> ").append(ticket.getTicketCode()).append("<br>")
                        .append("<strong>Sự kiện:</strong> ").append(safeEventName).append("<br>")
                        .append("<strong>Thời gian:</strong> ").append(ticket.getBookingDetails().getTicket().getEvent().getEventStart().toString()).append("<br>")
                        .append("<strong>Địa điểm:</strong> ").append(ticket.getBookingDetails().getTicket().getEvent().getEventLocation().getVenueName()).append(", ").append(ticket.getBookingDetails().getTicket().getEvent().getEventLocation().getAddress()).append(", ").append(ticket.getBookingDetails().getTicket().getEvent().getEventLocation().getCity()).append("<br>")
                        .append("<strong>Giá vé:</strong> ").append(String.format("%.2f", ticket.getBookingDetails().getTicket().getPrice())).append("VNĐ").append("<br>")
                        .append("</li>");
            } catch (Exception e) {

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
    public void sendHtmlEmail(String to, String subject, String htmlContent, List<CheckInTicket> tickets) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom("tungvladgod@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            if (tickets != null) {
                for (CheckInTicket ticket : tickets) {
                    try {
                        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
                        MatrixToImageWriter.writeToStream(
                                new QRCodeWriter().encode("http://localhost:3000"  + "/check-in/" + ticket.getTicketCode(), BarcodeFormat.QR_CODE, 200, 200),
                                "PNG", pngOutputStream
                        );
                        helper.addAttachment(ticket.getTicketCode() + ".png", new ByteArrayResource(pngOutputStream.toByteArray()));
                    } catch (WriterException | IOException e) {
                        log.error("Lỗi khi đính kèm mã QR cho vé {}: {}", ticket.getTicketCode(), e.getMessage(), e);
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
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // Enable HTML content
        helper.setTo(email);
        helper.setSubject("Mã Xác Minh Đăng Ký Tài Khoản");

        // Nội dung email được định dạng bằng HTML
        String htmlContent = "<!DOCTYPE html>" +
                "<html>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                "<h2 style='color: #2c3e50;'>Xác Minh Đăng Ký Tài Khoản</h2>" +
                "<p>Kính gửi Quý khách,</p>" +
                "<p>Cảm ơn bạn đã đăng ký tài khoản tại hệ thống của chúng tôi. Để hoàn tất quá trình đăng ký, vui lòng sử dụng mã xác minh dưới đây:</p>" +
                "<div style='background-color: #f8f9fa; padding: 15px; text-align: center; border-radius: 5px; margin: 20px 0;'>" +
                "<h3 style='color: #e74c3c; margin: 0;'>Mã xác minh: " + code + "</h3>" +
                "</div>" +
                "<p>Vui lòng nhập mã này vào trang xác minh trên website của chúng tôi để kích hoạt tài khoản </p>" +
                "<p><strong>Lưu ý:</strong> Vui lòng không chia sẻ mã xác minh này với bất kỳ ai để đảm bảo an toàn cho tài khoản của bạn.</p>" +
                "<p>Nếu bạn không thực hiện yêu cầu này, xin vui lòng bỏ qua email này.</p>" +
                "<p>Nếu cần hỗ trợ, vui lòng liên hệ với chúng tôi qua email: <a href='mailto:\n" +
                "tungvladgod@gmail.com' style='color: #3498db;'>support@eventmanagement.com</a>.</p>" +
                "<p>Trân trọng,<br>Đội ngũ Event Management</p>" +
                "<hr style='border-top: 1px solid #eee;'>" +
                "<p style='font-size: 12px; color: #777;'>Đây là email tự động, vui lòng không trả lời trực tiếp. Nếu cần hỗ trợ, liên hệ qua email hỗ trợ ở trên.</p>" +
                "</body>" +
                "</html>";

        helper.setText(htmlContent, true); // true để bật chế độ HTML
        mailSender.send(message);
        return code;
    }
    @Override
    public void sendNewEventNotification(String to, String eventName, String eventStart, String eventLocation, String eventUrl) throws MessagingException {
        String safeEventName = escapeHtml4(eventName);
        String subject = "Sự kiện mới: " + safeEventName;

        String htmlContent = "<!DOCTYPE html>" +
                "<html lang='vi'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>Thông báo sự kiện mới</title>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; }" +
                ".container { max-width: 600px; margin: 0 auto; background: #f9f9f9; padding: 20px; border-radius: 8px; }" +
                "h2 { color: #2c3e50; }" +
                "a { color: #3498db; text-decoration: none; }" +
                "a:hover { text-decoration: underline; }" +
                ".button { display: inline-block; padding: 10px 20px; background: #3498db; color: white; border-radius: 5px; text-align: center; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<h2>Thông báo sự kiện mới</h2>" +
                "<p>Kính gửi Quý khách,</p>" +
                "<p>Chúng tôi rất vui thông báo rằng một sự kiện mới vừa được tạo: <strong>" + safeEventName + "</strong>.</p>" +
                "<p><strong>Thời gian:</strong> " + eventStart + "</p>" +
                "<p><strong>Địa điểm:</strong> " + eventLocation + "</p>" +
                "<p>Hãy tham gia ngay để không bỏ lỡ sự kiện thú vị này!</p>" +
                "<p><a href='" + eventUrl + "' class='button'>Xem chi tiết sự kiện</a></p>" +
                "<p>Nếu cần hỗ trợ, vui lòng liên hệ qua email: <a href='mailto:tungvladgod@gmail.com'>support@eventmanagement.com</a>.</p>" +
                "<p>Trân trọng,<br>Đội ngũ Event Management</p>" +
                "</div>" +
                "</body>" +
                "</html>";

        sendHtmlEmail(to, subject, htmlContent);
    }

}
