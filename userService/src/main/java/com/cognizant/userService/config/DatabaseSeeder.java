package com.cognizant.userService.config;

import com.cognizant.userService.dao.UserRepository;
import com.cognizant.userService.entity.User;
import com.cognizant.userService.entity.entityEnum.StatusBasic;
import com.cognizant.userService.entity.entityEnum.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Prevent duplicate seeding
        if (userRepository.count() == 0) {
            log.info("Starting Database Seeding for Internal Roles...");

            try {
                String commonPassword = passwordEncoder.encode("Password123");

                // 1. Administrators
                createUser("Admin", "admin1@taxease.gov", "ADMINISTRATOR", commonPassword);

                // 2. Officers (Tax processing)
                createUser("Officer Sarah", "officer1@taxease.gov", "OFFICER", commonPassword);
                createUser("Officer James", "officer2@taxease.gov", "OFFICER", commonPassword);

                // 3. Managers (Reviewers)
                createUser("Manager Mike", "manager1@taxease.gov", "MANAGER", commonPassword);
                createUser("Manager Elena", "manager2@taxease.gov", "MANAGER", commonPassword);

                // 4. Compliance (Regulatory checks)
                createUser("Compliance Lead", "compliance1@taxease.gov", "COMPLIANCE", commonPassword);
                createUser("Compliance Officer", "compliance2@taxease.gov", "COMPLIANCE", commonPassword);

                // 5. Auditors (Internal/External audit)
                createUser("Auditor David", "auditor1@taxease.gov", "AUDITOR", commonPassword);
                createUser("Auditor Sophia", "auditor2@taxease.gov", "AUDITOR", commonPassword);

                log.info("Database Seeding Completed Successfully!");
                log.info("Internal Staff created. Taxpayers must register via the Application Signup.");

            } catch (Exception e) {
                log.error("Seeding failed: {}", e.getMessage());
                throw e;
            }
        } else {
            log.info("Database already contains users. Skipping seeder.");
        }
    }

    private void createUser(String name, String email, String role, String password) {
        User user = User.builder()
                .name(name)
                .email(email)
                .phone("9998887770") // Default placeholder phone
                .password(password)
                .role(role)
                .build();

        userRepository.saveAndFlush(user);
        log.debug("Created {} with role {}", email, role);
    }
}