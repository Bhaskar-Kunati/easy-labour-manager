package com.easylabour.userservice.web.controller;

import com.easylabour.userservice.application.service.WorkerService;
import com.easylabour.userservice.domain.model.User;
import com.easylabour.userservice.domain.model.UserType;
import com.easylabour.userservice.domain.model.WorkerProfile;
import com.easylabour.userservice.infrastructure.persistence.JpaUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkerController.class)
class WorkerProfileControllerTest {

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
    void testGetProfileSuccess() throws Exception {
        User user = User.builder().id("user-1").phoneNumber("9876543210").userType(UserType.LABOUR).build();
        WorkerProfile profile = WorkerProfile.builder()
                .id(1L)
                .user(user)
                .fullName("John Worker")
                .experienceYears(4)
                .workingRadius(12)
                .build();

        when(userRepository.findByPhoneNumber("9876543210")).thenReturn(Optional.of(user));
        when(workerService.getProfileByUserId("user-1")).thenReturn(profile);

        mockMvc.perform(get("/api/v1/workers/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("John Worker"))
                .andExpect(jsonPath("$.experienceYears").value(4));
    }

    @Test
    @WithMockUser(username = "9876543210")
    void testUpdateProfileSuccess() throws Exception {
        User user = User.builder().id("user-1").phoneNumber("9876543210").userType(UserType.LABOUR).build();
        WorkerProfile updatedProfile = WorkerProfile.builder()
                .id(1L)
                .user(user)
                .fullName("John Updated")
                .experienceYears(5)
                .workingRadius(15)
                .build();

        when(userRepository.findByPhoneNumber("9876543210")).thenReturn(Optional.of(user));
        when(workerService.updateProfile(eq("user-1"), any(WorkerProfile.class))).thenReturn(updatedProfile);

        mockMvc.perform(put("/api/v1/workers/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProfile)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("John Updated"))
                .andExpect(jsonPath("$.experienceYears").value(5));
    }

    @Test
    @WithMockUser(username = "1111111111")
    void testCustomerAccessDenied() throws Exception {
        User user = User.builder().id("cust-1").phoneNumber("1111111111").userType(UserType.CUSTOMER).build();

        when(userRepository.findByPhoneNumber("1111111111")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/workers/profile"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUnauthenticatedGetProfile() throws Exception {
        mockMvc.perform(get("/api/v1/workers/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUnauthenticatedPutProfile() throws Exception {
        WorkerProfile profile = WorkerProfile.builder().fullName("Test").build();
        mockMvc.perform(put("/api/v1/workers/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profile)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "1111111111")
    void testCustomerPutProfileDenied() throws Exception {
        User user = User.builder().id("cust-1").phoneNumber("1111111111").userType(UserType.CUSTOMER).build();
        WorkerProfile profile = WorkerProfile.builder().fullName("Test").build();

        when(userRepository.findByPhoneNumber("1111111111")).thenReturn(Optional.of(user));

        mockMvc.perform(put("/api/v1/workers/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profile)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "9876543210")
    void testInvalidProfileDataRejected() throws Exception {
        User user = User.builder().id("user-1").phoneNumber("9876543210").userType(UserType.LABOUR).build();
        WorkerProfile profile = WorkerProfile.builder().fullName("Test").build();

        when(userRepository.findByPhoneNumber("9876543210")).thenReturn(Optional.of(user));
        when(workerService.updateProfile(eq("user-1"), any(WorkerProfile.class)))
                .thenThrow(new IllegalArgumentException("Invalid profile data"));

        mockMvc.perform(put("/api/v1/workers/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profile)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid profile data"));
    }
}
