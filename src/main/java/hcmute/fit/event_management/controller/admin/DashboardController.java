package hcmute.fit.event_management.controller.admin;

import hcmute.fit.event_management.dto.DashboardStatsDTO;
import hcmute.fit.event_management.service.Impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import payload.Response;

@RestController
@RequestMapping("/api/v1/admin/dashboard")

public class DashboardController {
    @Autowired
    AdminServiceImpl adminService;

    @GetMapping("")
    public ResponseEntity<?> dashboard() {
        DashboardStatsDTO stats = adminService.getDashboardStats();
        Response response = new Response(1, "SUCCESSFULLY", stats);
        return ResponseEntity.ok(response);
    }
}
