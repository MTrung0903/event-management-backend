package hcmute.fit.event_management.repository;

import hcmute.fit.event_management.entity.User;
import hcmute.fit.event_management.entity.UserRole;
import hcmute.fit.event_management.entity.keys.AccountRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, AccountRoleId> {

    void deleteAllByUser(User user);

}
