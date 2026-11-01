package com.easylabour.userservice.application.service;

import com.easylabour.userservice.domain.model.AuditLog;
import com.easylabour.userservice.domain.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void logAction(String userId, String action, String targetType, String targetId, String details) {
        AuditLog log = AuditLog.builder()
            .userId(userId)
            .action(action)
            .targetType(targetType)
            .targetId(targetId)
            .details(details)
            .build();
        auditLogRepository.save(log);
    }
}
