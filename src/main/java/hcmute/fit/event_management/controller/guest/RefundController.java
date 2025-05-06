package hcmute.fit.event_management.controller.guest;

import hcmute.fit.event_management.dto.TransactionDTO;
import hcmute.fit.event_management.entity.Event;
import hcmute.fit.event_management.entity.Transaction;
import hcmute.fit.event_management.service.ITransactionService;
import hcmute.fit.event_management.service.Impl.VNPAYService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/refund")
public class RefundController {
    @Autowired
    ITransactionService transactionService;
    @Autowired
    private VNPAYService vnPayService;

    @GetMapping("/valid/{refCode}")
    public ResponseEntity<?> valid(@PathVariable("refCode") String refCode) {
        Transaction transaction = transactionService.findByOrderCode(refCode).orElse(new Transaction());
        if (transaction.getTransactionStatus().equals("REFUNDED")) {
            return new ResponseEntity<>("This transaction has been refunded", HttpStatus.OK);
        }

        Event event = transaction.getBooking().getEvent();
        if (event.getRefunds().equals("no")) {
            return new ResponseEntity<>("This transaction does not allow refund", HttpStatus.OK);
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = event.getEventStart();
        int validityDays = event.getValidityDays();
        LocalDateTime deadline = startTime.minusDays(validityDays);
        if (!now.isBefore(deadline)) {
            return new ResponseEntity<>("Over permitted time limit for refund", HttpStatus.OK);
        }
        return new ResponseEntity<>("true", HttpStatus.OK);
    }
    @PostMapping("/{refCode}")
    public ResponseEntity<?> refund(HttpServletRequest request, @PathVariable("refCode") String refCode) throws Exception {
        Transaction transaction = transactionService.findByOrderCode(refCode).orElse(new Transaction());
        TransactionDTO transactionDTO = new TransactionDTO();
        BeanUtils.copyProperties(transaction, transactionDTO);
        System.out.println(transactionDTO);
        return vnPayService.refund(request, transaction);
    }
}
