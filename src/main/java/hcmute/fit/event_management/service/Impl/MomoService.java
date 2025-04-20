package hcmute.fit.event_management.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hcmute.fit.event_management.config.MomoAPI;
import hcmute.fit.event_management.config.MomoConfig;

import hcmute.fit.event_management.dto.MomoRequestPayment;
import hcmute.fit.event_management.entity.*;
import hcmute.fit.event_management.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
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
    public ResponseEntity<?> createQRCode(int userId, int ticketId, int qtt, long amount) {
        String partnerCode = momoConfig.getPartnerCode();
        String accessKey = momoConfig.getAccessKey();
        String redirectUrl = momoConfig.getRedirectUrl();
        String ipnUrl = momoConfig.getIpnUrl();
        String requestType = momoConfig.getRequestType();
        String orderId = UUID.randomUUID().toString();
        String requestId = UUID.randomUUID().toString();
        String extraData = "Test";
        String orderInfo = "Thanh toan don hang: " + orderId;
        String rawHash = "accessKey=" + accessKey
                + "&amount=" + amount
                + "&extraData=" + extraData
                + "&ipnUrl=" + ipnUrl
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&partnerCode=" + partnerCode
                + "&redirectUrl=" + redirectUrl
                + "&requestId=" + requestId
                + "&requestType=" + requestType;
        String signature = "";
        try {
            signature = hmacSHA256(momoConfig.getSecretKey(), rawHash);
        } catch (Exception e) {
            log.error(">>>>>>>>>>>>>>>>>Loi: " + e);
            return null;
        }
        MomoRequestPayment request = MomoRequestPayment.builder()
                .partnerCode(partnerCode)
                .requestType(requestType)
                .ipnUrl(ipnUrl)
                .redirectUrl(redirectUrl)
                .orderId(orderId)
                .orderInfo(orderInfo)
                .requestId(requestId)
                .amount(amount)
                .extraData(extraData)
                .signature(signature)
                .lang("vi")
                .build();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String momoCreateDate = formatter.format(calendar.getTime());

        try {
            Booking booking = new Booking();
            booking.setBookingDate(formatter.parse(momoCreateDate));
            booking.setBookingCode(orderId);
            booking.setBookingStatus("Pending");
            booking.setUser(userRepository.findById(userId).orElse(new User()));
            bookingRepository.saveAndFlush(booking);
            BookingDetails bkdt = new BookingDetails();
            bkdt.setBooking(booking);
            bkdt.setTicket(ticketRepository.findById(ticketId).orElse(new Ticket()));
            bkdt.setQuantity(qtt);
            bkdt.setPrice(amount);
            bookingDetailsRepository.save(bkdt);
        }
        catch (Exception e) {
            System.out.println(">>>>>>>>>>>>>>"+e);
        }
        return momoAPI.createMomoQR(request);
    }
    public void ipn(Map<String, String> payload){
        String partnerCode = momoConfig.getPartnerCode();
        String accessKey = momoConfig.getAccessKey();
        String orderId = payload.get("orderId");
        String requestId = payload.get("requestId");
        String transId = payload.get("transId");
        String resultCode = payload.get("resultCode"); // "0" là thành công
        String message = payload.get("message");
        String amount = payload.get("amount");
        String signature = payload.get("signature");
        String extraData = payload.get("extraData");
        String orderInfo = payload.get("orderInfo");
        String oderType = payload.get("oderType");
        String payType = payload.get("payType");
        String responseTime = payload.get("responseTime");
        String rawHash
                = "accessKey=" + accessKey
                + "&amount=" + amount
                + "&extraData=" + extraData
                + "&message=" + message
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&oderType=" + oderType
                + "&partnerCode=" + partnerCode
                + "&payType=" + payType
                + "&requestId=" + requestId
                + "&responseTime=" + responseTime
                + "&resultCode=" + resultCode
                + "&transId=" + transId;
        String generatedSignature = hmacSHA256(momoConfig.getSecretKey(), rawHash);
//        if (!signature.equals(generatedSignature)) {
//            return;
//        }
        Optional<Booking> optionalBooking = bookingRepository.findByBookingCode(orderId);
        Booking booking = optionalBooking.orElse(new Booking());
//        if (booking.getBookingStatus().equals("PAID")) {
//            return;
//        }
//        if (optionalBooking.isEmpty()) {
//            return;
//        }

        if ("0".equals(resultCode)) {
            booking.setBookingStatus("PAID");
            bookingRepository.save(booking);
            Transaction transaction = new Transaction();
            transaction.setBooking(booking);
            transaction.setPaymentMethod("MOMO");
            transaction.setTransactionDate(responseTime);
            transaction.setTransactionAmount(Double.parseDouble(amount));
            transaction.setTransactionStatus("Paid");
            transaction.setReferenceCode(transId);
            transactionRepository.save(transaction);

        } else {
            booking.setBookingStatus("FAIL");
            bookingRepository.save(booking);
        }
    }
}
