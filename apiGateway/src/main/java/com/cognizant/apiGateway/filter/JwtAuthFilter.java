package com.cognizant.apiGateway.filter;

import com.cognizant.apiGateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.core.Ordered;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // Skip JWT validation for auth endpoints
        log.info("in the global filter");
        if (path.startsWith("/api/auth")) {
            log.info("Auth endpoint accessed: {}", path);
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // Check if Authorization header exists
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing Authorization Header for path: {}", path);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }

        // Extract token
        String token = authHeader.substring(7);

        // Validate token
        try {
            if (!jwtUtil.validateToken(token)) {
                log.warn("Invalid Token for path: {}", path);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
            }

            // Extract user info from token
            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);

            log.info("Token validated for user: {} with role: {}", username, role);

            // Add user info to headers for downstream services
            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header("X-User-Name", username)
                            .header("X-User-Role", role)
                            .build())
                    .build();

            return chain.filter(mutatedExchange);

        } catch (Exception e) {
            log.error("Error validating token for path: {}, error: {}", path, e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token validation failed: " + e.getMessage());
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}