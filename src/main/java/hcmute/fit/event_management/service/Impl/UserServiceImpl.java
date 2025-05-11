package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.dto.OrganizerDTO;
import hcmute.fit.event_management.dto.PermissionDTO;
import hcmute.fit.event_management.dto.RoleDTO;
import hcmute.fit.event_management.dto.UserDTO;

import hcmute.fit.event_management.entity.*;
import hcmute.fit.event_management.entity.keys.AccountRoleId;
import hcmute.fit.event_management.repository.*;
import hcmute.fit.event_management.service.IUserService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import payload.Response;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


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

    @Autowired
    private OrganizerRepository organizerRepository;


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
            user.setGender("");
            user.setFullName("Admin");

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
    @Override
    public ResponseEntity<Response> register(UserDTO userDTO) {

        // Kiểm tra email đã tồn tại
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            logger.warn("Registration failed: Email {} already exists", userDTO.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Response(409, "Conflict", "Email already exists"));
        }
        // Xác định vai trò dựa trên organizer
        String roleName = userDTO.getOrganizer() == null ? "ROLE_ATTENDEE" : "ROLE_ORGANIZER";
        Optional<Role> role = roleRepository.findByName(roleName);
        if (role.isEmpty()) {
            logger.error("Registration failed: {} not found in database", roleName);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(500, "Error", roleName + " not configured"));
        }
        // Tạo user
        User user = new User();
        user.setFullName(userDTO.getFullName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setGender(userDTO.getGender());
        user.setBirthday(userDTO.getBirthday());
        user.setAddress(userDTO.getAddress());
        user.setActive(true);

        // Lưu user
        User savedUser = userRepository.save(user);



        // Gán vai trò
        AccountRoleId accountRoleId = new AccountRoleId(savedUser.getUserId(), role.get().getRoleId());
        UserRole userRole = new UserRole();
        userRole.setId(accountRoleId);
        userRole.setUser(savedUser);
        userRole.setRole(role.get());
        userRoleRepository.save(userRole);

        // Nếu là ROLE_ORGANIZER, lưu thông tin Organizer
        if (userDTO.getOrganizer() != null) {
            Organizer organizer = new Organizer();
            organizer.setOrganizerName(userDTO.getOrganizer().getOrganizerName());
            organizer.setOrganizerAddress(userDTO.getOrganizer().getOrganizerAddress());
            organizer.setOrganizerWebsite(userDTO.getOrganizer().getOrganizerWebsite());
            organizer.setOrganizerPhone(userDTO.getOrganizer().getOrganizerPhone());
            organizer.setUser(savedUser);
            organizerRepository.save(organizer);
        }

        logger.info("User registered successfully with email: {}", userDTO.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response(201, "Success", "User registered successfully"));
    }

    @Override
    public ResponseEntity<Response> saveChangeInfor(UserDTO userChange) {

        if (userChange.getEmail() == null || userChange.getEmail().isEmpty()) {
            logger.error("Invalid email provided");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response(400, "Bad Request", "Email is required"));
        }


        Optional<User> userOpt = userRepository.findByEmail(userChange.getEmail());
        if (!userOpt.isPresent()) {
            logger.error("User {} not found", userChange.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(404, "Not Found", "User not found"));
        }

        User user = userOpt.get();

        // Kiểm tra email mới (nếu thay đổi)
        if (!userChange.getEmail().equals(user.getEmail())) {
            Optional<User> existingUserWithNewEmail = userRepository.findByEmail(userChange.getEmail());
            if (existingUserWithNewEmail.isPresent()) {
                logger.error("Email {} already exists", userChange.getEmail());
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new Response(409, "Conflict", "Email already exists"));
            }
            user.setEmail(userChange.getEmail());
        }


        if (userChange.getFullName() != null && !userChange.getFullName().isEmpty()) {
            user.setFullName(userChange.getFullName());
        }
        if (userChange.getPassword() != null && !userChange.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userChange.getPassword()));
        }
        if (userChange.getGender() != null) {
            user.setGender(userChange.getGender());
        }
        if (userChange.getBirthday() != null) {

            if (userChange.getBirthday().isAfter(LocalDate.now())) {
                logger.error("Invalid birthday: {}", userChange.getBirthday());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Response(400, "Bad Request", "Birthday cannot be in the future"));
            }
            user.setBirthday(userChange.getBirthday());
        }
        if (userChange.getAddress() != null) {
            user.setAddress(userChange.getAddress());
        }


        if (userChange.getOrganizer() != null) {
            OrganizerDTO organizerDTO = userChange.getOrganizer();
            if (organizerDTO.getOrganizerName() == null || organizerDTO.getOrganizerName().isEmpty()) {
                logger.error("Invalid organizer name provided");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Response(400, "Bad Request", "Organizer name is required"));
            }

            Organizer organizer = user.getOrganizer();

            // Cập nhật thông tin Organizer
            organizer.setOrganizerName(organizerDTO.getOrganizerName());
            if (organizerDTO.getOrganizerAddress() != null) {
                organizer.setOrganizerAddress(organizerDTO.getOrganizerAddress());
            }
            if (organizerDTO.getOrganizerWebsite() != null) {
                organizer.setOrganizerWebsite(organizerDTO.getOrganizerWebsite());
            }
            if (organizerDTO.getOrganizerPhone() != null) {
                organizer.setOrganizerPhone(organizerDTO.getOrganizerPhone());
            }
            if (organizerDTO.getOrganizerDesc() != null) {
                organizer.setOrganizerDesc(organizerDTO.getOrganizerDesc());
            }
            organizerRepository.save(organizer);
            user.setOrganizer(organizer);
        }

        // Lưu user
        userRepository.save(user);

        logger.info("User information updated successfully for email: {}", userChange.getEmail());
        return ResponseEntity.ok(new Response(200, "Success", "User information updated successfully"));
    }

    @Override
    public ResponseEntity<Response> AddMoreRoleForUser(String email, String roleName) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            logger.error("User  {} not found", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(404, "Not Found", "User not found"));
        }

        Optional<Role> roleOpt = roleRepository.findByName(roleName);
        if (!roleOpt.isPresent()) {
            logger.error("Role {} not found", roleName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(404, "Not Found", "Role not found"));
        }

        User user = userOpt.get();
        Role role = roleOpt.get();



        // Thêm role mới
        AccountRoleId accountRoleId = new AccountRoleId(user.getUserId(), role.getRoleId());
        UserRole userRole = new UserRole(accountRoleId, user, role);
        userRoleRepository.save(userRole);

        logger.info("Role {} added to user {}", roleName, email);
        return ResponseEntity.ok(new Response(200, "Success", "Role added successfully"));
    }

    @Override
    public ResponseEntity<Response> deleteRoleInUser(String email, String roleName) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            logger.error("User  {} not found by", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(404, "Not Found", "User not found"));
        }

        Optional<Role> roleOpt = roleRepository.findByName(roleName);
        if (!roleOpt.isPresent()) {
            logger.error("Role with name {} not found", roleName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(404, "Not Found", "Role not found"));
        }

        User user = userOpt.get();
        Role role = roleOpt.get();

        // Tìm và xóa UserRole
        Optional<List<UserRole>> userRolesOpt = userRoleRepository.findAllByUser(user);
        if (userRolesOpt.isPresent()) {
            for (UserRole ur : userRolesOpt.get()) {
                if (ur.getRole().getRoleId() == role.getRoleId()) {
                    userRoleRepository.delete(ur);
                    logger.info("Role {} removed from user {}", roleName, email);
                    return ResponseEntity.ok(new Response(200, "Success", "Role removed successfully"));
                }
            }
        }

        logger.warn("Role {} not found for user {}", roleName, email);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Response(404, "Not Found", "Role not assigned to user"));
    }

    @Override
    public UserDTO getInfor(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            logger.error("User with email {} not found", email);
            return new UserDTO();
        }

        User user = userOpt.get();
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);

        // Map Organizer
        if (user.getOrganizer() != null) {
            OrganizerDTO organizerDTO = new OrganizerDTO();
            BeanUtils.copyProperties(user.getOrganizer(), organizerDTO);
            userDTO.setOrganizer(organizerDTO);
        }

        // Map Roles and Permissions
        List<RoleDTO> roleDTOs = new ArrayList<>();
        Optional<List<UserRole>> userRolesOpt = userRoleRepository.findAllByUser(user);

        if (userRolesOpt.isPresent()) {
            for (UserRole userRole : userRolesOpt.get()) {
                Role role = userRole.getRole();
                RoleDTO roleDTO = new RoleDTO();
                roleDTO.setRoleID(role.getRoleId());
                roleDTO.setName(role.getName());
                roleDTO.setCreatedBy(role.getCreatedBy());
                // Map Permissions
                List<PermissionDTO> permissionDTOs = new ArrayList<>();
                if (role.getPermissions() != null) {
                    for (Permission permission : role.getPermissions()) {
                        PermissionDTO permissionDTO = new PermissionDTO();

                        permissionDTO.setName(permission.getName());
                        permissionDTO.setDescription(permission.getDescription());
                        permissionDTOs.add(permissionDTO);
                    }
                }
                roleDTO.setPermissions(permissionDTOs);
                roleDTOs.add(roleDTO);
            }
        }
        userDTO.setRoles(roleDTOs);
        return userDTO;
    }

    @Override
    public UserDTO findById(int userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {

            return new UserDTO();
        }

        User user = userOpt.get();
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);


        // Map Roles and Permissions
        List<RoleDTO> roleDTOs = new ArrayList<>();
        Optional<List<UserRole>> userRolesOpt = userRoleRepository.findAllByUser(user);

        if (userRolesOpt.isPresent()) {
            for (UserRole userRole : userRolesOpt.get()) {
                Role role = userRole.getRole();
                RoleDTO roleDTO = new RoleDTO();
                roleDTO.setRoleID(role.getRoleId());
                roleDTO.setName(role.getName());
                roleDTO.setCreatedBy(role.getCreatedBy());
                // Map Permissions
                List<PermissionDTO> permissionDTOs = new ArrayList<>();
                if (role.getPermissions() != null) {
                    for (Permission permission : role.getPermissions()) {
                        PermissionDTO permissionDTO = new PermissionDTO();

                        permissionDTO.setName(permission.getName());
                        permissionDTO.setDescription(permission.getDescription());
                        permissionDTOs.add(permissionDTO);
                    }
                }
                roleDTO.setPermissions(permissionDTOs);
                roleDTOs.add(roleDTO);
            }
        }
        userDTO.setRoles(roleDTOs);
        return userDTO;
    }
    @Transactional
    @Override
    public ResponseEntity<Response> upgradeToOrganizer(String email, OrganizerDTO organizerDTO) {
        // Kiểm tra email hợp lệ
        if (email == null || email.isEmpty()) {
            logger.error("Invalid email provided");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response(400, "Bad Request", "Email is required"));
        }

        // Tìm user theo email
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            logger.error("User with email {} not found", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(404, "Not Found", "User not found"));
        }

        User user = userOpt.get();


        Optional<List<UserRole>> userRolesOpt = userRoleRepository.findAllByUser(user);


        // Kiểm tra xem user  có role ROLE_ORGANIZER không
        boolean hasOrganizerRole = false;
        if (userRolesOpt.isPresent()) {
            for (UserRole ur : userRolesOpt.get()) {
                if (ur.getRole().getName().equals("ROLE_ORGANIZER")) {
                    hasOrganizerRole = true;
                    break;
                }
            }
        }
        if (hasOrganizerRole) {
            logger.warn("User with email {} already has ROLE_ORGANIZER", email);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Response(409, "Conflict", "User already has ROLE_ORGANIZER"));
        }

        // Kiểm tra OrganizerDTO hợp lệ
        if (organizerDTO == null || organizerDTO.getOrganizerName() == null || organizerDTO.getOrganizerName().isEmpty()) {
            logger.error("Invalid organizer data provided");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response(400, "Bad Request", "Organizer name is required"));
        }

        // Thêm role ROLE_ORGANIZER
        Optional<Role> roleOpt = roleRepository.findByName("ROLE_ORGANIZER");
        if (!roleOpt.isPresent()) {
            logger.error("ROLE_ORGANIZER not found in database");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(500, "Error", "ROLE_ORGANIZER not configured"));
        }
        Role organizerRole = roleOpt.get();
        AccountRoleId accountRoleId = new AccountRoleId(user.getUserId(), organizerRole.getRoleId());
        UserRole userRole = new UserRole(accountRoleId, user, organizerRole);
        userRoleRepository.save(userRole);

        // Tạo mới Organizer
        if (user.getOrganizer() != null) {
            logger.warn("User with email {} already has an Organizer", email);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Response(409, "Conflict", "User already has an Organizer"));
        }
        Organizer organizer = new Organizer();
        organizer.setOrganizerName(organizerDTO.getOrganizerName());
        organizer.setOrganizerAddress(organizerDTO.getOrganizerAddress());
        organizer.setOrganizerWebsite(organizerDTO.getOrganizerWebsite());
        organizer.setOrganizerPhone(organizerDTO.getOrganizerPhone());
        organizer.setOrganizerDesc(organizerDTO.getOrganizerDesc());
        organizer.setRegistrationDate(LocalDate.now());
        organizer.setUser(user);
        organizerRepository.save(organizer);
        user.setOrganizer(organizer);

        // Lưu user
        userRepository.save(user);

        logger.info("User with email {} upgraded to ROLE_ORGANIZER successfully", email);
        return ResponseEntity.ok(new Response(200, "Success", "User upgraded to ROLE_ORGANIZER successfully"));
    }

    @Override
    public ResponseEntity<Response> deleteUser(String email) {
        if (email == null || email.isEmpty()) {
            logger.error("Invalid email provided");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response(400, "Bad Request", "Email is required"));
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            logger.error("User with email {} not found", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(404, "Not Found", "User not found"));
        }

        User user = userOpt.get();

        // Kiểm tra nếu user là admin mặc định
        if (user.getEmail().equals("admin@gmail.com")) {
            logger.warn("Cannot delete default admin account: {}", email);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response(400, "Bad Request", "Cannot delete default admin account"));
        }

        // Xóa các liên kết
        Optional<List<UserRole>> userRolesOpt = userRoleRepository.findAllByUser(user);
        if (userRolesOpt.isPresent()) {
            userRoleRepository.deleteAll(userRolesOpt.get());
        }

        if (user.getOrganizer() != null) {
            organizerRepository.delete(user.getOrganizer());
        }

        // Xóa user
        userRepository.delete(user);

        logger.info("User with email {} deleted successfully", email);
        return ResponseEntity.ok(new Response(200, "Success", "User deleted successfully"));
    }
    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = new ArrayList<>();

        for (User user : users) {
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO);

            // Map Organizer
            if (user.getOrganizer() != null) {
                OrganizerDTO organizerDTO = new OrganizerDTO();
                BeanUtils.copyProperties(user.getOrganizer(), organizerDTO);
                userDTO.setOrganizer(organizerDTO);
            }

            // Map Roles and Permissions
            List<RoleDTO> roleDTOs = new ArrayList<>();
            Optional<List<UserRole>> userRolesOpt = userRoleRepository.findAllByUser(user);

            if (userRolesOpt.isPresent()) {
                for (UserRole userRole : userRolesOpt.get()) {
                    Role role = userRole.getRole();
                    RoleDTO roleDTO = new RoleDTO();
                    roleDTO.setRoleID(role.getRoleId());
                    roleDTO.setName(role.getName());

                    List<PermissionDTO> permissionDTOs = new ArrayList<>();
                    if (role.getPermissions() != null) {
                        for (Permission permission : role.getPermissions()) {
                            PermissionDTO permissionDTO = new PermissionDTO();
                            permissionDTO.setName(permission.getName());
                            permissionDTO.setDescription(permission.getDescription());
                            permissionDTOs.add(permissionDTO);
                        }
                    }
                    roleDTO.setPermissions(permissionDTOs);
                    roleDTOs.add(roleDTO);
                }
            }
            userDTO.setRoles(roleDTOs);
            userDTOs.add(userDTO);
        }

        logger.info("Retrieved {} users", userDTOs.size());
        return userDTOs;
    }

    @Override
    public List<UserDTO> searchUserForChat(String query, int currentUserId) {
        List<UserDTO> users = getAllUsers()
                .stream()
                .filter(user ->
                        user.getUserId() != currentUserId && user.isActive() &&
                                (user.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                                        user.getFullName().toLowerCase().contains(query.toLowerCase())))
                .collect(Collectors.toList());
        return users;
    }
}