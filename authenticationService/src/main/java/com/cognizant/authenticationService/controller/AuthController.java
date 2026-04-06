package com.cognizant.authenticationService.controller;

import com.cognizant.authenticationService.dto.AuthRequestDTO;
import com.cognizant.authenticationService.dto.AuthResponseDTO;
import com.cognizant.authenticationService.dto.UserDTO;
import com.cognizant.authenticationService.feignclient.UserClient;
import com.cognizant.authenticationService.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {




        private final AuthenticationManager authManager;


        private final JwtUtil jwtUtil;

        private final UserClient userClient;

   // @PreAuthorize("permitAll()")
        @PostMapping("/login")
        public ResponseEntity<?> login(
                @RequestBody AuthRequestDTO request) {

            Authentication authentication =
                    authManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getEmail(),
                                    request.getPassword()));


            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String role = userDetails.getAuthorities()
                    .iterator()
                    .next()
                    .getAuthority();

            String token = jwtUtil.generateToken(userDetails.getUsername(), role);

            return ResponseEntity.ok(new AuthResponseDTO(token));


        }
    @PostMapping("/register")
    public UserDTO registerUser(@RequestBody UserDTO userDto)
    {
        return userClient.registerUser(userDto);
    }

}

