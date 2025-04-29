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
    public ResponseEntity<?> createQRCode(CheckoutDTO checkoutDTO) {
        String partnerCode = momoConfig.getPartnerCode();
        String accessKey = momoConfig.getAccessKey();
        String redirectUrl = momoConfig.getRedirectUrl();
        String ipnUrl = momoConfig.getIpnUrl();
        String requestType = momoConfig.getRequestType();
        String orderId = UUID.randomUUID().toString();
        String requestId = UUID.randomUUID().toString();
        String extraData = checkoutDTO.getOrderInfo();
        String orderInfo = checkoutDTO.getOrderInfo();
        String rawHash = "accessKey=" + accessKey
                + "&amount=" + checkoutDTO.getAmount()
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
                .amount(checkoutDTO.getAmount())
                .extraData(extraData)
                .signature(signature)
                .lang("vi")
                .build();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String momoCreateDate = formatter.format(calendar.getTime());
        calendar.add(Calendar.HOUR, 1);
        calendar.add(Calendar.MINUTE, 40);
        String momoExpireDate = formatter.format(calendar.getTime());
        try {
            Booking booking = new Booking();
            booking.setTotalPrice(checkoutDTO.getAmount());
            booking.setCreateDate(formatter.parse(momoCreateDate));
            booking.setExpireDate(formatter.parse(momoExpireDate));

            booking.setBookingCode(orderId);
            booking.setBookingMethod("Momo");
            booking.setBookingStatus("Pending");
            booking.setUser(userRepository.findById(Integer.valueOf(checkoutDTO.getUserId())).orElse(new User()));
            bookingRepository.saveAndFlush(booking);
            for (Integer ticketId : checkoutDTO.getTickets().keySet()) {
                BookingDetails bkdt = new BookingDetails();
                bkdt.setBooking(booking);
                bkdt.setTicket(ticketRepository.findById(ticketId).orElse(new Ticket()));
                bkdt.setQuantity(checkoutDTO.getTickets().get(ticketId));
                bkdt.setPrice(checkoutDTO.getAmount());
                bookingDetailsRepository.save(bkdt);
            }
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
        String orderType = payload.get("orderType");
        String payType = payload.get("payType");
        String responseTime = payload.get("responseTime");
        String rawHash = "accessKey=" + accessKey
                + "&amount=" + amount
                + "&extraData=" + extraData
                + "&message=" + message
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&orderType=" + orderType
                + "&partnerCode=" + partnerCode
                + "&payType=" + payType
                + "&requestId=" + requestId
                + "&responseTime=" + responseTime
                + "&resultCode=" + resultCode
                + "&transId=" + transId;

        String generatedSignature = hmacSHA256(momoConfig.getSecretKey(), rawHash);
        Optional<Booking> optionalBooking = bookingRepository.findByBookingCode(orderId);
        Booking booking = optionalBooking.orElse(new Booking());
        if (!signature.equals(generatedSignature)) {
            booking.setBookingStatus("FAILED");
            bookingRepository.save(booking);
            return;
        }
        if (booking.getBookingStatus().equals("PAID")) {
            return;
        }
        if (optionalBooking.isEmpty()) {
            return;
        }
        if ("0".equals(resultCode)) {
            List<BookingDetails> bookingDetails = bookingDetailsRepository.findByBookingId(booking.getBookingId());
            List<Ticket> ticketsToUpdate = new ArrayList<>();
            for (BookingDetails details : bookingDetails) {
                Ticket ticket = details.getTicket();
                ticket.setQuantity(ticket.getQuantity() - details.getQuantity());
                ticketsToUpdate.add(ticket);
            }
            ticketRepository.saveAll(ticketsToUpdate);
            booking.setBookingStatus("PAID");
            bookingRepository.save(booking);
            Transaction transaction = new Transaction();
            transaction.setBooking(booking);
            transaction.setTransactionInfo(extraData);
            transaction.setMessage(message);
            transaction.setPaymentMethod("MOMO");
            transaction.setTransactionDate(responseTime);
            transaction.setTransactionAmount(Double.parseDouble(amount));
            transaction.setTransactionStatus("SUCCESSFULLY");
            transaction.setReferenceCode(transId);
            transactionRepository.save(transaction);
            System.out.println("Thanh toan thanh cong");
        } else {
            booking.setBookingStatus("FAILED");
            bookingRepository.save(booking);
        }
    }

}
