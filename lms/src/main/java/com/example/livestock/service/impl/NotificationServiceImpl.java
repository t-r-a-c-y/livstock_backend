package com.example.livestock.service.impl;

import com.example.livestock.dto.NotificationResponse;
import com.example.livestock.entity.Notification;
import com.example.livestock.entity.User;
import com.example.livestock.enums.NotificationType;
import com.example.livestock.exception.ResourceNotFoundException;
import com.example.livestock.mapper.DtoMapper;
import com.example.livestock.repository.NotificationRepository;
import com.example.livestock.security.CurrentUserService;
import com.example.livestock.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final CurrentUserService currentUserService;

    @Override
    public void notify(User user, String title, String message, NotificationType type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(type);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> findMine(Pageable pageable) {
        return notificationRepository.findByUserIdAndActiveTrue(currentUserService.getCurrentUser().getId(), pageable)
                .map(DtoMapper::toNotification);
    }

    @Override
    public NotificationResponse markRead(Long id) {
        Notification notification = notificationRepository.findByIdAndUserIdAndActiveTrue(id, currentUserService.getCurrentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setReadStatus(true);
        return DtoMapper.toNotification(notification);
    }
}
