package com.cognizant.notificationService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/users/{userId}")
    Object getUserById(@PathVariable("userId") Long userId);

    // --- NEW: Fetch all user IDs ---
    @GetMapping("/api/users/ids")
    List<Long> getAllUserIds();
}