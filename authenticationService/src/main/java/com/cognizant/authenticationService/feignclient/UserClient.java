package com.cognizant.authenticationService.feignclient;

import com.cognizant.authenticationService.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="USER-SERVICE")
public interface UserClient {
    @PostMapping("/api/users/register")
    public UserDTO registerUser(@RequestBody UserDTO userDto);

    @GetMapping("/api/users/username/{username}")
    public UserDTO getUserByUsername(@PathVariable String username);
}
