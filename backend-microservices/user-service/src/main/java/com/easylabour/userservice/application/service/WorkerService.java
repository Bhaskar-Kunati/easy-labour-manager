package com.easylabour.userservice.application.service;

import com.easylabour.userservice.domain.model.*;
import com.easylabour.userservice.domain.repository.WorkerProfileRepository;
import com.easylabour.userservice.domain.repository.WorkerSkillRepository;
import com.easylabour.userservice.infrastructure.persistence.JpaSkillRepository;
import com.easylabour.userservice.infrastructure.persistence.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkerService {
    private final WorkerProfileRepository workerProfileRepository;
    private final WorkerSkillRepository workerSkillRepository;
    private final JpaSkillRepository skillRepository;
    private final JpaUserRepository userRepository;

    @Transactional(readOnly = true)
    public WorkerProfile getProfileByUserId(String userId) {
        return workerProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Worker profile not found"));
    }

    @Transactional
    public WorkerProfile updateProfile(String userId, WorkerProfile profileInput) {
        WorkerProfile worker = workerProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Worker profile not found"));

        // Update only worker-editable fields
        if (profileInput.getFullName() != null) {
            worker.setFullName(profileInput.getFullName());
        }
        if (profileInput.getProfileImage() != null) {
            worker.setProfileImage(profileInput.getProfileImage());
        }
        if (profileInput.getExperienceYears() != null) {
            worker.setExperienceYears(profileInput.getExperienceYears());
        }
        if (profileInput.getWorkingRadius() != null) {
            worker.setWorkingRadius(profileInput.getWorkingRadius());
        }

        // Protected fields (verificationStatus, rating, totalJobs, availabilityStatus) remain unchanged.
        return workerProfileRepository.save(worker);
    }

    @Transactional
    public WorkerSkill addSkill(String userId, Long serviceId) {
        if (serviceId == null) {
            throw new IllegalArgumentException("Service ID is required");
        }
        // Validate catalog skill exists
        if (!skillRepository.existsById(serviceId)) {
            throw new IllegalArgumentException("Catalog skill/service not found");
        }

        WorkerProfile worker = workerProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Worker profile not found"));

        // Check for duplicate skill
        boolean exists = workerSkillRepository.findByWorkerId(worker.getId()).stream()
            .anyMatch(ws -> ws.getServiceId().equals(serviceId));
        if (exists) {
            throw new IllegalStateException("Worker already has this skill");
        }

        WorkerSkill skill = WorkerSkill.builder()
            .worker(worker)
            .serviceId(serviceId)
            .status("ACTIVE")
            .build();

        return workerSkillRepository.save(skill);
    }

    @Transactional
    public void removeSkill(String userId, Long skillId) {
        WorkerProfile worker = workerProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Worker profile not found"));

        WorkerSkill skill = workerSkillRepository.findById(skillId)
            .orElseThrow(() -> new RuntimeException("Worker skill not found"));

        if (!skill.getWorker().getId().equals(worker.getId())) {
            throw new AccessDeniedException("Worker does not own this skill");
        }

        workerSkillRepository.delete(skill);
    }

    @Transactional
    public WorkerProfile updateAvailability(String userId, WorkerAvailability availability) {
        WorkerProfile worker = workerProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Worker profile not found"));
        
        if (availability == WorkerAvailability.AVAILABLE) {
            // Validate before making available
            if (worker.getVerificationStatus() != WorkerVerificationStatus.APPROVED) {
                throw new RuntimeException("Worker not verified");
            }
        }
        
        worker.setAvailabilityStatus(availability);
        return workerProfileRepository.save(worker);
    }
}
