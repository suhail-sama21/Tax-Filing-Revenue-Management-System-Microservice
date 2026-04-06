package com.cognizant.taxpayerService.client;

import com.cognizant.taxpayerService.dto.UpdateTaxpayerProfileRequestDto;
import com.cognizant.taxpayerService.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/users/{id}")
    UserDto getUserById(@PathVariable("id") Long id);

    // Pass the update request back to the User Service!
    @PutMapping("/api/users/{id}/profile")
    UserDto updateUserProfile(@PathVariable("id") Long id, @RequestBody UpdateTaxpayerProfileRequestDto request);
}