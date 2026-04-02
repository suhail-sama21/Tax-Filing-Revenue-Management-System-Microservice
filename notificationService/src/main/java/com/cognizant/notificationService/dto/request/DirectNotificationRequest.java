package com.cognizant.notificationService.dto.request;

import com.cognizant.notificationService.entity.entityenum.NotificationCategory;
import lombok.Data;

@Data
public class DirectNotificationRequest {
    private String message;
    private NotificationCategory category;
}