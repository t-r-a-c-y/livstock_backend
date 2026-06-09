package com.example.livestock.service;

import com.example.livestock.dto.NotificationResponse;
import com.example.livestock.entity.User;
import com.example.livestock.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    void notify(User user, String title, String message, NotificationType type);
    Page<NotificationResponse> findMine(Pageable pageable);
    NotificationResponse markRead(Long id);
}
