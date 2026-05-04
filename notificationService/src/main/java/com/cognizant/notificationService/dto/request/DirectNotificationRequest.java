package com.cognizant.notificationService.dto.request;

import com.cognizant.notificationService.entity.entityenum.NotificationCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DirectNotificationRequest {

    @NotBlank(message = "Notification message cannot be empty or blank")
    @Size(max = 500, message = "Notification message cannot exceed 500 characters")
    private String message;

    // We leave this without @NotNull because your NotificationServiceImpl
    // automatically defaults to SYSTEM_UPDATE if this is left null.
    private NotificationCategory category;
}