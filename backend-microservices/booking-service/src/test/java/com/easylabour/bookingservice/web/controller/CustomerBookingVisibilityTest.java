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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerBookingVisibilityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    @WithMockUser(username = "+1234567890", roles = {"CUSTOMER"})
    void testCustomerViewsAssignedWorkerSummarySuccess() throws Exception {
        Booking booking = Booking.builder()
                .id(1L)
                .customerId("+1234567890")
                .serviceName("Plumbing Repair")
                .status(BookingStatus.WORKER_ASSIGNED)
                .build();
        booking.setAssignedWorkerName("John Doe");
        booking.setAssignedWorkerRating(4.8);
        booking.setAssignedWorkerProfileImage("profile.jpg");

        Mockito.when(bookingService.getBookingDetails(1L, "+1234567890")).thenReturn(booking);

        mockMvc.perform(get("/api/v1/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WORKER_ASSIGNED"))
                .andExpect(jsonPath("$.assignedWorkerName").value("John Doe"))
                .andExpect(jsonPath("$.assignedWorkerRating").value(4.8));
    }

    @Test
    @WithMockUser(username = "+9876543210", roles = {"CUSTOMER"})
    void testCustomerCannotViewOtherCustomerBooking() throws Exception {
        Mockito.when(bookingService.getBookingDetails(1L, "+9876543210"))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"));

        mockMvc.perform(get("/api/v1/bookings/1"))
                .andExpect(status().isForbidden());
    }
}
