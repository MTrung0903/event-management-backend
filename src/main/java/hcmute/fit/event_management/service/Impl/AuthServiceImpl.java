package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.dto.AccountDTO;
import hcmute.fit.event_management.dto.ResetPasswordDTO;
import hcmute.fit.event_management.entity.Account;
import hcmute.fit.event_management.entity.PasswordResetToken;
import hcmute.fit.event_management.repository.AccountRepository;
import hcmute.fit.event_management.repository.PasswordResetTokenRepository;
import hcmute.fit.event_management.service.AuthService;
import hcmute.fit.event_management.util.JwtTokenUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import payload.Response;

import java.util.List;
import java.util.Optional;

@Component
public class AuthServiceImpl implements AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    EmailServiceImpl emailService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public ResponseEntity<Response> signIn(AccountDTO account) {
        Response response;
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(account.getEmail(), account.getPassword()));
        if (authentication.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            String token = jwtTokenUtil.generateToken(authentication, roles);
            response = new Response(200, "Success", token);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response = new Response(401, "Unauthorized", "Invalid credentials");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
    @Transactional
    @Override
    public ResponseEntity<Response> sendResetPassword(String email) {
        // Tìm tài khoản theo email
        Optional<Account> accountOpt = accountRepository.findByEmail(email);
        if (accountOpt.isEmpty()) {
            logger.warn("Account not found with password reset request for email: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(404, "Not Found", "Account with this email does not exist"));
        }

        Account account = accountOpt.get(); // Tài khoản tồn tại
        String newToken = jwtTokenUtil.generateResetToken(email);

        // Kiểm tra và cập nhật/đặt mới token
        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByAccountID(account.getAccountID())
                .orElse(new PasswordResetToken());

        resetToken.setAccount(account);
        resetToken.setToken(newToken);
        passwordResetTokenRepository.save(resetToken);

        // Gửi email
        emailService.sendResetEmail(email, newToken);
        logger.info("Password reset request email sent successfully for email: {}", email);

        // Trả về phản hồi
        return ResponseEntity.ok(new Response(200, "Success", "Password reset link has been sent to your email"));
    }

    @Transactional
    @Override
    public ResponseEntity<Response> resetPassword(ResetPasswordDTO resetPasswordDTO) {
        String token = resetPasswordDTO.getToken();
        Response response;
        if (jwtTokenUtil.validateToken(token)) {
            Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByToken(token);
            if (passwordResetToken.isPresent()) {
                Account account = passwordResetToken.get().getAccount();
                // Set the new password for the account
                account.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
                passwordResetTokenRepository.delete(passwordResetToken.get());
                // Remove the relationship to the deleted token to avoid cascade persistence
                account.setToken(null);
                accountRepository.save(account);
                response = new Response(200, "Password successfully reset", "True");
                logger.info("The account's password has been reset successfully");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response = new Response(404, "Token not found", "False");
                logger.warn("Password reset token not foundt");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } else {
            response = new Response(400, "Invalid token", "False");
            logger.warn("Invalid reset token");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

}
