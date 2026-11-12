package com.easylabour.adminservice.application.service;

import com.easylabour.adminservice.infrastructure.client.BookingClient;
import com.easylabour.adminservice.infrastructure.client.PaymentClient;
import com.easylabour.adminservice.infrastructure.client.WalletClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminAnalyticsService {

    private final BookingClient bookingClient;
    private final PaymentClient paymentClient;
    private final WalletClient walletClient;

    public Map<String, Object> getOverview() {
        Map<String, Object> bookingData = fetchBookingData();
        Map<String, Object> paymentData = fetchPaymentData();
        Map<String, Object> walletData = fetchWalletData();

        BigDecimal gmv = toBigDecimal(bookingData.get("gmv"));
        BigDecimal platformCommission = toBigDecimal(bookingData.get("platformCommission"));
        long activeBookings = toLong(bookingData.get("activeBookings"));
        long completedBookings = toLong(bookingData.get("completedBookings"));

        BigDecimal capturedPayments = toBigDecimal(paymentData.get("capturedPayments"));
        BigDecimal walletLiabilities = toBigDecimal(walletData.get("walletLiabilities"));
        BigDecimal workerEarnings = toBigDecimal(walletData.get("workerEarningsCredits"));

        return Map.of(
                "gmv", gmv,
                "totalCapturedPayments", capturedPayments,
                "activeBookings", activeBookings,
                "completedBookings", completedBookings,
                "platformCommission", platformCommission,
                "workerEarnings", workerEarnings,
                "customerWalletLiabilities", walletLiabilities,
                "dataFreshnessTimestamp", LocalDateTime.now()
        );
    }

    public Map<String, Object> getFinancialReconciliation() {
        Map<String, Object> bookingData = fetchBookingData();
        Map<String, Object> paymentData = fetchPaymentData();
        Map<String, Object> walletData = fetchWalletData();

        BigDecimal capturedPayments = toBigDecimal(paymentData.get("capturedPayments"));
        BigDecimal walletTopups = toBigDecimal(walletData.get("walletTopups"));
        BigDecimal workerEarningsCredits = toBigDecimal(walletData.get("workerEarningsCredits"));
        BigDecimal walletRefundCredits = toBigDecimal(walletData.get("walletRefundCredits"));
        BigDecimal originalPaymentRefunds = toBigDecimal(paymentData.get("originalPaymentRefunds"));
        long pendingRefunds = toLong(paymentData.get("pendingRefunds"));
        long unknownRefunds = toLong(paymentData.get("unknownRefunds"));

        BigDecimal discrepancy = capturedPayments.subtract(walletTopups.add(workerEarningsCredits).add(originalPaymentRefunds));

        return Map.of(
                "totalPaymentsCaptured", capturedPayments,
                "totalWalletTopups", walletTopups,
                "totalWorkerPayouts", workerEarningsCredits,
                "totalRefundsWallet", walletRefundCredits,
                "totalRefundsGateway", originalPaymentRefunds,
                "discrepancyAmount", discrepancy,
                "pendingTransactionsCount", pendingRefunds,
                "unknownTransactionsCount", unknownRefunds,
                "dataFreshnessTimestamp", LocalDateTime.now()
        );
    }

    public Map<String, Object> getBookingsSummary() {
        Map<String, Object> bookingData = fetchBookingData();
        long completedBookings = toLong(bookingData.get("completedBookings"));
        BigDecimal gmv = toBigDecimal(bookingData.get("gmv"));
        Object statusCounts = bookingData.get("statusCounts");

        return Map.of(
                "statusCounts", statusCounts != null ? statusCounts : Map.of(),
                "completedBookings", completedBookings,
                "totalRevenue", gmv,
                "dataFreshnessTimestamp", LocalDateTime.now()
        );
    }

    private Map<String, Object> fetchBookingData() {
        try {
            Map<String, Object> data = bookingClient.getAnalytics();
            if (data == null) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Booking service returned empty analytics response");
            }
            return data;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Failed to communicate with booking-service: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> fetchPaymentData() {
        try {
            Map<String, Object> data = paymentClient.getAnalytics();
            if (data == null) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Payment service returned empty analytics response");
            }
            return data;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Failed to communicate with payment-service: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> fetchWalletData() {
        try {
            Map<String, Object> data = walletClient.getAnalytics();
            if (data == null) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Wallet service returned empty analytics response");
            }
            return data;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Failed to communicate with wallet-service: " + e.getMessage(), e);
        }
    }

    private BigDecimal toBigDecimal(Object val) {
        if (val == null) return BigDecimal.ZERO;
        if (val instanceof BigDecimal) return (BigDecimal) val;
        try {
            return new BigDecimal(val.toString());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private long toLong(Object val) {
        if (val == null) return 0L;
        if (val instanceof Number) return ((Number) val).longValue();
        try {
            return Long.parseLong(val.toString());
        } catch (Exception e) {
            return 0L;
        }
    }
}
