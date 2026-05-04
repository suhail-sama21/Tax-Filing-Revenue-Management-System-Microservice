package com.cognizant.notificationService.repository;

import com.cognizant.notificationService.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Finds all notifications for a specific user, newest first
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Ensures a user can only read their own notification
    Optional<Notification> findByIdAndUserId(Long id, Long userId);
}