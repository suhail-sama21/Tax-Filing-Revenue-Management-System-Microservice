package com.cognizant.notificationService.service.impl;

import com.cognizant.notificationService.client.UserClient;
import com.cognizant.notificationService.dto.response.NotificationResponse;
import com.cognizant.notificationService.entity.Notification;
import com.cognizant.notificationService.entity.entityenum.NotificationCategory;
import com.cognizant.notificationService.entity.entityenum.NotificationStatus;
import com.cognizant.notificationService.exception.ResourceNotFoundException;
import com.cognizant.notificationService.repository.NotificationRepository;
import com.cognizant.notificationService.service.NotificationService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserClient userClient; // <-- Inject the Feign Client

    // --- Helper Method to Validate User via Feign ---
    // --- Helper Method to Validate User via Feign ---
    private void validateUserExists(Long userId) {
        try {
            userClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            // This handles a proper 404 from the User Service
            log.error("Validation failed: User ID {} not found in User Service", userId);
            throw new ResourceNotFoundException("User ID " + userId + " does not exist.");
        } catch (FeignException e) {
            // NEW: Catch other Feign errors (like 401 or 500) and re-throw them
            // so your GlobalExceptionHandler's @ExceptionHandler(FeignException.class) can process them.
            log.error("Feign exception communicating with User Service. Status: {}", e.status());
            throw e;
        } catch (Exception e) {
            // This now only catches non-HTTP/Feign errors (e.g., connection refused, timeout)
            log.error("Error communicating with User Service", e);
            throw new RuntimeException("Error verifying user existence.");
        }
    }

    @Override
    @Transactional
    public void sendNotificationToUser(Long userId, String message, NotificationCategory category) {
        log.info("Verifying user {} before creating notification", userId);
        validateUserExists(userId); // <-- Call validation

        log.info("Creating notification for user {}", userId);
        Notification notification = Notification.builder()
                .userId(userId)
                .message(message)
                .category(category != null ? category : NotificationCategory.SYSTEM_UPDATE)
                .status(NotificationStatus.UNREAD)
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponse> getUserNotifications(Long userId) {
        log.info("Verifying user {} before fetching notifications", userId);
        validateUserExists(userId); // <-- Call validation

        log.info("Fetching notifications for user {}", userId);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        log.info("Verifying user {} before marking notification as read", userId);
        validateUserExists(userId); // <-- Call validation

        log.info("Marking notification {} as read for user {}", notificationId, userId);
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found or unauthorized"));

        if (notification.getStatus() == NotificationStatus.UNREAD) {
            notification.setStatus(NotificationStatus.READ);
            notificationRepository.save(notification);
        }
    }

    // Helper method to map Entity to DTO
    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .category(notification.getCategory())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .build();
    }
    // ... inside NotificationServiceImpl ...

    @Override
    @Transactional
    public void broadcastNotification(String message, NotificationCategory category) {
        log.info("Fetching all user IDs for broadcast...");

        List<Long> allUserIds;
        try {
            allUserIds = userClient.getAllUserIds();
        } catch (Exception e) {
            log.error("Error communicating with User Service to fetch all IDs", e);
            throw new RuntimeException("Failed to fetch users for broadcasting.");
        }

        if (allUserIds == null || allUserIds.isEmpty()) {
            log.warn("No users found to broadcast the notification to.");
            return;
        }

        log.info("Broadcasting notification to {} users", allUserIds.size());

        // Map the user IDs to a list of Notification entities
        List<Notification> notifications = allUserIds.stream()
                .map(userId -> Notification.builder()
                        .userId(userId)
                        .message(message)
                        .category(category != null ? category : NotificationCategory.BROADCAST) // Defaults to BROADCAST
                        .status(NotificationStatus.UNREAD)
                        .build())
                .collect(Collectors.toList());

        // Batch save all notifications
        notificationRepository.saveAll(notifications);
        log.info("Successfully saved broadcast notifications to the database.");
    }
}