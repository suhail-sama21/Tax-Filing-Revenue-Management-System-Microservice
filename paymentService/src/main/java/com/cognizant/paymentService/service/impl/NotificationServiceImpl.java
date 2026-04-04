package com.cognizant.paymentService.service.impl;

import com.cognizant.taxease.dao.NotificationRepository;
import com.cognizant.taxease.dao.UserRepository;
import com.cognizant.taxease.dto.responsedto.NotificationResponse;
import com.cognizant.taxease.entity.Notification;
import com.cognizant.taxease.entity.User;
import com.cognizant.taxease.entity.entityEnum.NotificationCategory;
import com.cognizant.taxease.entity.entityEnum.NotificationStatus;
import com.cognizant.taxease.service.AuditLogService;
import com.cognizant.taxease.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    private  final ModelMapper modelMapper ;

    @Override
    @Transactional
    public void broadcastNotification(String message, NotificationCategory category) {
        // Fetch all users
        List<User> allUsers = userRepository.findAll();

        // Map users to new Notification entities
        List<Notification> notifications = allUsers.stream().map(user ->
                Notification.builder()
                        .user(user)
                        .message(message)
                        .category(category != null ? category : NotificationCategory.BROADCAST)
                        .status(NotificationStatus.UNREAD)
                        .build()
        ).collect(Collectors.toList());

        // Save all notifications to the database in one batch
        notificationRepository.saveAll(notifications);
        auditLogService.record("NOTIFICATION_BROADCAST", "notifications/bulk");
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        // 1. Fetch the notification, ensuring it belongs to the user
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new RuntimeException("Notification not found or you do not have permission to access it."));

        // 2. Check if it is already read to avoid unnecessary database updates
        if (notification.getStatus() == NotificationStatus.READ) {
            return;
        }

        // 3. Update the status and save
        notification.setStatus(NotificationStatus.READ);
        notificationRepository.save(notification);
        auditLogService.record("NOTIFICATION_SEND", "notifications/user/" + userId);
    }

//    @Override
//    public List<NotificationResponse> getUserNotifications(Long userId) {
//        // 1. Fetch from DB
//        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedDateDesc(userId);
//
//        // 2. Map to DTOs
//        return notifications.stream().map(notification ->
//                NotificationResponse.builder()
//                        .id(notification.getId())
//                        .message(notification.getMessage())
//                        .category(notification.getCategory())
//                        .status(notification.getStatus())
//                        .entityId(notification.getEntityId())
//                        .createdDate(notification.getCreatedDate())
//                        .build()
//        ).collect(Collectors.toList());
//    }
@Override
public List<NotificationResponse> getUserNotifications(Long userId) {
    List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedDateDesc(userId);

    // Replaced Stream with ModelMapper using TypeToken to map the entire list at once
    java.lang.reflect.Type targetListType = new TypeToken<List<NotificationResponse>>() {}.getType();

    return modelMapper.map(notifications, targetListType);
}
    @Override
    @Transactional
    public void sendNotificationToUser(Long userId, String message, NotificationCategory category) {
        // 1. Verify the user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // 2. Build the new notification
        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .category(category != null ? category : NotificationCategory.SYSTEM_UPDATE)
                .status(NotificationStatus.UNREAD)
                .build();

        // 3. Save it to the database
        notificationRepository.save(notification);
    }
}