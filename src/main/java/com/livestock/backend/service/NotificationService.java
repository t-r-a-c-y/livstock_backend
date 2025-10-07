package com.livestock.backend.service;

import com.livestock.backend.dto.request.NotificationRequest;
import com.livestock.backend.dto.response.NotificationResponse;
import com.livestock.backend.model.Notification;
import com.livestock.backend.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final AuthService authService;

    public NotificationService(NotificationRepository notificationRepository, NotificationMapper notificationMapper, AuthService authService) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.authService = authService;
    }

    public NotificationResponse createNotification(NotificationRequest request) {
        Notification notification = notificationMapper.toEntity(request);
        notification.setUser(authService.getCurrentUser());
        notification = notificationRepository.save(notification);
        return notificationMapper.toResponse(notification);
    }

    public Page<NotificationResponse> getNotifications(Pageable pageable) {
        UUID userId = authService.getCurrentUser().getId();
        Page<Notification> notifications = notificationRepository.findByUserId(userId, pageable);
        return notifications.map(notificationMapper::toResponse);
    }

    public List<NotificationResponse> getAllNotifications() {
        UUID userId = authService.getCurrentUser().getId();
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return notifications.stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    public void markAsRead(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        UUID userId = authService.getCurrentUser().getId();
        if (!notification.getUser().getId().equals(userId)) {
            throw new SecurityException("Unauthorized to mark this notification as read");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}