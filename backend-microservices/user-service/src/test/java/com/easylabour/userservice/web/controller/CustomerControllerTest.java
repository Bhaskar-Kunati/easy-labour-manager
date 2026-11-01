package com.easylabour.userservice.web.controller;

import com.easylabour.userservice.application.service.CustomerService;
import com.easylabour.userservice.domain.model.CustomerProfile;
import com.easylabour.userservice.domain.model.User;
import com.easylabour.userservice.infrastructure.persistence.JpaUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private JpaUserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "+1234567890")
    void testGetMeSuccess() throws Exception {
        User user = User.builder().id("user-123").phoneNumber("+1234567890").build();
        CustomerProfile profile = CustomerProfile.builder()
                .id(1L)
                .user(user)
                .fullName("Test Customer")
                .email("test@example.com")
                .gender("Male")
                .build();

        Mockito.when(userRepository.findByPhoneNumber("+1234567890")).thenReturn(Optional.of(user));
        Mockito.when(customerService.getCustomerProfile("user-123")).thenReturn(profile);

        mockMvc.perform(get("/api/v1/customers/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Test Customer"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(username = "+1234567890")
    void testUpdateMeSuccess() throws Exception {
        User user = User.builder().id("user-123").phoneNumber("+1234567890").build();
        CustomerProfile profile = CustomerProfile.builder()
                .id(1L)
                .user(user)
                .fullName("Updated Name")
                .email("updated@example.com")
                .gender("Female")
                .build();

        Mockito.when(userRepository.findByPhoneNumber("+1234567890")).thenReturn(Optional.of(user));
        Mockito.when(customerService.updateCustomerProfile(eq("user-123"), any(CustomerProfile.class))).thenReturn(profile);

        mockMvc.perform(put("/api/v1/customers/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profile)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void testGetMeUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/customers/me"))
                .andExpect(status().isUnauthorized());
    }
}
