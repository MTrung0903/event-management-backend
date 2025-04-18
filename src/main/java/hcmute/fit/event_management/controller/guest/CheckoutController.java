package hcmute.fit.event_management.controller.guest;
import hcmute.fit.event_management.config.VNPAYConfig;
import hcmute.fit.event_management.dto.CheckoutDTO;
import hcmute.fit.event_management.entity.Booking;
import hcmute.fit.event_management.entity.Transaction;
import hcmute.fit.event_management.service.IBookingDetailsService;
import hcmute.fit.event_management.service.IBookingService;
import hcmute.fit.event_management.service.ITransactionService;
import hcmute.fit.event_management.service.IUserService;
import hcmute.fit.event_management.service.Impl.MomoService;
import hcmute.fit.event_management.service.Impl.VNPAYService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static hcmute.fit.event_management.util.PaymentUtil.hashAllFields;
@RestController
@RequestMapping("/api/payment")
public class CheckoutController {
    @Autowired
    IBookingDetailsService bookingDetailsService;
    @Autowired
    ITransactionService transactionService;
    @Autowired
    IBookingService bookingService;
    @Autowired
    IUserService userService;

    @Autowired
    private VNPAYService vnPayService;

    @Autowired
    private VNPAYConfig vnPayConfig;
    @Autowired
    private MomoService momoService;

    @GetMapping("/create-vnpay")
    public ResponseEntity<?> createPaymentWithVNPAY(HttpServletRequest request, @RequestBody CheckoutDTO checkoutDTO) {
        try {
            String paymentUrl = vnPayService.createPaymentUrl(request, checkoutDTO);
            return ResponseEntity.ok(Collections.singletonMap("paymentUrl", paymentUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create payment");
        }
    }
    @GetMapping("/vnpay-ipn")
    public void vnpayIPN(HttpServletRequest request) {
        vnPayService.ipn(request);
    }
    @GetMapping("/vnpay-return")
    public ResponseEntity<?> vnpayReturn(HttpServletRequest request) throws Exception {
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
        if (signValue.equals(vnp_SecureHash)) {
            String responseCode = request.getParameter("vnp_ResponseCode");
            if ("00".equals(responseCode)) {
                return ResponseEntity.ok("Thanh toán thành công!");
            } else {
                return ResponseEntity.ok("Thanh toán thất bại! Mã lỗi: " + responseCode);
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sai chữ ký! Không hợp lệ.");
        }
    }
    @PostMapping("/create-momo")
    public ResponseEntity<?> createPaymentWithMomo() {
            return momoService.createQRCode(1,1,1,100000);
    }
    @PostMapping("/momo-ipn")
    public void momoIPN(@RequestBody Map<String, String> payload) {
        momoService.ipn(payload);
    }
}
