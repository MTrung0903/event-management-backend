package hcmute.fit.event_management.service;

import hcmute.fit.event_management.dto.RoleDTO;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import payload.Response;

import java.util.List;

public interface IRoleService {
    @Transactional
    ResponseEntity<Response> createRole(RoleDTO roleDTO);

    @Transactional
    ResponseEntity<Response> assignPermissionsToRole(int roleId, List<String> permissionNames);

    List<RoleDTO> getAllRoles();
}
