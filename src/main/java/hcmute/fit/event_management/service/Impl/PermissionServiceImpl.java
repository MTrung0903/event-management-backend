package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.dto.PermissionDTO;
import hcmute.fit.event_management.entity.Permission;
import hcmute.fit.event_management.repository.PermissionRepository;
import hcmute.fit.event_management.service.IPermissionService;
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
public class PermissionServiceImpl implements IPermissionService {
    @Autowired
    private PermissionRepository permissionRepository;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Transactional
    @Override
    public ResponseEntity<Response> createPermission(PermissionDTO permissionDTO) {
        Optional<Permission> existingPermission = permissionRepository.findByName(permissionDTO.getName());
        if (existingPermission.isPresent()) {
            logger.warn("Permission creation failed: Name {} already exists", permissionDTO.getName());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Response(409, "Conflict", "Permission name already exists"));
        }

        Permission permission = new Permission();
        permission.setName(permissionDTO.getName());
        permission.setDescription(permissionDTO.getDescription());
        permissionRepository.save(permission);

        logger.info("Permission {} created successfully", permissionDTO.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response(201, "Success", "Permission created successfully"));
    }

    @Override
    public List<PermissionDTO> getAllPermissions() {
        List<Permission> permissions = permissionRepository.findAll();
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
