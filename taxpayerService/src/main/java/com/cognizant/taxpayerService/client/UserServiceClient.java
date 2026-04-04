package com.cognizant.taxpayerService.client;

import com.cognizant.taxpayerService.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Must match the User Service's eureka name
@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient {

    @GetMapping("/api/users/email/{email}")
    UserDTO getUserByEmail(@PathVariable("email") String email);
}