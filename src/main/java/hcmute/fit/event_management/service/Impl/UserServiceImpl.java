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
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import payload.Response;

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

    @PostConstruct
    @Transactional
    public void initDefaultAdmin() {
        Optional<Role> adminRole = roleRepository.findByName("ROLE_ADMIN");
        if (adminRole.isEmpty()){
            Role role = new Role();
            role.setName("ROLE_ADMIN");
            roleRepository.save(role);
            adminRole = Optional.of(role);
            logger.info("Created ROLE_ADMIN");
        }
        Optional<User> adminUser = userRepository.findByEmail("admin@gmail.com");
        if(adminUser.isEmpty()){
            User user = new User();
            user.setEmail("admin@gmail.com");
            user.setPassword(passwordEncoder.encode("admin"));
            user.setActive(true);
            userRepository.save(user);

            AccountRoleId accountRoleId = new AccountRoleId(user.getUserId(), adminRole.get().getRoleId());
            UserRole userRole = new UserRole(accountRoleId, user, adminRole.get());
            userRoleRepository.save(userRole);
            logger.info("Created default admin account: admin@gmail.com");
        } else {
            logger.info("Admin account already exists: admin@gmail.com");
        }
    }
    @Transactional
    public ResponseEntity<Response> register(UserDTO userDTO) {
        Optional<User> existingUser = userRepository.findByEmail(userDTO.getEmail());
        if (existingUser.isPresent()) {
            logger.warn("Registration failed: Email {} already exists", userDTO.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Response(409, "Conflict", "Email already exists"));
        }

        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setActive(true);

        Optional<Role> role = roleRepository.findByName("ROLE_USER");
        if (role.isEmpty()) {
            logger.error("Registration failed: ROLE_USER not found in database");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(500, "Error", "ROLE_USER not configured"));
        }

        // Lưu User trước
        User userSaved = userRepository.save(user);

        // Tạo UserRole với userSaved
        AccountRoleId accountRoleId = new AccountRoleId(userSaved.getUserId(),role.get().getRoleId());
        UserRole userRoleEntity = new UserRole();
        userRoleEntity.setId(accountRoleId);
        userRoleEntity.setUser(userSaved);
        userRoleEntity.setRole(role.get());
        userRoleRepository.save(userRoleEntity);

        logger.info("User registered successfully with email: {}", userDTO.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response(201, "Success", "User registered successfully"));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }


    @Override
    public void delete(User entity) {
        userRepository.delete(entity);
    }



    @Override
    public <S extends User> S save(S entity) {
        return userRepository.save(entity);
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
        Optional<User> existingAccount = userRepository.findByEmail(userDTO.getEmail());

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
                AccountRoleId accountRoleId = new AccountRoleId(user.getUserId(), roleEntity.getRoleId());
                UserRole userRole = new UserRole(accountRoleId, user, new Role(roleEntity.getRoleId(), roleEntity.getName()));
                userRoles.add(userRole);
            });
        }
        userRoleRepository.saveAll(userRoles);
    }



}
