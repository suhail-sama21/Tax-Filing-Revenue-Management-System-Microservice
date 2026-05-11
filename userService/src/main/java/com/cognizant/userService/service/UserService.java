package com.cognizant.userService.service;

import com.cognizant.userService.dto.PasswordDto;
import com.cognizant.userService.dto.UpdateUserProfileRequest;
import com.cognizant.userService.dto.UserRegistrationRequest;
import com.cognizant.userService.entity.User;
import com.cognizant.userService.dao.UserRepository;
import com.cognizant.userService.exception.GlobalExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            // Use specific exception for better error handling in the future
            throw new RuntimeException("User already exists with email: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : "TAXPAYER")
                .address(request.getAddress())
                .panNumber(request.getPanNumber())
                .dob(request.getDob())
                .build();

        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BadCredentialsException("user not found with id:"+id));
    }

    public User updateUserProfile(Long id, UpdateUserProfileRequest request) {
        log.info("Updating profile for user ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Update the new fields
        user.setAddress(request.getAddress());
        user.setPanNumber(request.getPanNumber()); // Changed from setContactInfo
        user.setDob(request.getDob());             // Added DOB update support
        user.setName(request.getName());
        user.setPhone(request.getPhone());

        return userRepository.save(user);
    }

    public User getUserByName(String username) {
        User user = userRepository.findByEmail(username);
        if(user==null)
                throw new BadCredentialsException("user not found with email "+username);
        return user;
    }

    public ResponseEntity<String> changePassword(Long userId, PasswordDto passwordDto) {
        User user = userRepository.findById(userId).get();
        if(user==null) {
            log.info("user not found with id {}",userId);
            throw new BadCredentialsException("user not found with id " + userId);
        }else {
            if(passwordEncoder.matches(passwordDto.getOldPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
                userRepository.save(user);
                log.info("user change password successful");
            }
            else{
                log.info("user change password failed");
                throw new BadCredentialsException("The password is incorrect");
            }
        }
        return ResponseEntity.ok("Password Changed Successfully");
    }
}