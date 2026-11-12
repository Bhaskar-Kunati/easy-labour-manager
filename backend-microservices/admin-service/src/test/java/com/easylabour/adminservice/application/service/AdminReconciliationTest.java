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

class AdminReconciliationTest {

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
    void testFinancialReconciliationDiscrepancyDetection() {
        when(bookingClient.getAnalytics()).thenReturn(Map.of("gmv", new BigDecimal("1000.00")));
        when(paymentClient.getAnalytics()).thenReturn(Map.of(
                "capturedPayments", new BigDecimal("1000.00"),
                "originalPaymentRefunds", BigDecimal.ZERO,
                "pendingRefunds", 1L,
                "unknownRefunds", 1L
        ));
        when(walletClient.getAnalytics()).thenReturn(Map.of(
                "walletTopups", new BigDecimal("800.00"),
                "workerEarningsCredits", new BigDecimal("200.00"),
                "walletRefundCredits", BigDecimal.ZERO
        ));

        Map<String, Object> recon = adminAnalyticsService.getFinancialReconciliation();

        assertNotNull(recon);
        assertEquals(0, BigDecimal.ZERO.compareTo((BigDecimal) recon.get("discrepancyAmount")));
        assertEquals(1L, recon.get("pendingTransactionsCount"));
        assertEquals(1L, recon.get("unknownTransactionsCount"));
    }
}
