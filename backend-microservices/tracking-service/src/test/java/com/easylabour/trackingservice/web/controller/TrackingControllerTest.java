package com.easylabour.trackingservice.web.controller;

import com.easylabour.trackingservice.application.service.TrackingService;
import com.easylabour.trackingservice.domain.model.LocationHistory;
import com.easylabour.trackingservice.domain.repository.LocationRepository;
import com.easylabour.trackingservice.infrastructure.kafka.BookingEventConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrackingController.class)
class TrackingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrackingService trackingService;

    @MockBean
    private LocationRepository locationRepository;

    @MockBean
    private BookingEventConsumer bookingEventConsumer;

    @BeforeEach
    void setUp() {
        Mockito.when(bookingEventConsumer.isBookingActiveForTracking(any())).thenReturn(true);
        Mockito.when(bookingEventConsumer.verifyCustomerOwnership(any(), any())).thenReturn(true);
    }

    @Test
    @WithMockUser(username = "customer-123")
    void testGetHistoryAuthorized() throws Exception {
        Mockito.when(locationRepository.findByBookingId("booking-123"))
                .thenReturn(List.of(LocationHistory.builder()
                        .id("1").bookingId("booking-123").latitude(12.0).longitude(77.0).timestamp(LocalDateTime.now()).build()));

        mockMvc.perform(get("/api/tracking/history/booking-123"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "customer-456")
    void testGetHistoryForbiddenForOtherCustomer() throws Exception {
        Mockito.when(bookingEventConsumer.verifyCustomerOwnership("booking-123", "customer-456")).thenReturn(false);

        mockMvc.perform(get("/api/tracking/history/booking-123"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "customer-123", roles = {"CUSTOMER"})
    void testUpdateLocationForbiddenForCustomer() throws Exception {
        mockMvc.perform(post("/api/tracking/update")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookingId\":\"booking-123\",\"latitude\":12.0,\"longitude\":77.0}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "worker-1", roles = {"WORKER"})
    void testUpdateLocationDeniedUnknownBooking() throws Exception {
        Mockito.when(bookingEventConsumer.isBookingActiveForTracking("booking-123")).thenReturn(false);

        mockMvc.perform(post("/api/tracking/update")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookingId\":\"booking-123\",\"latitude\":12.0,\"longitude\":77.0}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "worker-1")
    void testUpdateLocationValid() throws Exception {
        mockMvc.perform(post("/api/tracking/update")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookingId\":\"booking-123\",\"latitude\":12.0,\"longitude\":77.0}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "worker-1")
    void testUpdateLocationInvalidCoordinates() throws Exception {
        mockMvc.perform(post("/api/tracking/update")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookingId\":\"booking-123\",\"latitude\":100.0,\"longitude\":77.0}"))
                .andExpect(status().isBadRequest());
    }
}
