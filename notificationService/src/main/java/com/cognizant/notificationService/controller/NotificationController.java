package com.cognizant.notificationService.controller;

import com.cognizant.notificationService.dto.request.DirectNotificationRequest;
import com.cognizant.notificationService.dto.response.NotificationResponse;
import com.cognizant.notificationService.service.NotificationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Validated // <-- 1. CRITICAL: Enables validation for @PathVariable and @RequestParam
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/user/{userId}")
    public ResponseEntity<String> sendDirectNotification(
            // 2. Ensure userId is a positive number
            @PathVariable @Positive(message = "User ID must be a positive number") Long userId,
            // 3. Keep the @Valid here to trigger your DTO validation
            @Valid @RequestBody DirectNotificationRequest request) {

        notificationService.sendNotificationToUser(userId, request.getMessage(), request.getCategory());
        return ResponseEntity.ok("Notification sent successfully");
    }
//    @PreAuthorize("hasAnyRole('TAXPAYER','INTERNAL')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(
            @PathVariable @Positive(message = "User ID must be a positive number") Long userId) {

        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<String> markAsRead(
            @PathVariable @Positive(message = "Notification ID must be a positive number") Long notificationId,
            @RequestParam @Positive(message = "User ID must be a positive number") Long userId) {

        notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.ok("Notification marked as read");
    }
}