package hcmute.fit.event_management.controller.guest;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import hcmute.fit.event_management.config.VNPAYConfig;

import hcmute.fit.event_management.dto.CheckoutDTO;
import hcmute.fit.event_management.dto.TransactionDTO;

import hcmute.fit.event_management.entity.*;

import hcmute.fit.event_management.repository.*;
import hcmute.fit.event_management.service.*;

import hcmute.fit.event_management.service.Impl.EmailServiceImpl;
import hcmute.fit.event_management.service.Impl.MomoService;
import hcmute.fit.event_management.service.Impl.VNPAYService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payload.Response;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/payment")
public class CheckoutController {
    @Autowired
    ITransactionService transactionService;

    @Autowired
    private VNPAYService vnPayService;

    @Autowired
    private MomoService momoService;
    @Autowired
    private EmailServiceImpl emailService;
    @Autowired
    IEventService eventService;
    @Autowired
    IBookingService bookingService;
    @Autowired
    IBookingDetailsService bookingDetailsService;
    @Autowired
    ITicketService ticketService;
    @Autowired
    ICheckInTicketService checkInTicketService;
    @Autowired
    IUserService userService;
    @Autowired
    IBuyFreeTicket buyFreeTicket;
    @PostMapping("/create-vnpay")
    public ResponseEntity<?> createPaymentWithVNPAY(HttpServletRequest request, @RequestBody CheckoutDTO checkoutDTO) {
        try {
            String paymentUrl = vnPayService.createPaymentUrl(request, checkoutDTO);
            return ResponseEntity.ok(Collections.singletonMap("paymentUrl", paymentUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create payment");
        }
    }

    @GetMapping("/vnpay-ipn")
    public void vnpayIPN(HttpServletRequest request) throws Exception {
        vnPayService.ipn(request);
    }

    @GetMapping("/vnpay-return")
    public void vnpayReturn(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.sendRedirect("http://localhost:3000/payment-result?orderCode=" + request.getParameter("vnp_TxnRef"));
    }

    @PostMapping("/create-momo")
    public ResponseEntity<?> createPaymentWithMomo(@RequestBody CheckoutDTO checkoutDTO) {
        return momoService.createQRCode(checkoutDTO);
    }

    @PostMapping("/momo-ipn")
    public void momoIPN(@RequestBody Map<String, String> payload) {
        System.out.println(payload);
        momoService.ipn(payload);
    }

    @GetMapping("/momo-return")
    public void momoReturn(@RequestParam Map<String, String> params, HttpServletResponse response) throws IOException {
        response.sendRedirect("http://localhost:3000/payment-result?orderCode=" + params.get("orderCode"));
    }

    @GetMapping("/status/{orderCode}")
    public ResponseEntity<?> checkStatus(@PathVariable("orderCode") String orderCode) {
        Optional<Transaction> transactionOpt = transactionService.findByOrderCode(orderCode);
        TransactionDTO transactionDTO = new TransactionDTO();
        if (transactionOpt.isPresent()) {
            Transaction transaction = transactionOpt.get();
            BeanUtils.copyProperties(transaction, transactionDTO);
        }
        return new ResponseEntity<>(transactionDTO, HttpStatus.OK);
    }

    @PostMapping("/free-ticket")
    public ResponseEntity<?> buyFreeTicket(@RequestBody CheckoutDTO checkoutDTO) throws IOException {
        String bookingCode = String.valueOf(System.currentTimeMillis());
        buyFreeTicket.buyFreeTicket(checkoutDTO,bookingCode);
        Response response = new Response(1, "Payment successfully", bookingCode);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
