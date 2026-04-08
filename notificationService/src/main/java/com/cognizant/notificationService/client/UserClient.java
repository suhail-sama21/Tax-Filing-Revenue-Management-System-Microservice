package com.cognizant.notificationService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {
    // We can return a generic Object since we only care if it returns a 200 OK or a 404 Not Found
    @GetMapping("/api/users/{userId}")
    Object getUserById(@PathVariable("userId") Long userId);
}