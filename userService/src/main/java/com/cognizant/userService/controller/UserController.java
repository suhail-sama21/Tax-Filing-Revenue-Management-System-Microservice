package com.cognizant.userService.controller;

import com.cognizant.userService.dto.UserDTO;
import com.cognizant.userService.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {


        UserDTO userDto=userService.findByUsername(username);

        return ResponseEntity.ok(userDto);
    }
    // ADD THIS to UserController.java in User Service
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        // Change this from findByUsername to findByEmail!
        UserDTO userDto = userService.findByEmail(email);
        return ResponseEntity.ok(userDto);
    }


    @PostMapping("/register")
    public UserDTO registerUser(@RequestBody UserDTO userDto)
    {
        return userService.registerUser(userDto);
    }
}
