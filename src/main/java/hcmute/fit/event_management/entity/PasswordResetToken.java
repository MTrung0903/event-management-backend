package hcmute.fit.event_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "password_reset_token")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetToken {
    @Id
    @Column(name = "account_id")
    private int accountID;
    @Column(name = "token")
    private String token;

    @OneToOne
    @MapsId
    @JoinColumn(name = "account_id")
    private Account account;

}
