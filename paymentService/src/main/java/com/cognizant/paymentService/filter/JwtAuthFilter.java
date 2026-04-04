package com.cognizant.paymentService.filter;

import com.cognizant.taxease.entity.User;
import com.cognizant.taxease.dao.UserRepository;
import com.cognizant.taxease.util.AuthUtil;
import com.cognizant.taxease.util.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final AuthUtil authUtil;
    private final CustomUserDetails customUserDetailsService;
    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver resolver;

    JwtAuthFilter(AuthUtil authUtil,CustomUserDetails customUserDetails,@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver){
        this.authUtil=authUtil;
        this.customUserDetailsService=customUserDetails;
        this.resolver=resolver;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String header = request.getHeader("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = header.substring(7);
            String email = authUtil.extractEmail(token);

            // Added validation check for the token
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                // Ensure the token is valid before setting the context
                if (authUtil.validateToken(token)) {

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }

            filterChain.doFilter(request, response);
        }
        catch (ExpiredJwtException e) {
            // This forwards the error to your GlobalExceptionHandler
            resolver.resolveException(request, response, null, e);
        }
    }
}