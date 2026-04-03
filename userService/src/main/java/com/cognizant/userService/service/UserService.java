package com.cognizant.userService.service;

import com.cognizant.userService.dao.UserRepository;
import com.cognizant.userService.dto.UserDTO;
import com.cognizant.userService.entity.User;
import com.cognizant.userService.entity.entityEnum.UserRole;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    //private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDTO registerUser(UserDTO userDto) {

          User user = modelMapper.map(userDto,User.class);

        // Encrypt password

        String encryptedPassword = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(encryptedPassword);
        user.setRole(UserRole.TAXPAYER);
        User savedUser=userRepository.save(user);
        return modelMapper.map(savedUser, UserDTO.class);

    }

    public UserDTO findByUsername(String username) {
        User user = userRepository
                .findByName(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));
        return modelMapper.map(user, UserDTO.class);
    }
}
