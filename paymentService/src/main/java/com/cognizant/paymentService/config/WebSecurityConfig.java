package com.cognizant.paymentService.config;

import com.cognizant.paymentService.filter.JwtAuthFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Make sure to import this!
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableMethodSecurity
@Slf4j
public class WebSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final HandlerExceptionResolver resolver;

    public WebSecurityConfig(JwtAuthFilter jwtAuthFilter,
                             @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.resolver = resolver;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(csrfConfig -> csrfConfig.disable())
                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            resolver.resolveException(request, response, null, accessDeniedException);
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        // THE MAGIC LINE: Let Angular's preflight check pass without a token!
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Your original role-based rules
                        .requestMatchers("/api/payments/pay", "/api/payments/retry/**").hasAnyRole("TAXPAYER","INTERNAL")
                        .requestMatchers("/api/payments/history/**").hasAnyRole("TAXPAYER", "OFFICER","INTERNAL")
                        .requestMatchers("/api/payments/**").hasAnyRole("OFFICER","INTERNAL")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        log.info("Security filter chain configured successfully for Payment Service");
        return httpSecurity.build();
    }
}