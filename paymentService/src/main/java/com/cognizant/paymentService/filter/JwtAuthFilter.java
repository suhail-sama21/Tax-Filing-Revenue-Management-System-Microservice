package com.cognizant.paymentService.filter;

import com.cognizant.paymentService.util.AuthUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final AuthUtil authUtil;
    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver resolver;

    // Injecting properties for Internal Key-Value pair
    @Value("${internal.auth.header-name}")
    private String internalHeader;

    @Value("${internal.auth.secret-value}")
    private String internalSecret;

    JwtAuthFilter(AuthUtil authUtil, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver){
        this.authUtil=authUtil;
        this.resolver=resolver;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String incomingInternalKey = request.getHeader(internalHeader);

            if (incomingInternalKey != null && incomingInternalKey.equals(internalSecret)) {
                log.info("Valid Internal Service Key detected. Granting INTERNAL role.");

                // Create a system-level authentication context
                UsernamePasswordAuthenticationToken internalAuth = new UsernamePasswordAuthenticationToken(
                        "SYSTEM-SERVICE",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_INTERNAL"))
                );

                internalAuth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(internalAuth);

                filterChain.doFilter(request, response);
                return;
            }
            String header = request.getHeader("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = header.substring(7);
            String email = authUtil.extractUsername(token);
            String role = authUtil.extractRole(token);
            log.info("Extracted email: {}, role: {} from JWT token", email, role);
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                if (authUtil.validateToken(token)) {
                    List<SimpleGrantedAuthority> authorities =List.of(new SimpleGrantedAuthority(role));
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            authorities
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            log.info("JWT token is valid. Proceeding with the request.");
            filterChain.doFilter(request, response);
            log.info("Request processing completed for email: {}", email);

        } catch (ExpiredJwtException e) {
            resolver.resolveException(request, response, null, e);
        }
    }
}