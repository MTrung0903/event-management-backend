package hcmute.fit.event_management.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private String transactionId;
    private String transactionDate;
    private String transactionAmount;
    private String paymentMethod;
    private String transactionStatus;
    private String payerAccount;
    private String receiverAccount;
    private String paymentGateway;
    private String referenceCode;
    private String bookingId;

}
