package com.cognizant.notificationService.config;

import com.cognizant.notificationService.filter.JwtAuthFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                        .requestMatchers("/api/notifications/**").hasAnyRole("TAXPAYER","OFFICER","ADMINISTRATOR",
                                        "MANAGER",
                                        "COMPLIANCE",
                                        "AUDITOR","INTERNAL")
//                        .requestMatchers("/api/notifications/user/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        log.info("Security filter chain configured successfully for Taxpayer Service");
        return httpSecurity.build();
    }
//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
}

