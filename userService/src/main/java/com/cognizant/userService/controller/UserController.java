package com.cognizant.userService.controller;

import com.cognizant.userService.dto.UpdateUserProfileRequest;
import com.cognizant.userService.dto.UserRegistrationRequest;
import com.cognizant.userService.entity.User;
import com.cognizant.userService.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserRegistrationRequest request) {
        User createdUser = userService.registerUser(request);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TAXPAYER')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // --- NEW ENDPOINT FOR FEIGN CLIENT ---
    @PutMapping("/{id}/profile")
    public ResponseEntity<User> updateProfile(
            @PathVariable Long id,
            @RequestBody UpdateUserProfileRequest request) {
        return ResponseEntity.ok(userService.updateUserProfile(id, request));
    }

    @GetMapping("/username/{username}")
    public  ResponseEntity<User> getUserByUsername(@PathVariable String username){
        return ResponseEntity.ok(userService.getUserByName(username));
    }
}