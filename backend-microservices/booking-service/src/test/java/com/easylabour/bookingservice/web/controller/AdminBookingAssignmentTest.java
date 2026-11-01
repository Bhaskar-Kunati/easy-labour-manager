package com.easylabour.bookingservice.web.controller;

import com.easylabour.bookingservice.application.service.BookingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminBookingAssignmentTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testAssignWorkerSuccess() throws Exception {
        Mockito.doNothing().when(bookingService).assignWorker(1L, "worker-1");

        mockMvc.perform(post("/api/v1/admin/bookings/1/assign-worker")
                        .param("workerId", "worker-1")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testAssignWorkerSchedulingConflict() throws Exception {
        Mockito.doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Scheduling conflict"))
                .when(bookingService).assignWorker(1L, "worker-1");

        mockMvc.perform(post("/api/v1/admin/bookings/1/assign-worker")
                        .param("workerId", "worker-1")
                        .with(csrf()))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = {"WORKER"})
    void testAssignWorkerForbiddenForWorker() throws Exception {
        mockMvc.perform(post("/api/v1/admin/bookings/1/assign-worker")
                        .param("workerId", "worker-1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void testAssignWorkerForbiddenForCustomer() throws Exception {
        mockMvc.perform(post("/api/v1/admin/bookings/1/assign-worker")
                        .param("workerId", "worker-1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAssignWorkerUnauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/admin/bookings/1/assign-worker")
                        .param("workerId", "worker-1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
