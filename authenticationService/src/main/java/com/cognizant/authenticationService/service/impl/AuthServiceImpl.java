package com.cognizant.authenticationService.service.impl;

import com.cognizant.authenticationService.dto.AuthRequestDTO;
import com.cognizant.authenticationService.dto.AuthResponseDTO;
import com.cognizant.authenticationService.dto.RegisterResponse;
import com.cognizant.authenticationService.dto.UserDTO;
import com.cognizant.authenticationService.feignclient.UserClient;
import com.cognizant.authenticationService.service.AuthService;
import com.cognizant.authenticationService.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil authUtil;
    private final UserClient userClient;
    @Override
    public AuthResponseDTO login(AuthRequestDTO dto) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        String role = userDetails.getAuthorities()
                .iterator()
                .next()
                .getAuthority();

        String token = authUtil.generateToken(userDetails.getUsername(),role);
        log.info("Generated); JWT token for user: {}", dto.getEmail());

        return new AuthResponseDTO(token);
    }

    @Override
    public RegisterResponse register(UserDTO userDTO) {
       UserDTO user=userClient.registerUser(userDTO);
       return RegisterResponse.builder()
               .name(user.getName())
               .message("User registered successfully")
               .build();
    }
}
