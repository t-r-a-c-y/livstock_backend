package com.livestock.backend.service;


import com.livestock.backend.dto.response.NotificationResponse;
import com.livestock.backend.exception.ResourceNotFoundException;
import com.livestock.backend.model.Notification;
import com.livestock.backend.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

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

    public Page<NotificationResponse> getNotificationsForCurrentUser(int page, int size) {
        UUID userId = authService.getCurrentUser().getId();
        Pageable pageable = PageRequest.of(page, size);
        return notificationRepository.findByUserId(userId).stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList()); // Use paging if needed
    }

    public void markAsRead(UUID id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead() {
        UUID userId = authService.getCurrentUser().getId();
        notificationRepository.findByUserId(userId).forEach(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
    }

    public void deleteNotification(UUID id) {
        notificationRepository.deleteById(id);
    }

    // Method to create notification, call from other services
    public void createNotification(Notification notification) {
        notificationRepository.save(notification);
    }
}
