package com.livestock.backend.repository;

import com.livestock.backend.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    // Find all active notifications
    @Query("SELECT n FROM Notification n WHERE n.deletedAt IS NULL")
    List<Notification> findAllActive();

    // Paginated version
    @Query("SELECT n FROM Notification n WHERE n.deletedAt IS NULL")
    Page<Notification> findAllActive(Pageable pageable);

    // Filter by isRead
    @Query("SELECT n FROM Notification n WHERE n.deletedAt IS NULL AND n.isRead = :isRead")
    List<Notification> findByIsRead(@Param("isRead") Boolean isRead);
}