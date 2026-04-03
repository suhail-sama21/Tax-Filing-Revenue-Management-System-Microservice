package com.cognizant.notificationService.controller;

import com.cognizant.notificationService.dto.request.DirectNotificationRequest;
import com.cognizant.notificationService.dto.response.NotificationResponse;
import com.cognizant.notificationService.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/user/{userId}")
    public ResponseEntity<String> sendDirectNotification(
            @PathVariable Long userId,
            @RequestBody DirectNotificationRequest request) {

        notificationService.sendNotificationToUser(userId, request.getMessage(), request.getCategory());
        return ResponseEntity.ok("Notification sent successfully");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<String> markAsRead(
            @PathVariable Long notificationId,
            @RequestParam Long userId) {

        notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.ok("Notification marked as read");
    }
}