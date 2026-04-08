package com.cognizant.authenticationService.controller;

import com.cognizant.authenticationService.dto.AuthRequestDTO;
import com.cognizant.authenticationService.dto.AuthResponseDTO;
import com.cognizant.authenticationService.dto.RegisterResponse;
import com.cognizant.authenticationService.dto.UserDTO;
import com.cognizant.authenticationService.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
   @PostMapping("/login")
   public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
       log.info("START: Login attempt for user: {}", request.getEmail());
       AuthResponseDTO response = authService.login(request);
       log.info("END: Login successful ");
       return ResponseEntity.ok(response);
   }
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody UserDTO userDto)
    {
        log.info("START: Registering taxpayer with email: {}", userDto.getEmail());
        RegisterResponse response= authService.register(userDto);
        log.info("END: Registration successful for user ID: {}", response.getName());
        return ResponseEntity.ok(response);
    }

}

