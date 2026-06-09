package com.example.livestock.repository;

import com.example.livestock.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUserIdAndActiveTrue(Long userId, Pageable pageable);
    Optional<Notification> findByIdAndUserIdAndActiveTrue(Long id, Long userId);
}
