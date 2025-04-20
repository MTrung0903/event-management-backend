package hcmute.fit.event_management.service;

import hcmute.fit.event_management.dto.UserDTO;
import hcmute.fit.event_management.entity.User;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    List<User> findAll();
    void delete(User entity);
    <S extends User> S save(S entity);

    UserDTO DTO(User user);
    List<UserDTO> getAllAccountDTOs();

}