package hcmute.fit.event_management.controller.guest;

import hcmute.fit.event_management.dto.AccountDTO;


import hcmute.fit.event_management.dto.ResetPasswordDTO;
import hcmute.fit.event_management.service.Impl.AccountServiceImpl;
import hcmute.fit.event_management.service.Impl.AuthServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.crypto.SecretKey;
import java.io.Console;


@RestController
public class LoginController {

    @Autowired
    AuthServiceImpl authServiceImpl;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AccountDTO account) {

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
