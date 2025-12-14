// src/main/java/com/livestock/service/NotificationService.java
package com.livestock.service;

import com.livestock.entity.Notification;
import com.livestock.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // Explicit constructor — fixes "not initialized" error
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> getAllNotifications(Boolean isRead, String priority, String category) {
        if (isRead != null && !isRead) {
            return notificationRepository.findByIsReadFalseOrderByCreatedAtDesc();
        }
        if (priority != null) {
            return notificationRepository.findByPriorityAndIsReadFalse(priority);
        }
        if (category != null) {
            return notificationRepository.findByCategoryAndIsReadFalse(category);
        }
        return notificationRepository.findByIsReadFalseOrderByCreatedAtDesc();
    }

    public long getUnreadCount() {
        return notificationRepository.countUnread();
    }

    public void markAsRead(UUID id) {
        Notification n = notificationRepository.findById(id).orElseThrow();
        n.setIsRead(true);
        n.setReadAt(LocalDateTime.now());
        notificationRepository.save(n);
    }

    public void markAllAsRead() {
        List<Notification> unread = notificationRepository.findByIsReadFalseOrderByCreatedAtDesc();
        for (Notification n : unread) {
            n.setIsRead(true);
            n.setReadAt(LocalDateTime.now());
        }
        notificationRepository.saveAll(unread);
    }

    // Helper method used by other services
    public Notification createNotification(String title, String message, String type,
                                           String priority, String category,
                                           UUID relatedEntityId, String relatedEntityType,
                                           boolean actionRequired) {
        Notification n = new Notification();
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);
        n.setPriority(priority);
        n.setCategory(category);
        n.setRelatedEntityId(relatedEntityId);
        n.setRelatedEntityType(relatedEntityType);
        n.setActionRequired(actionRequired);
        return notificationRepository.save(n);
    }
}