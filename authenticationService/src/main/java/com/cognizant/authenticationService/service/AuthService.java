package com.cognizant.authenticationService.service;

import com.cognizant.authenticationService.dto.AuthRequestDTO;
import com.cognizant.authenticationService.dto.AuthResponseDTO;
import com.cognizant.authenticationService.dto.RegisterResponse;
import com.cognizant.authenticationService.dto.UserDTO;

public interface AuthService {
    AuthResponseDTO login(AuthRequestDTO request);
    RegisterResponse register(UserDTO userDTO);
}
