package com.easylabour.userservice.web.controller;

import com.easylabour.userservice.application.service.AuditService;
import com.easylabour.userservice.application.service.WorkerService;
import com.easylabour.userservice.domain.model.WorkerProfile;
import com.easylabour.userservice.domain.model.WorkerVerificationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/workers")
@RequiredArgsConstructor
public class AdminWorkerController {
    private final WorkerService workerService;
    private final AuditService auditService;

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WorkerProfile> approveWorker(@PathVariable Long id) {
        // Implementation for approval
        String adminId = SecurityContextHolder.getContext().getAuthentication().getName();
        // ... workerService.updateStatus(id, APPROVED) ...
        auditService.logAction(adminId, "APPROVE_WORKER", "WORKER", id.toString(), "Approved by admin");
        return ResponseEntity.ok().build(); // Mocked response
    }
}
