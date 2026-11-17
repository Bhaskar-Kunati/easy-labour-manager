package com.easylabour.bookingservice.security;

import com.easylabour.bookingservice.config.SecurityConfig;
import com.easylabour.bookingservice.domain.repository.BookingRepository;
import com.easylabour.bookingservice.web.controller.BookingInternalController;
import com.easylabour.common.security.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingInternalController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
    "booking.internal.admin-secret=admin-booking-secret-test"
})
class BookingInternalSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private com.easylabour.bookingservice.infrastructure.client.WalletClient walletClient;

    @MockBean
    private org.springframework.boot.web.client.RestTemplateBuilder restTemplateBuilder;

    @MockBean
    private org.springframework.web.client.RestTemplate restTemplate;

    @Test
    void testAnalyticsWithValidInternalKeySuccess() throws Exception {
        when(bookingRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/internal/bookings/analytics")
                        .header("X-Internal-Service-Key", "admin-booking-secret-test"))
                .andExpect(status().isOk());
    }

    @Test
    void testAnalyticsWithMissingInternalKeyRejected() throws Exception {
        mockMvc.perform(get("/api/internal/bookings/analytics"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAnalyticsWithBlankInternalKeyRejected() throws Exception {
        mockMvc.perform(get("/api/internal/bookings/analytics")
                        .header("X-Internal-Service-Key", "   "))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAnalyticsWithInvalidInternalKeyRejected() throws Exception {
        mockMvc.perform(get("/api/internal/bookings/analytics")
                        .header("X-Internal-Service-Key", "wrong-secret"))
                .andExpect(status().isUnauthorized());
    }
}
