// NotificationRepository.java
package com.livestock.repository;

import com.livestock.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByIsReadFalseOrderByCreatedAtDesc();

    List<Notification> findByPriorityAndIsReadFalse(String priority);

    List<Notification> findByCategoryAndIsReadFalse(String category);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.isRead = false")
    long countUnread();

    @Query("SELECT n FROM Notification n WHERE n.relatedEntityId = :entityId " +
            "AND n.relatedEntityType = :entityType")
    List<Notification> findByRelatedEntity(
            @Param("entityId") UUID entityId,
            @Param("entityType") String entityType);
}