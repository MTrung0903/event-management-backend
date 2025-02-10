package hcmute.fit.event_management.entity;

import hcmute.fit.event_management.entity.keys.AccountRoleId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "account_role")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountRole {
    @EmbeddedId
    private AccountRoleId id;

    @ManyToOne
    @MapsId("accountID")
    @JoinColumn(name = "account_id", referencedColumnName = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @MapsId("roleID")
    @JoinColumn(name = "role_id", referencedColumnName = "role_id", nullable = false)
    private Role role;
}
