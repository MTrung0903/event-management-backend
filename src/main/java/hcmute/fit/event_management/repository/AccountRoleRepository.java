package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.Account;
import hcmute.fit.event_management.entity.AccountRole;
import hcmute.fit.event_management.entity.keys.AccountRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRole, AccountRoleId> {

    void deleteAllByAccount(Account account);

}
