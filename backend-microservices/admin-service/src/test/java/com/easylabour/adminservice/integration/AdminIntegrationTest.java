package com.easylabour.adminservice.integration;

import com.easylabour.adminservice.application.service.AdminAnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
})
class AdminIntegrationTest {

    @Autowired
    private AdminAnalyticsService adminAnalyticsService;

    @MockBean
    private com.easylabour.adminservice.infrastructure.client.BookingClient bookingClient;

    @MockBean
    private com.easylabour.adminservice.infrastructure.client.PaymentClient paymentClient;

    @MockBean
    private com.easylabour.adminservice.infrastructure.client.WalletClient walletClient;

    @Test
    void testIntegrationWorkflow() {
        when(bookingClient.getAnalytics()).thenReturn(Map.of("gmv", new BigDecimal("5000.00"), "completedBookings", 10L));
        when(paymentClient.getAnalytics()).thenReturn(Map.of("capturedPayments", new BigDecimal("5000.00")));
        when(walletClient.getAnalytics()).thenReturn(Map.of("walletLiabilities", new BigDecimal("1000.00"), "workerEarningsCredits", new BigDecimal("4000.00")));

        Map<String, Object> overview = adminAnalyticsService.getOverview();
        assertNotNull(overview);
        assertEquals(10L, overview.get("completedBookings"));
    }
}
