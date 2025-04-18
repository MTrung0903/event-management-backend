package hcmute.fit.event_management.service;

import hcmute.fit.event_management.dto.UserDTO;
import hcmute.fit.event_management.entity.User;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface IUserService {


    List<User> findAll();
    List<User> findAllById(Iterable<Integer> integers);
    long count();
    void delete(User entity);
    void deleteAll();
    void deleteAllById(Iterable<? extends Integer> integers);
    <S extends User> S save(S entity);
    List<User> findAll(Sort sort);
    <S extends User> Optional<S> findOne(Example<S> example);
    UserDTO DTO(User user);
    List<UserDTO> getAllAccountDTOs();
    Optional<User> findbyEmail(String email);

    Optional<User> findById(Integer integer);
}
