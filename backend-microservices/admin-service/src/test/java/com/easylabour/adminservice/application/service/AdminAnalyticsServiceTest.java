package com.easylabour.adminservice.application.service;

import com.easylabour.adminservice.infrastructure.client.BookingClient;
import com.easylabour.adminservice.infrastructure.client.PaymentClient;
import com.easylabour.adminservice.infrastructure.client.WalletClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AdminAnalyticsServiceTest {

    @Mock
    private BookingClient bookingClient;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private WalletClient walletClient;

    @InjectMocks
    private AdminAnalyticsService adminAnalyticsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetOverviewAggregatesCorrectly() {
        when(bookingClient.getAnalytics()).thenReturn(Map.of(
                "gmv", new BigDecimal("1000.00"),
                "platformCommission", new BigDecimal("100.00"),
                "activeBookings", 2L,
                "completedBookings", 5L
        ));
        when(paymentClient.getAnalytics()).thenReturn(Map.of(
                "capturedPayments", new BigDecimal("1000.00")
        ));
        when(walletClient.getAnalytics()).thenReturn(Map.of(
                "walletLiabilities", new BigDecimal("500.00"),
                "workerEarningsCredits", new BigDecimal("900.00")
        ));

        Map<String, Object> overview = adminAnalyticsService.getOverview();

        assertNotNull(overview);
        assertEquals(0, new BigDecimal("1000.00").compareTo((BigDecimal) overview.get("gmv")));
        assertEquals(0, new BigDecimal("100.00").compareTo((BigDecimal) overview.get("platformCommission")));
        assertEquals(5L, overview.get("completedBookings"));
    }
}
