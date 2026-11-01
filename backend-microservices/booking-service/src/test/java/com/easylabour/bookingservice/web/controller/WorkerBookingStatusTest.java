package com.easylabour.bookingservice.web.controller;

import com.easylabour.bookingservice.application.service.BookingService;
import com.easylabour.bookingservice.domain.model.Booking;
import com.easylabour.bookingservice.domain.model.BookingStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WorkerBookingStatusTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    @WithMockUser(username = "worker-1", roles = {"WORKER"})
    void testUpdateBookingStatusSuccess() throws Exception {
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.WORKER_ACCEPTED)
                .build();

        Mockito.when(bookingService.updateWorkerBookingStatus(anyLong(), anyString(), any(BookingStatus.class)))
                .thenReturn(booking);

        mockMvc.perform(put("/api/v1/workers/bookings/1/status")
                        .param("status", "WORKER_ACCEPTED")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "customer-123", roles = {"CUSTOMER"})
    void testUpdateBookingStatusForbiddenForCustomer() throws Exception {
        mockMvc.perform(put("/api/v1/workers/bookings/1/status")
                        .param("status", "WORKER_ACCEPTED")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateBookingStatusUnauthorized() throws Exception {
        mockMvc.perform(put("/api/v1/workers/bookings/1/status")
                        .param("status", "WORKER_ACCEPTED")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
