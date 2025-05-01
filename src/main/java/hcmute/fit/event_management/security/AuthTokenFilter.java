package hcmute.fit.event_management.security;

import hcmute.fit.event_management.dto.UserDetail;
import hcmute.fit.event_management.util.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/forgot",
            "/api/auth/reset-password",
            "/api/auth/send-verification-code/**",
            "/chat/**",
            "/api/auth/logout",
            "/change-password",
            "/ws/**",
            "/api/storage/**",
            "/api/events/search/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        String authHeader = request.getHeader("Authorization");


        //logger.debug("Authorization header for {}: {}", path, authHeader != null ? authHeader : "null");

        // Bỏ qua các endpoint công khai
        if (isPublicEndpoint(path)) {
            logger.debug("Skipping JWT validation for public endpoint: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = getJwtFromRequest(request);
            logger.info("JWT received for {}: {}", path, token != null ? "present" : "null");

            if (token != null && jwtTokenUtil.validateToken(token)) {
                String email = jwtTokenUtil.getEmailFromToken(token);
                List<String> roles = jwtTokenUtil.getRolesFromToken(token);
                List<String> permissions = jwtTokenUtil.getPermissionsFromToken(token);
                List<GrantedAuthority> authorities = new ArrayList<>();
                roles.stream().map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role)).forEach(authorities::add);
                permissions.stream().map(SimpleGrantedAuthority::new).forEach(authorities::add);
                UserDetail userDetail = new UserDetail(email, null, authorities);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Authenticated user: {} for {}", email, path);
            } else {
                logger.warn("Invalid or missing JWT for {}", path);
            }
        } catch (Exception e) {
            logger.error("JWT authentication error for {}: {}", path, e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(endpoint ->
                endpoint.endsWith("/**") ? path.startsWith(endpoint.substring(0, endpoint.length() - 3)) : path.equals(endpoint)
        );
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}