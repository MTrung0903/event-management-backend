package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.dto.PermissionDTO;
import hcmute.fit.event_management.dto.RoleDTO;
import hcmute.fit.event_management.entity.Permission;
import hcmute.fit.event_management.entity.Role;
import hcmute.fit.event_management.repository.PermissionRepository;
import hcmute.fit.event_management.repository.RoleRepository;
import hcmute.fit.event_management.service.IRoleService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import payload.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RolerServiceImpl implements IRoleService {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PermissionRepository permissionRepository;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Transactional
    @Override
    public ResponseEntity<Response> createRole(RoleDTO roleDTO) {
        //Kiểm tra xem role đã tồn tại chưa
        Optional<Role> existingRole = roleRepository.findByName(roleDTO.getName());
        if (existingRole.isPresent()) {
            logger.warn("Role creation failed: Name {} already exists", roleDTO.getName());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Response(409, "Conflict", "Role name already exists"));
        }

        // tạo role mới nếu role chưa tồn tại
        Role role = new Role();
        role.setName(roleDTO.getName());
        roleRepository.save(role);

        logger.info("Role {} created successfully", roleDTO.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response(201, "Success", "Role created successfully"));
    }

    @Transactional
    @Override
    public ResponseEntity<Response> assignPermissionsToRole(int roleId, List<String> permissionNames) {
        //Kiểm tra xem role đã tồn tại chưa
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (roleOpt.isEmpty()) {
            logger.warn("Assign permissions failed: Role ID {} not found", roleId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(404, "Not Found", "Role not found"));
        }
        Role role = roleOpt.get();

        List<Permission> permissions = new ArrayList<>();
        for (String name : permissionNames) {
            Optional<Permission> permOpt = permissionRepository.findByName(name);
            if (permOpt.isEmpty()) {
                logger.warn("Permission {} not found", name);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response(404, "Not Found", "Permission " + name + " not found"));
            }
            permissions.add(permOpt.get());
        }

        role.setPermissions(permissions);
        roleRepository.save(role);

        logger.info("Assigned permissions {} to role {}", permissionNames, role.getName());
        return ResponseEntity.ok(new Response(200, "Success", "Permissions assigned successfully"));
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        List<RoleDTO> roleDTOs = new ArrayList<>();
        for (Role role : roles) {
            RoleDTO roleDTO = new RoleDTO();
            roleDTO.setRoleID(role.getRoleId());
            roleDTO.setName(role.getName());

            roleDTO.setPermissions(convertToDTO(role.getPermissions()));
            roleDTOs.add(roleDTO);

        }
        return roleDTOs;
    }
   public List<PermissionDTO> convertToDTO(List<Permission> permissions) {
        List<PermissionDTO> permissionDTOs = new ArrayList<>();
        for (Permission permission : permissions) {
            PermissionDTO permissionDTO = new PermissionDTO();
            permissionDTO.setName(permission.getName());
            permissionDTO.setDescription(permission.getDescription());
            permissionDTOs.add(permissionDTO);
        }
        return permissionDTOs;
   }
}
