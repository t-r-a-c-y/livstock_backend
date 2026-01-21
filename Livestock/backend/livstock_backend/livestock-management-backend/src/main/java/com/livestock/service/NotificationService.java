// NotificationService.java
package com.livestock.service;

import com.livestock.dto.NotificationDto;
import com.livestock.entity.Notification;
import com.livestock.entity.enums.NotificationCategory;
import com.livestock.entity.enums.NotificationType;
import com.livestock.entity.enums.Priority;
import com.livestock.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public NotificationDto createNotification(
            String title,
            String message,
            NotificationType type,
            NotificationCategory category,
            Priority priority,
            String relatedEntityType,
            UUID relatedEntityId) {

        Notification n = new Notification();
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);
        n.setCategory(category);
        n.setPriority(priority);
        n.setRelatedEntityType(relatedEntityType);
        n.setRelatedEntityId(relatedEntityId);
        n.setActionRequired(false); // default

        Notification saved = notificationRepository.save(n);
        return mapToDto(saved);
    }

    public NotificationDto createHealthAlert(String title, String animalTag, UUID animalId) {
        return createNotification(
                title,
                "Health issue with animal " + animalTag,
                NotificationType.HEALTH_ALERT,
                NotificationCategory.WARNING,
                Priority.HIGH,
                "animal",
                animalId
        );
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getUnreadNotifications() {
        return notificationRepository.findByIsReadFalseOrderByCreatedAtDesc()
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getUnreadCount() {
        return notificationRepository.countByIsReadFalse();
    }

    @Transactional
    public void markAsRead(UUID id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        n.setIsRead(true);
        n.setReadAt(LocalDateTime.now());
        notificationRepository.save(n);
    }

    @Transactional
    public void markAllRead() {
        notificationRepository.findByIsReadFalseOrderByCreatedAtDesc()
                .forEach(n -> {
                    n.setIsRead(true);
                    n.setReadAt(LocalDateTime.now());
                });
        notificationRepository.saveAll(notificationRepository.findByIsReadFalseOrderByCreatedAtDesc());
    }

    private NotificationDto mapToDto(Notification n) {
        NotificationDto dto = new NotificationDto();
        dto.setId(n.getId());
        dto.setTitle(n.getTitle());
        dto.setMessage(n.getMessage());
        dto.setType(n.getType());
        dto.setCategory(n.getCategory());
        dto.setPriority(n.getPriority());
        dto.setRelatedEntityType(n.getRelatedEntityType());
        dto.setRelatedEntityId(n.getRelatedEntityId());
        dto.setIsRead(n.isRead());
        dto.setReadAt(n.getReadAt());
        dto.setActionRequired(n.isActionRequired());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }
}