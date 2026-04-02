package com.cognizant.notificationService.service;

import com.cognizant.notificationService.dto.response.NotificationResponse;
import com.cognizant.notificationService.entity.entityenum.NotificationCategory;

import java.util.List;

public interface NotificationService {
    void sendNotificationToUser(Long userId, String message, NotificationCategory category);
    List<NotificationResponse> getUserNotifications(Long userId);
    void markAsRead(Long notificationId, Long userId);
}