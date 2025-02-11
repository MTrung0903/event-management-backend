package hcmute.fit.event_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private int transactionId;

    @Column(name = "transaction_date")
    private Date transactionDate;

    @Column(name = "transaction_amount")
    private double transactionAmount;

    @Column(name ="payment_method")
    private String paymentMethod;

    @Column(name = "transaction_status")
    private String transactionStatus;

    @Column(name = "payer_account")
    private String payerAccount;

    @Column(name = "receiver_account")
    private String receiverAccount;

    @Column(name = "payment_gateway")
    private String paymentGateway;

    @Column(name = " reference_code")
    private String referenceCode;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;
}
