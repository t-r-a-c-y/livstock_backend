package com.livestock.backend.service;

import com.livestock.backend.dto.NotificationDTO;
import com.livestock.backend.dto.NotificationCreateDTO;
import com.livestock.backend.dto.NotificationUpdateDTO;
import com.livestock.backend.model.Notification;
import com.livestock.backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;

    public Page<NotificationDTO> getAllNotifications(Boolean isRead, Pageable pageable) {
        logger.info("Fetching notifications with isRead: {}", isRead);
        if (isRead != null) {
            return notificationRepository.findByIsRead(isRead, pageable)
                    .map(notification -> modelMapper.map(notification, NotificationDTO.class));
        }
        return notificationRepository.findAllActive(pageable)
                .map(notification -> modelMapper.map(notification, NotificationDTO.class));
    }

    @Transactional
    public NotificationDTO markNotificationAsRead(UUID id) {
        logger.info("Marking notification as read with id: {}", id);
        Notification notification = notificationRepository.findById(id)
                .filter(n -> n.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Notification not found or deleted"));
        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        notification = notificationRepository.save(notification);
        return modelMapper.map(notification, NotificationDTO.class);
    }

    @Transactional
    public NotificationDTO createNotification(NotificationCreateDTO dto) {
        logger.info("Creating new notification of type: {}", dto.getType());
        Notification notification = modelMapper.map(dto, Notification.class);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification = notificationRepository.save(notification);
        return modelMapper.map(notification, NotificationDTO.class);
    }

    @Transactional
    public void softDeleteNotification(UUID id) {
        logger.info("Soft deleting notification with id: {}", id);
        Notification notification = notificationRepository.findById(id)
                .filter(n -> n.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Notification not found or already deleted"));
        notification.setDeletedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }
}