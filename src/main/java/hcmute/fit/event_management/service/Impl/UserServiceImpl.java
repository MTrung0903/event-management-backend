package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.dto.UserDTO;

import hcmute.fit.event_management.entity.User;
import hcmute.fit.event_management.entity.UserRole;
import hcmute.fit.event_management.entity.Role;
import hcmute.fit.event_management.entity.keys.AccountRoleId;
import hcmute.fit.event_management.repository.UserRepository;
import hcmute.fit.event_management.repository.UserRoleRepository;
import hcmute.fit.event_management.repository.RoleRepository;
import hcmute.fit.event_management.service.IUserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    Logger logger = LoggerFactory.getLogger(this.getClass());
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findAllById(Iterable<Integer> integers) {
        return userRepository.findAllById(integers);
    }

    @Override
    public long count() {
        return userRepository.count();
    }

    @Override
    public void delete(User entity) {
        userRepository.delete(entity);
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> integers) {
        userRepository.deleteAllById(integers);
    }

    @Override
    public <S extends User> S save(S entity) {
        return userRepository.save(entity);
    }

    @Override
    public List<User> findAll(Sort sort) {
        return userRepository.findAll(sort);
    }

    @Override
    public <S extends User> Optional<S> findOne(Example<S> example) {
        return userRepository.findOne(example);
    }

    @Override
    public UserDTO DTO(User user) {
        List<String> roles = new ArrayList<>();
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        for (UserRole userRole : user.getListUserRoles()) {
            roles.add(userRole.getRole().getName());
        }
        userDTO.setRoles(roles);
        return userDTO;
    }
    @Override
    public List<UserDTO> getAllAccountDTOs() {
        List<User> listUsers = findAll();
        return listUsers.stream()
                .map(this::DTO)
                .toList();
    }
    @Transactional
    public int addOrUpdateAccount(boolean isUpdate, UserDTO userDTO) {
        Optional<User> existingAccount = findbyEmail(userDTO.getEmail());

        if (!isUpdate) {
            if (existingAccount.isPresent()) {
                logger.warn("Account creation failed: Account already exists");
                return 409;
            }
            return createAccount(userDTO);
        }

        if (existingAccount.isEmpty()) {
            logger.warn("Account update failed: Account not found");
            return 404;
        }
        return updateAccount(existingAccount.get(), userDTO);
    }

    private int createAccount(UserDTO userDTO) {
        User user = new User();
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        BeanUtils.copyProperties(userDTO, user);
        user.setActive(userDTO.isActive());
        save(user);

        createAccountRoles(userDTO, user);

        logger.info("Account created successfully");
        return 201;
    }

    private int updateAccount(User existingUser, UserDTO userDTO) {
        userDTO.setUserId(existingUser.getUserId());
        userRoleRepository.deleteAllByUser(existingUser);

        User updatedUser = new User();
        BeanUtils.copyProperties(userDTO, updatedUser);
        updatedUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        updatedUser.setActive(userDTO.isActive());
        save(updatedUser);

        createAccountRoles(userDTO, updatedUser);

        logger.info("Account updated successfully");
        return 200;
    }

    private void createAccountRoles(UserDTO userDTO, User user) {
        List<UserRole> userRoles = new ArrayList<>();
        for (String role : userDTO.getRoles()) {
            Optional<Role> roleOptional = roleRepository.findByName(role);
            roleOptional.ifPresent(roleEntity -> {
                AccountRoleId accountRoleId = new AccountRoleId(user.getUserId(), roleEntity.getRoleID());
                UserRole userRole = new UserRole(accountRoleId, user, new Role(roleEntity.getRoleID(), roleEntity.getName()));
                userRoles.add(userRole);
            });
        }
        userRoleRepository.saveAll(userRoles);
    }

    @Override
    public Optional<User> findbyEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
