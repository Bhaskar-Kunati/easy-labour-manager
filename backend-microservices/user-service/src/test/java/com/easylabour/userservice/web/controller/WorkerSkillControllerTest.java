package com.easylabour.userservice.web.controller;

import com.easylabour.userservice.application.service.WorkerService;
import com.easylabour.userservice.domain.model.User;
import com.easylabour.userservice.domain.model.UserType;
import com.easylabour.userservice.domain.model.WorkerProfile;
import com.easylabour.userservice.domain.model.WorkerSkill;
import com.easylabour.userservice.infrastructure.persistence.JpaUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkerController.class)
class WorkerSkillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WorkerService workerService;

    @MockBean
    private JpaUserRepository userRepository;

    @Test
    @WithMockUser(username = "9876543210")
    void testAddSkillSuccess() throws Exception {
        User user = User.builder().id("user-1").phoneNumber("9876543210").userType(UserType.LABOUR).build();
        WorkerProfile worker = WorkerProfile.builder().id(1L).user(user).build();
        WorkerSkill skill = WorkerSkill.builder().id(10L).worker(worker).serviceId(100L).build();

        when(userRepository.findByPhoneNumber("9876543210")).thenReturn(Optional.of(user));
        when(workerService.addSkill(eq("user-1"), eq(100L))).thenReturn(skill);

        mockMvc.perform(post("/api/v1/workers/skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(skill)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.serviceId").value(100));
    }

    @Test
    @WithMockUser(username = "9876543210")
    void testRemoveSkillSuccess() throws Exception {
        User user = User.builder().id("user-1").phoneNumber("9876543210").userType(UserType.LABOUR).build();

        when(userRepository.findByPhoneNumber("9876543210")).thenReturn(Optional.of(user));
        org.mockito.Mockito.doNothing().when(workerService).removeSkill(eq("user-1"), eq(10L));

        mockMvc.perform(delete("/api/v1/workers/skills/10"))
                .andExpect(status().isOk());
    }

    @Test
    void testUnauthenticatedAddSkill() throws Exception {
        WorkerSkill skill = WorkerSkill.builder().serviceId(100L).build();
        mockMvc.perform(post("/api/v1/workers/skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(skill)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "1111111111")
    void testCustomerAddSkillDenied() throws Exception {
        User user = User.builder().id("cust-1").phoneNumber("1111111111").userType(UserType.CUSTOMER).build();
        WorkerSkill skill = WorkerSkill.builder().serviceId(100L).build();

        when(userRepository.findByPhoneNumber("1111111111")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/v1/workers/skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(skill)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUnauthenticatedRemoveSkill() throws Exception {
        mockMvc.perform(delete("/api/v1/workers/skills/10"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "1111111111")
    void testCustomerRemoveSkillDenied() throws Exception {
        User user = User.builder().id("cust-1").phoneNumber("1111111111").userType(UserType.CUSTOMER).build();

        when(userRepository.findByPhoneNumber("1111111111")).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/api/v1/workers/skills/10"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "9876543210")
    void testCrossWorkerRemoveSkillDenied() throws Exception {
        User user = User.builder().id("user-1").phoneNumber("9876543210").userType(UserType.LABOUR).build();

        when(userRepository.findByPhoneNumber("9876543210")).thenReturn(Optional.of(user));
        org.mockito.Mockito.doThrow(new AccessDeniedException("Worker does not own this skill"))
                .when(workerService).removeSkill(eq("user-1"), eq(10L));

        mockMvc.perform(delete("/api/v1/workers/skills/10"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "9876543210")
    void testDuplicateSkillRejected() throws Exception {
        User user = User.builder().id("user-1").phoneNumber("9876543210").userType(UserType.LABOUR).build();
        WorkerSkill skill = WorkerSkill.builder().serviceId(100L).build();

        when(userRepository.findByPhoneNumber("9876543210")).thenReturn(Optional.of(user));
        when(workerService.addSkill(eq("user-1"), eq(100L)))
                .thenThrow(new IllegalStateException("Worker already has this skill"));

        mockMvc.perform(post("/api/v1/workers/skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(skill)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Worker already has this skill"));
    }

    @Test
    @WithMockUser(username = "9876543210")
    void testInvalidCatalogServiceRejected() throws Exception {
        User user = User.builder().id("user-1").phoneNumber("9876543210").userType(UserType.LABOUR).build();
        WorkerSkill skill = WorkerSkill.builder().serviceId(999L).build();

        when(userRepository.findByPhoneNumber("9876543210")).thenReturn(Optional.of(user));
        when(workerService.addSkill(eq("user-1"), eq(999L)))
                .thenThrow(new IllegalArgumentException("Catalog skill/service not found"));

        mockMvc.perform(post("/api/v1/workers/skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(skill)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Catalog skill/service not found"));
    }
}
