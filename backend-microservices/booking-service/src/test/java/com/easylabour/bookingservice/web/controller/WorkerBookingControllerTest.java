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
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WorkerBookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    @WithMockUser(username = "worker-1", roles = {"WORKER"})
    void testGetAssignedBookingsSuccess() throws Exception {
        Booking booking = Booking.builder()
                .id(1L)
                .customerId("+1234567890")
                .serviceId(1L)
                .serviceName("Plumbing Repair")
                .status(BookingStatus.WORKER_ASSIGNED)
                .build();

        Mockito.when(bookingService.getAssignedBookings("worker-1")).thenReturn(List.of(booking));

        mockMvc.perform(get("/api/v1/workers/bookings"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "worker-1", roles = {"WORKER"})
    void testGetBookingDetailsSuccess() throws Exception {
        Booking booking = Booking.builder()
                .id(1L)
                .customerId("+1234567890")
                .serviceId(1L)
                .serviceName("Plumbing Repair")
                .status(BookingStatus.WORKER_ASSIGNED)
                .build();

        Mockito.when(bookingService.getWorkerBookingDetails(1L, "worker-1")).thenReturn(booking);

        mockMvc.perform(get("/api/v1/workers/bookings/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "worker-2", roles = {"WORKER"})
    void testGetBookingDetailsForbiddenForOtherWorker() throws Exception {
        Mockito.when(bookingService.getWorkerBookingDetails(1L, "worker-2"))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"));

        mockMvc.perform(get("/api/v1/workers/bookings/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "customer-123", roles = {"CUSTOMER"})
    void testGetWorkerBookingsForbiddenForCustomer() throws Exception {
        mockMvc.perform(get("/api/v1/workers/bookings"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetWorkerBookingsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/workers/bookings"))
                .andExpect(status().isUnauthorized());
    }
}
