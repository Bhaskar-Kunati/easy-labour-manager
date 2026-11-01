package com.easylabour.bookingservice.web.controller;

import com.easylabour.bookingservice.application.service.BookingService;
import com.easylabour.bookingservice.domain.model.Booking;
import com.easylabour.bookingservice.domain.model.BookingStatus;
import com.easylabour.bookingservice.web.dto.BookingRequest;
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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "+1234567890")
    void testCreateBookingSuccess() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setServiceId(1L);
        request.setServiceDateTime(LocalDateTime.now().plusDays(1));
        request.setLocation("123 Main St");

        Booking booking = Booking.builder()
                .id(1L)
                .customerId("+1234567890")
                .serviceId(1L)
                .serviceName("Plumbing Repair")
                .bookedPrice(BigDecimal.valueOf(500))
                .status(BookingStatus.PENDING)
                .serviceDateTime(request.getServiceDateTime())
                .location("123 Main St")
                .idempotencyKey("key-1")
                .build();

        Mockito.when(bookingService.createBooking(eq("+1234567890"), eq("key-1"), any(BookingRequest.class)))
                .thenReturn(booking);

        mockMvc.perform(post("/api/v1/bookings")
                        .header("X-Idempotency-Key", "key-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.serviceName").value("Plumbing Repair"));
    }

    @Test
    @WithMockUser(username = "+1234567890")
    void testCreateBookingPastDate() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setServiceId(1L);
        request.setServiceDateTime(LocalDateTime.now().minusDays(1));
        request.setLocation("123 Main St");

        Mockito.when(bookingService.createBooking(anyString(), anyString(), any(BookingRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Service date and time must be in the future"));

        mockMvc.perform(post("/api/v1/bookings")
                        .header("X-Idempotency-Key", "key-2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "+1234567890")
    void testGetCustomerBookings() throws Exception {
        Booking booking = Booking.builder()
                .id(1L)
                .customerId("+1234567890")
                .serviceId(1L)
                .serviceName("Plumbing Repair")
                .bookedPrice(BigDecimal.valueOf(500))
                .status(BookingStatus.PENDING)
                .location("123 Main St")
                .build();

        Mockito.when(bookingService.getCustomerBookings("+1234567890")).thenReturn(List.of(booking));

        mockMvc.perform(get("/api/v1/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].serviceName").value("Plumbing Repair"));
    }

    @Test
    @WithMockUser(username = "+1234567890")
    void testCancelBookingSuccess() throws Exception {
        Booking booking = Booking.builder()
                .id(1L)
                .customerId("+1234567890")
                .serviceId(1L)
                .serviceName("Plumbing Repair")
                .status(BookingStatus.CANCELLED)
                .build();

        Mockito.when(bookingService.cancelBooking(1L, "+1234567890")).thenReturn(booking);

        mockMvc.perform(put("/api/v1/bookings/1/cancel")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void testGetBookingsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/bookings"))
                .andExpect(status().isUnauthorized());
    }
}
