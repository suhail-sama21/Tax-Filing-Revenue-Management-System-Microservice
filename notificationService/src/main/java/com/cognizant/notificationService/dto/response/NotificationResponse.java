package com.cognizant.notificationService.dto.response;

import com.cognizant.notificationService.entity.entityenum.NotificationCategory;
import com.cognizant.notificationService.entity.entityenum.NotificationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private String message;
    private NotificationCategory category;
    private NotificationStatus status;
    private LocalDateTime createdAt;
}