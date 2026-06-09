package com.example.livestock.service.impl;

import com.example.livestock.dto.AuditLogResponse;
import com.example.livestock.entity.AuditLog;
import com.example.livestock.entity.User;
import com.example.livestock.repository.AuditLogRepository;
import com.example.livestock.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {
    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public void record(String action, String entityType, Long entityId, User actor, String details) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setActor(actor);
        log.setDetails(details);
        auditLogRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> findAll(Pageable pageable) {
        return auditLogRepository.findByActiveTrue(pageable)
                .map(log -> new AuditLogResponse(
                        log.getId(),
                        log.getAction(),
                        log.getEntityType(),
                        log.getEntityId(),
                        log.getActor() == null ? "system" : log.getActor().getFullName(),
                        log.getDetails(),
                        log.getCreatedAt()));
    }
}
