package hcmute.fit.event_management.controller.admin;

import hcmute.fit.event_management.dto.RoleDTO;
import hcmute.fit.event_management.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import payload.Response;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    @Autowired
    private IRoleService roleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> createRole(@RequestBody RoleDTO roleDTO) {
        return roleService.createRole(roleDTO);
    }

    @PostMapping("/{roleId}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> assignPermissions(
            @PathVariable int roleId,
            @RequestBody List<String> permissionNames) {
        return roleService.assignPermissionsToRole(roleId, permissionNames);
    }
    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> getRoles() {
        List<RoleDTO> roles = roleService.getAllRoles();
        Response response = new Response();
        response.setData(roles);
        return ResponseEntity.ok(response);
    }

    @PutMapping("update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> updateRole(@RequestBody RoleDTO roleDTO) {
        return roleService.updateRole(roleDTO);
    }
    @DeleteMapping("delete/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> deleteRole(@PathVariable String roleName) {
        return roleService.deleteRole(roleName);
    }
}