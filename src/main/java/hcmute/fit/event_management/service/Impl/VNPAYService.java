package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.config.VNPAYConfig;
import hcmute.fit.event_management.dto.CheckoutDTO;
import hcmute.fit.event_management.dto.TicketDTO;
import hcmute.fit.event_management.entity.*;

import hcmute.fit.event_management.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import static hcmute.fit.event_management.util.PaymentUtil.*;

@Service
public class VNPAYService {

    @Autowired
    VNPAYConfig vnPayConfig;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    BookingDetailsRepository bookingDetailsRepository;

    @Autowired
    UserRepository userRepository;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    TransactionRepository transactionRepository;

    public String createPaymentUrl(HttpServletRequest req, CheckoutDTO checkoutDTO) throws Exception  {

        String vnp_TxnRef = String.valueOf(System.currentTimeMillis());
        String vnp_OrderInfo = "Thanh toan don hang:" + getRandomNumber(8);
        String vnp_OrderType = "other";
        String vnp_Amount = String.valueOf(checkoutDTO.getAmount() * 100L); // Nhân 100 để ra đơn vị tiền nhỏ nhất (VND)
        String vnp_Locale = "vn";
        String vnp_ReturnUrl = vnPayConfig.getReturnUrl();
        String vnp_IpAddr = getIpAddress(req);
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        vnp_Params.put("vnp_Amount", vnp_Amount);
        vnp_Params.put("vnp_CurrCode", "VND");
        if (checkoutDTO.getBankCode() != null && !checkoutDTO.getBankCode().isEmpty()) {
            vnp_Params.put("vnp_BankCode", checkoutDTO.getBankCode());
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", vnp_OrderType);
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_Locale", vnp_Locale);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(calendar.getTime());
        vnp_Params.put("vnp_CreateDate", vnpCreateDate);
        calendar.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(calendar.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        String queryUrl = getPaymentURL(vnp_Params, true);
        String hashData = getPaymentURL(vnp_Params, false);
        String vnpSecureHash = hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        // Lưu vào booking với status pending
        try {
            Booking booking = new Booking();
            booking.setBookingDate(formatter.parse(vnpCreateDate));
            booking.setBookingCode(vnp_TxnRef);
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
        return vnPayConfig.getPayUrl() + "?" + queryUrl;
    }
    public void ipn(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII);
            String fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                fields.put(fieldName, fieldValue);
            }
        }
        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");
        String signValue = hashAllFields(vnPayConfig.getSecretKey(),fields);
        String txnRef = request.getParameter("vnp_TxnRef"); // booking_code của bạn
        Optional<Booking> optionalBooking = bookingRepository.findByBookingCode(txnRef);
        Booking booking = optionalBooking.orElse(new Booking());
        if (signValue.equals(vnp_SecureHash)) {
            String responseCode = request.getParameter("vnp_ResponseCode");

            if (booking.getBookingStatus().equals("PAID")) {
                return;
            }
            if (optionalBooking.isEmpty()) {
                return;
            }
            if ("00".equals(responseCode)) {
                booking.setBookingStatus("PAID");
                bookingRepository.save(booking);
                Transaction transaction = new Transaction();
                transaction.setBooking(booking);
                transaction.setPaymentMethod("VNPAY");
                transaction.setTransactionDate(request.getParameter("vnp_CreateDate"));
                transaction.setTransactionAmount(Double.parseDouble(request.getParameter("vnp_Amount")));
                transaction.setTransactionStatus("Paid");
                transaction.setReferenceCode(request.getParameter("vnp_TxnRef"));
                transactionRepository.save(transaction);
            } else {
                booking.setBookingStatus("FAIL");
                bookingRepository.save(booking);
            }
        } else {
            booking.setBookingStatus("FAIL");
            bookingRepository.save(booking);
        }
    }
}

