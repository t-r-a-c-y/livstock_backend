package com.livestock.backend.service;

import com.livestock.backend.dto.NotificationDTO;
import com.livestock.backend.model.Notification;
import com.livestock.backend.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public Page<NotificationDTO> getAll(Pageable pageable) {
        logger.info("Fetching notifications for current user");
        UUID userId = getCurrentUserId();
        Specification<Notification> spec = Specification.where((root, query, cb) -> cb.isNull(root.get("deletedAt")))
                .and((root, query, cb) -> cb.equal(root.get("userId"), userId));
        return notificationRepository.findAll(spec, pageable).map(this::toDTO);
    }

    @Transactional
    public void markAsRead(UUID id) {
        logger.info("Marking notification as read: {}", id);
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new RuntimeException("Notification not found"));
        if (notification.getDeletedAt() != null) throw new RuntimeException("Notification deleted");
        if (!notification.getUserId().equals(getCurrentUserId())) throw new RuntimeException("Not your notification");
        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Transactional
    public void softDelete(UUID id) {
        logger.info("Soft deleting notification: {}", id);
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new RuntimeException("Notification not found"));
        if (!notification.getUserId().equals(getCurrentUserId())) throw new RuntimeException("Not your notification");
        notification.setDeletedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    private NotificationDTO toDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUserId());
        dto.setMessage(notification.getMessage());
        dto.setIsRead(notification.getIsRead());
        dto.setReadAt(notification.getReadAt());
        dto.setActionRequired(notification.getActionRequired());
        dto.setRelatedEntityType(notification.getRelatedEntityType());
        dto.setRelatedEntityId(notification.getRelatedEntityId());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }

    private UUID getCurrentUserId() {
        // same as above
    }
}