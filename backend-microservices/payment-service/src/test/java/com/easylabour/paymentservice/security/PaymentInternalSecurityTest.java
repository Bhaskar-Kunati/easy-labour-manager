package com.easylabour.paymentservice.security;

import com.easylabour.paymentservice.config.SecurityConfig;
import com.easylabour.paymentservice.domain.repository.PaymentRepository;
import com.easylabour.paymentservice.web.controller.PaymentInternalController;
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

@WebMvcTest(PaymentInternalController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
    "payment.internal.admin-secret=admin-payment-secret-test"
})
class PaymentInternalSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentRepository paymentRepository;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @Test
    void testAnalyticsWithValidInternalKeySuccess() throws Exception {
        when(paymentRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/internal/payments/analytics")
                        .header("X-Internal-Service-Key", "admin-payment-secret-test"))
                .andExpect(status().isOk());
    }

    @Test
    void testAnalyticsWithMissingInternalKeyRejected() throws Exception {
        mockMvc.perform(get("/api/internal/payments/analytics"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAnalyticsWithInvalidInternalKeyRejected() throws Exception {
        mockMvc.perform(get("/api/internal/payments/analytics")
                        .header("X-Internal-Service-Key", "wrong-secret"))
                .andExpect(status().isUnauthorized());
    }
}
