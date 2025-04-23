package hcmute.fit.event_management.controller.admin;

import hcmute.fit.event_management.dto.ForgotPasswordDTO;
import hcmute.fit.event_management.dto.ResetPasswordDTO;
import hcmute.fit.event_management.dto.UserDTO;
import hcmute.fit.event_management.service.Impl.AuthServiceImpl;
import hcmute.fit.event_management.service.Impl.EmailServiceImpl;
import hcmute.fit.event_management.service.Impl.UserServiceImpl;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payload.Response;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private EmailServiceImpl emailService;

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody UserDTO userDTO) {
        return authService.signIn(userDTO);
    }

    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody UserDTO userDTO) {
        return userService.register(userDTO);
    }

    @PostMapping("/forgot")
    public ResponseEntity<Response> forgotPassword(@RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        return authService.sendResetPassword(forgotPasswordDTO.getEmail());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Response> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        return authService.resetPassword(resetPasswordDTO);
    }
    @PostMapping("/logout")
    public ResponseEntity<Response> logout() {
        return authService.logout();
    }

    @PostMapping("/send-verification-code/{email}")
    public ResponseEntity<String> sendVerificationCode(@PathVariable String email) {
        try {
            String code = emailService.sendVerificationCode(email);
            return ResponseEntity.ok(code);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
