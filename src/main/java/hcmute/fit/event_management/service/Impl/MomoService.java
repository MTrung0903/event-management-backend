package hcmute.fit.event_management.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hcmute.fit.event_management.config.MomoAPI;
import hcmute.fit.event_management.config.MomoConfig;

import hcmute.fit.event_management.dto.CheckoutDTO;
import hcmute.fit.event_management.dto.MomoRequestPayment;
import hcmute.fit.event_management.entity.*;
import hcmute.fit.event_management.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static hcmute.fit.event_management.util.PaymentUtil.hmacSHA256;
@Service
@Slf4j
@AllArgsConstructor
public class MomoService {

    @Autowired
    MomoConfig momoConfig;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    BookingDetailsRepository bookingDetailsRepository;

    private final MomoAPI momoAPI;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private CheckInTicketRepository checkInTicketRepository;
    public ResponseEntity<?> createQRCode(CheckoutDTO checkoutDTO) {
        try {
            // 1. Khởi tạo các biến cần thiết
            String partnerCode = momoConfig.getPartnerCode();
            String accessKey = momoConfig.getAccessKey();
            String redirectUrl = momoConfig.getRedirectUrl();
            String ipnUrl = momoConfig.getIpnUrl();
            String requestType = momoConfig.getRequestType();
            String secretKey = momoConfig.getSecretKey();

            String orderId = UUID.randomUUID().toString();
            String requestId = UUID.randomUUID().toString();
            String orderInfo = checkoutDTO.getOrderInfo();
            int amount = (int) checkoutDTO.getAmount();

            // 2. Tạo chuỗi rawHash và chữ ký
            String rawHash = String.format("accessKey=%s&amount=%d&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                    accessKey, amount, orderInfo, ipnUrl, orderId, orderInfo, partnerCode, redirectUrl, requestId, requestType);
            String signature = hmacSHA256(secretKey, rawHash);

            // 3. Tạo đối tượng request gửi đến MoMo
            MomoRequestPayment request = MomoRequestPayment.builder()
                    .partnerCode(partnerCode)
                    .requestType(requestType)
                    .ipnUrl(ipnUrl)
                    .redirectUrl(redirectUrl)
                    .orderId(orderId)
                    .orderInfo(orderInfo)
                    .requestId(requestId)
                    .amount(amount)
                    .extraData(orderInfo)
                    .signature(signature)
                    .lang("vi")
                    .build();

            // 4. Tạo thời gian tạo và hết hạn của booking
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            Date createDate = formatter.parse(formatter.format(calendar.getTime()));
            calendar.add(Calendar.HOUR, 1);
            calendar.add(Calendar.MINUTE, 40);
            Date expireDate = formatter.parse(formatter.format(calendar.getTime()));

            // 5. Tạo Booking và lưu vào DB
            Booking booking = new Booking();
            booking.setBookingCode(orderId);
            booking.setBookingMethod("Momo");
            booking.setBookingStatus("Pending");
            booking.setTotalPrice(amount);
            booking.setCreateDate(createDate);
            booking.setExpireDate(expireDate);

            Event event = eventRepository.findById(checkoutDTO.getEventId()).orElseThrow(() -> new RuntimeException("Event not found"));
            User user = userRepository.findById(checkoutDTO.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
            booking.setEvent(event);
            booking.setUser(user);

            bookingRepository.saveAndFlush(booking);

            // 6. Tạo BookingDetails cho từng vé
            for (Map.Entry<Integer, Integer> entry : checkoutDTO.getTickets().entrySet()) {
                Ticket ticket = ticketRepository.findById(entry.getKey()).orElseThrow(() -> new RuntimeException("Ticket not found"));
                BookingDetails details = new BookingDetails();
                details.setBooking(booking);
                details.setTicket(ticket);
                details.setQuantity(entry.getValue());
                details.setPrice(amount);
                bookingDetailsRepository.save(details);
            }

            // 7. Gọi API Momo
            return momoAPI.createMomoQR(request);

        } catch (Exception e) {
            log.error("Failed to create MoMo QR code: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi tạo QR code thanh toán.");
        }
    }

    public void ipn(Map<String, String> payload) {
        String partnerCode = momoConfig.getPartnerCode();
        String accessKey = momoConfig.getAccessKey();
        String secretKey = momoConfig.getSecretKey();

        String orderId = payload.get("orderId");
        Optional<Booking> optionalBooking = bookingRepository.findByBookingCode(orderId);
        if (optionalBooking.isEmpty()) return;

        Booking booking = optionalBooking.get();
        if ("PAID".equalsIgnoreCase(booking.getBookingStatus())) return;

        String rawHash = String.format(
                "accessKey=%s&amount=%s&extraData=%s&message=%s&orderId=%s&orderInfo=%s" +
                        "&orderType=%s&partnerCode=%s&payType=%s&requestId=%s&responseTime=%s&resultCode=%s&transId=%s",
                accessKey,
                payload.get("amount"),
                payload.get("extraData"),
                payload.get("message"),
                orderId,
                payload.get("orderInfo"),
                payload.get("orderType"),
                partnerCode,
                payload.get("payType"),
                payload.get("requestId"),
                payload.get("responseTime"),
                payload.get("resultCode"),
                payload.get("transId")
        );

        String generatedSignature = hmacSHA256(secretKey, rawHash);
        if (!generatedSignature.equals(payload.get("signature"))) {
            booking.setBookingStatus("FAILED");
            bookingRepository.save(booking);
            return;
        }

        boolean isPaymentSuccess = "0".equals(payload.get("resultCode"));
        booking.setBookingStatus(isPaymentSuccess ? "PAID" : "FAILED");
        bookingRepository.save(booking);

        if (!isPaymentSuccess) return;

        // Cập nhật số lượng vé
        List<Ticket> updatedTickets = booking.getBookingDetails().stream().map(detail -> {
            Ticket ticket = detail.getTicket();
            ticket.setQuantity(ticket.getQuantity() - detail.getQuantity());
            return ticket;
        }).collect(Collectors.toList());

        ticketRepository.saveAll(updatedTickets);

        // Lưu giao dịch
        Transaction transaction = new Transaction();
        transaction.setBooking(booking);
        transaction.setTransactionInfo(payload.get("extraData"));
        transaction.setMessage(payload.get("message"));
        transaction.setPaymentMethod("MOMO");
        transaction.setTransactionDate(payload.get("responseTime"));
        transaction.setTransactionAmount(Double.parseDouble(payload.get("amount")));
        transaction.setTransactionStatus("SUCCESSFULLY");
        transaction.setReferenceCode(payload.get("transId"));
        transactionRepository.save(transaction);
        List<BookingDetails> bkdts = booking.getBookingDetails();
        List<CheckInTicket> tickets = new ArrayList<>();
        for (BookingDetails bkdt : bkdts) {
            for (int i = 0; i < bkdt.getQuantity(); i++) {
                CheckInTicket ticket = new CheckInTicket();
                ticket.setStatus(false);
                ticket.setTicketCode(UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
                ticket.setBookingDetails(bkdt);
                tickets.add(ticket);
            }
        }
        checkInTicketRepository.saveAll(tickets);
        System.out.println("Thanh toán thành công");
    }


}
