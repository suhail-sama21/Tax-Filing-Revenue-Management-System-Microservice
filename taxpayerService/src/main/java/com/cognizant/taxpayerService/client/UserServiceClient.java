package com.cognizant.taxpayerService.client;

import com.cognizant.taxpayerService.dto.PasswordDto;
import com.cognizant.taxpayerService.dto.UpdateTaxpayerProfileRequestDto;
import com.cognizant.taxpayerService.dto.User;
import com.cognizant.taxpayerService.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", url = "http://localhost:8099", configuration = FeignClientInterceptor.class)
public interface UserServiceClient {

    @GetMapping("/api/users/{id}")
    UserDto getUserById(@PathVariable("id") Long id);

    // Pass the update request back to the User Service!
    @PutMapping("/api/users/{id}/profile")
    UserDto updateUserProfile(@PathVariable("id") Long id, @RequestBody UpdateTaxpayerProfileRequestDto request);

    @PatchMapping("/api/users/{userId}/changePassword")
    String changePassword(@PathVariable Long userId, @RequestBody PasswordDto passwordDto);

    @GetMapping("/api/users/username/{username}")
    User getUserByUsername(@PathVariable("username") String username);
}