package hcmute.fit.event_management.controller.guest;

import hcmute.fit.event_management.dto.UserDTO;


import hcmute.fit.event_management.dto.ResetPasswordDTO;
import hcmute.fit.event_management.service.Impl.AuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class LoginController {

    @Autowired
    AuthServiceImpl authServiceImpl;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO account) {

        return authServiceImpl.signIn(account);
    }

    @PostMapping("/forgot")
    public ResponseEntity<?> forgot(@RequestBody ResetPasswordDTO email) {
        System.out.println("======================"+email+"====================");
        return authServiceImpl.sendResetPassword(email.getEmail());
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        System.out.println(resetPasswordDTO);
        return authServiceImpl.resetPassword(resetPasswordDTO);
    }
}
