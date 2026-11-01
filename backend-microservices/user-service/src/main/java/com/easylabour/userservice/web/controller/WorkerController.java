package com.easylabour.userservice.web.controller;

import com.easylabour.userservice.application.service.WorkerService;
import com.easylabour.userservice.domain.model.User;
import com.easylabour.userservice.domain.model.UserType;
import com.easylabour.userservice.domain.model.WorkerAvailability;
import com.easylabour.userservice.domain.model.WorkerProfile;
import com.easylabour.userservice.domain.model.WorkerSkill;
import com.easylabour.userservice.infrastructure.persistence.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workers")
@RequiredArgsConstructor
public class WorkerController {
    private final WorkerService workerService;
    private final JpaUserRepository userRepository;

    private String getAuthenticatedWorkerUserId() {
        String phoneNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByPhoneNumber(phoneNumber)
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getUserType() != UserType.LABOUR) {
            throw new AccessDeniedException("Access denied: user is not a worker");
        }
        return user.getId();
    }

    @GetMapping("/profile")
    public ResponseEntity<WorkerProfile> getProfile() {
        String userId = getAuthenticatedWorkerUserId();
        return ResponseEntity.ok(workerService.getProfileByUserId(userId));
    }

    @PutMapping("/profile")
    public ResponseEntity<WorkerProfile> updateProfile(@RequestBody WorkerProfile profileInput) {
        String userId = getAuthenticatedWorkerUserId();
        return ResponseEntity.ok(workerService.updateProfile(userId, profileInput));
    }

    @PostMapping("/skills")
    public ResponseEntity<WorkerSkill> addSkill(@RequestBody WorkerSkill skillInput) {
        String userId = getAuthenticatedWorkerUserId();
        Long serviceId = skillInput.getServiceId();
        return ResponseEntity.ok(workerService.addSkill(userId, serviceId));
    }

    @DeleteMapping("/skills/{skillId}")
    public ResponseEntity<Void> removeSkill(@PathVariable Long skillId) {
        String userId = getAuthenticatedWorkerUserId();
        workerService.removeSkill(userId, skillId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/availability")
    public ResponseEntity<WorkerProfile> updateAvailability(@RequestParam WorkerAvailability availability) {
        String userId = getAuthenticatedWorkerUserId();
        return ResponseEntity.ok(workerService.updateAvailability(userId, availability));
    }
}
