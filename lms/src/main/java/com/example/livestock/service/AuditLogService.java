package com.example.livestock.service;

import com.example.livestock.dto.AuditLogResponse;
import com.example.livestock.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
    void record(String action, String entityType, Long entityId, User actor, String details);
    Page<AuditLogResponse> findAll(Pageable pageable);
}
