package hcmute.fit.event_management.util;

import hcmute.fit.event_management.dto.AccountDetail;
import hcmute.fit.event_management.entity.Account;
import hcmute.fit.event_management.service.IAccountService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class JwtTokenUtil {
    @Value("${jwt.privateKey}")
    private String privateKey;
    @Value("${jwt.expirationMs}")
    private Long expirationMs;
    @Value("${jwt.expirationLoginMs}")
    private Long expirationLoginMs;
    @Autowired
    private IAccountService accountService;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public String generateToken(Authentication authentication, List<String> roles) {
        AccountDetail accountPrincipal = (AccountDetail) authentication.getPrincipal();
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(privateKey));
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationLoginMs);
        Account acc = accountService.findbyEmail(accountPrincipal.getUsername())
                .orElse(new Account());
        return Jwts.builder()
                .subject(accountPrincipal.getUsername())
                .issuedAt(now)
                .expiration(expiration)
                .claim("roles", roles)
                .claim("userId", acc.getAccountID())
                .signWith(key)
                .compact();
    }

    public String generateResetToken(String email) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(privateKey));
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    public String getEmailFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(privateKey));
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(privateKey));
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public List<String> getRolesFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(privateKey));
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return claims.get("roles", List.class); // Lấy danh sách roles từ "roles" claim
    }
}
