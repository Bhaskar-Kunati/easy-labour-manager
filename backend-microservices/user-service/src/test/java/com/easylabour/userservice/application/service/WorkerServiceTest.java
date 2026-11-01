package com.easylabour.userservice.application.service;

import com.easylabour.userservice.domain.model.*;
import com.easylabour.userservice.domain.repository.WorkerProfileRepository;
import com.easylabour.userservice.domain.repository.WorkerSkillRepository;
import com.easylabour.userservice.infrastructure.persistence.JpaSkillRepository;
import com.easylabour.userservice.infrastructure.persistence.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class WorkerServiceTest {

    @Mock
    private WorkerProfileRepository workerProfileRepository;

    @Mock
    private WorkerSkillRepository workerSkillRepository;

    @Mock
    private JpaSkillRepository skillRepository;

    @Mock
    private JpaUserRepository userRepository;

    @InjectMocks
    private WorkerService workerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateProfileProtectsFields() {
        User user = User.builder().id("user-1").build();
        WorkerProfile existing = WorkerProfile.builder()
                .id(1L)
                .user(user)
                .fullName("Old Name")
                .experienceYears(2)
                .workingRadius(10)
                .verificationStatus(WorkerVerificationStatus.PENDING)
                .rating(4.5)
                .totalJobs(5)
                .availabilityStatus(WorkerAvailability.OFFLINE)
                .build();

        WorkerProfile input = WorkerProfile.builder()
                .fullName("New Name")
                .experienceYears(5)
                .workingRadius(20)
                .verificationStatus(WorkerVerificationStatus.APPROVED) // should be ignored
                .rating(5.0) // should be ignored
                .totalJobs(100) // should be ignored
                .availabilityStatus(WorkerAvailability.AVAILABLE) // should be ignored
                .build();

        when(workerProfileRepository.findByUserId("user-1")).thenReturn(Optional.of(existing));
        when(workerProfileRepository.save(any(WorkerProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        WorkerProfile updated = workerService.updateProfile("user-1", input);

        assertEquals("New Name", updated.getFullName());
        assertEquals(5, updated.getExperienceYears());
        assertEquals(20, updated.getWorkingRadius());
        // Protected fields must remain unchanged
        assertEquals(WorkerVerificationStatus.PENDING, updated.getVerificationStatus());
        assertEquals(4.5, updated.getRating());
        assertEquals(5, updated.getTotalJobs());
        assertEquals(WorkerAvailability.OFFLINE, updated.getAvailabilityStatus());
    }

    @Test
    void addSkillValidatesCatalogAndDuplicate() {
        User user = User.builder().id("user-1").build();
        WorkerProfile worker = WorkerProfile.builder().id(1L).user(user).build();

        when(workerProfileRepository.findByUserId("user-1")).thenReturn(Optional.of(worker));
        when(skillRepository.existsById(100L)).thenReturn(false);

        // Catalog skill not found -> IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> workerService.addSkill("user-1", 100L));

        when(skillRepository.existsById(100L)).thenReturn(true);
        when(workerSkillRepository.findByWorkerId(1L)).thenReturn(Collections.singletonList(
                WorkerSkill.builder().id(10L).worker(worker).serviceId(100L).build()
        ));

        // Duplicate skill -> IllegalStateException
        assertThrows(IllegalStateException.class, () -> workerService.addSkill("user-1", 100L));
    }

    @Test
    void removeSkillEnforcesOwnership() {
        User user1 = User.builder().id("user-1").build();
        WorkerProfile worker1 = WorkerProfile.builder().id(1L).user(user1).build();

        User user2 = User.builder().id("user-2").build();
        WorkerProfile worker2 = WorkerProfile.builder().id(2L).user(user2).build();

        WorkerSkill skill = WorkerSkill.builder().id(10L).worker(worker2).serviceId(100L).build();

        when(workerProfileRepository.findByUserId("user-1")).thenReturn(Optional.of(worker1));
        when(workerSkillRepository.findById(10L)).thenReturn(Optional.of(skill));

        // Cross-worker skill removal -> AccessDeniedException
        assertThrows(AccessDeniedException.class, () -> workerService.removeSkill("user-1", 10L));
    }
}
