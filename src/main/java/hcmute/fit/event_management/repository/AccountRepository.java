package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository  extends JpaRepository<Account, Integer> {
    Optional<Account> findByEmail(String email);
}
