package com.cognizant.paymentService.config;
import com.cognizant.taxease.entity.entityEnum.UserRole;
import com.cognizant.taxease.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
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
                .csrf(csrfConfig -> csrfConfig.disable())
                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            resolver.resolveException(request, response, null, accessDeniedException);
                        })
                    )
                .authorizeHttpRequests(auth -> auth
                        // 1. Public Routes
                        .requestMatchers("/api/auth/**").permitAll()

                        // 2. Tax Filings & Documents
                        .requestMatchers("/api/filings/submit").hasRole("TAXPAYER")
                        .requestMatchers("/api/taxpayers/**").hasRole("TAXPAYER")
                        .requestMatchers("/api/filings/taxpayer/**").hasAnyRole("TAXPAYER", "OFFICER")
                        .requestMatchers("/api/filings/*/status").hasRole("OFFICER")
                        .requestMatchers("/api/documents/upload").hasRole("TAXPAYER")
                        .requestMatchers("/api/documents/filing/**").hasAnyRole("TAXPAYER", "OFFICER")

                        // 3. Payments & Revenue
                        .requestMatchers("/api/payments/pay", "/api/payments/retry/**").hasRole("TAXPAYER")
                        .requestMatchers("/api/payments/history/**").hasAnyRole("TAXPAYER", "OFFICER")

                        // 4. Compliance & Audit
                        .requestMatchers("/api/compliance/**").hasAnyRole("COMPLIANCE", "MANAGER", "ADMIN","TAXPAYER")
                        .requestMatchers("/api/compliance/taxpayer/**").hasAnyRole("TAXPAYER", "COMPLIANCE")
                        .requestMatchers("/api/audit/**").hasRole("AUDITOR")

                        // 5. Reporting & Analytics
                        .requestMatchers("/api/reports/payments/**", "/api/reports/revenue/**").hasAnyRole("MANAGER", "AUDITOR")
                        .requestMatchers("/api/reports/audits/**").hasAnyRole("AUDITOR", "ADMINISTRATOR")
                        .requestMatchers("/api/reports/custom/download").hasAnyRole("ADMINISTRATOR", "MANAGER")

                        // 6. Notifications
                        .requestMatchers("/api/notifications/broadcast").hasAnyRole("ADMINISTRATOR", "MANAGER")
                        .requestMatchers("/api/notifications/user/**").authenticated() // Logic handled in service for self vs officer

                        .requestMatchers("/api/audit-logs/**").hasRole("ADMINISTRATOR")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
