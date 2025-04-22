package hcmute.fit.event_management.config;


import hcmute.fit.event_management.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue").setTaskScheduler(new DefaultManagedTaskScheduler());
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat")
                .setAllowedOrigins("http://localhost:3000")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, org.springframework.messaging.MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // Xác thực JWT
                    String token = accessor.getFirstNativeHeader("Authorization");
                    if (token != null && token.startsWith("Bearer ")) {
                        token = token.substring(7);
                        try {
                            if (jwtTokenUtil.validateToken(token)) {
                                String email = jwtTokenUtil.getEmailFromToken(token);
                                Principal principal = new Principal() {
                                    @Override
                                    public String getName() {
                                        return email.toLowerCase().trim();
                                    }
                                };
                                accessor.setUser(principal);
                                // Lưu vào SecurityContext
                                Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, null);
                                SecurityContextHolder.getContext().setAuthentication(authentication);
                                System.out.println("User connected via JWT: " + email.toLowerCase().trim());
                            } else {
                                System.err.println("Invalid JWT token in STOMP CONNECT");
                            }
                        } catch (Exception e) {
                            System.err.println("Error validating JWT token: " + e.getMessage());
                        }
                    } else {
                        System.err.println("No Authorization header in STOMP CONNECT");
                    }
                }
                return message;
            }
        });
    }
}