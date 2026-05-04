package com.cognizant.userService.service;

import com.cognizant.userService.dto.UpdateUserProfileRequest;
import com.cognizant.userService.dto.UserRegistrationRequest;
import com.cognizant.userService.entity.User;
import com.cognizant.userService.dao.UserRepository;
import com.cognizant.userService.exception.GlobalExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(UserRegistrationRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        if (userRepository.existsByEmail(request.getEmail())) {
            // Use specific exception
            throw new RuntimeException("user already exists with email "+request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : "TAXPAYER")
                .address(request.getAddress())
                .contactInfo(request.getContactInfo())
                .build();

        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BadCredentialsException("user not found with id:"+id));
    }

    public User updateUserProfile(Long id, UpdateUserProfileRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BadCredentialsException("user not found with id:"+id));

        user.setAddress(request.getAddress());
        user.setContactInfo(request.getContactInfo());

        return userRepository.save(user);
    }

    public User getUserByName(String username) {
        User user = userRepository.findByEmail(username);
        if(user==null)
                throw new BadCredentialsException("user not found with email "+username);
        return user;
    }
}