package com.cognizant.userService.service;

import com.cognizant.userService.dto.UpdateUserProfileRequest;
import com.cognizant.userService.dto.UserRegistrationRequest;
import com.cognizant.userService.entity.User;
import com.cognizant.userService.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public User registerUser(UserRegistrationRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(request.getPassword())
                .role(request.getRole() != null ? request.getRole() : "TAXPAYER")
                .address(request.getAddress())         // Saving the new field
                .contactInfo(request.getContactInfo()) // Saving the new field
                .build();

        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        log.info("Fetching user ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    // --- NEW METHOD FOR FEIGN CLIENT TO CALL ---
    public User updateUserProfile(Long id, UpdateUserProfileRequest request) {
        log.info("Updating profile details for user ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        user.setAddress(request.getAddress());
        user.setContactInfo(request.getContactInfo());

        return userRepository.save(user);
    }
}