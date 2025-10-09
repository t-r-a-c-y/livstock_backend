package com.livestock.backend.service;

import com.livestock.backend.dto.NotificationDTO;
import com.livestock.backend.model.Notification;
import com.livestock.backend.repository.NotificationRepository;
import com.livestock.backend.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public Page<NotificationDTO> getAll(Boolean isRead, Pageable pageable) {
        logger.info("Fetching notifications with isRead: {}", isRead);
        Specification<Notification> spec = (root, query, cb) -> cb.equal(root.get("userId"), getCurrentUserId());
        if (isRead != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isRead"), isRead));
        }
        return notificationRepository.findAll(spec, pageable).map(this::toDTO);
    }

    @Transactional
    public void markAsRead(UUID id) {
        logger.info("Marking notification as read: {}", id);
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        if (!notification.getUserId().equals(getCurrentUserId())) {
            throw new RuntimeException("Unauthorized access to notification");
        }
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    private NotificationDTO toDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setType(notification.getType());
        dto.setMessage(notification.getMessage());
        dto.setIsRead(notification.getIsRead());
        dto.setUserId(notification.getUserId());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }

    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof UserPrincipal principal) {
            return principal.getId();
        }
        throw new RuntimeException("No authenticated user");
    }
}