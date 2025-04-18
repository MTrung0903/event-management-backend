package hcmute.fit.event_management.controller.admin;

import hcmute.fit.event_management.dto.ForgotPasswordDTO;
import hcmute.fit.event_management.dto.ResetPasswordDTO;
import hcmute.fit.event_management.dto.UserDTO;
import hcmute.fit.event_management.service.Impl.AuthServiceImpl;
import hcmute.fit.event_management.service.Impl.UserServiceImpl;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import payload.Response;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private UserServiceImpl userService;

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
}
