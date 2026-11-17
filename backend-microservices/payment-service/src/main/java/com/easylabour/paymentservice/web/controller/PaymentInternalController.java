package com.easylabour.paymentservice.web.controller;

import com.easylabour.paymentservice.domain.model.Payment;
import com.easylabour.paymentservice.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/internal/payments")
@RequiredArgsConstructor
public class PaymentInternalController {

    private final PaymentRepository paymentRepository;

    @Value("${payment.internal.admin-secret:}")
    private String internalServiceKey;

    private boolean secureEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
        byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(aBytes, bBytes);
    }

    private void validateInternalKey(String internalKey) {
        if (internalServiceKey == null || internalServiceKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal service key not configured");
        }
        if (!secureEquals(internalKey, internalServiceKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing internal service key");
        }
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getPaymentAnalytics(
            @RequestHeader(value = "X-Internal-Service-Key", required = false) String internalKey) {
        validateInternalKey(internalKey);

        List<Payment> payments = paymentRepository.findAll();
        BigDecimal capturedPayments = BigDecimal.ZERO;
        long totalPayments = payments.size();
        long successfulPayments = 0;
        long pendingPayments = 0;
        long failedPayments = 0;

        for (Payment p : payments) {
            String status = p.getPaymentStatus() != null ? p.getPaymentStatus() : "";
            BigDecimal amount = p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO;
            if ("SUCCESS".equalsIgnoreCase(status) || "PAID".equalsIgnoreCase(status)) {
                capturedPayments = capturedPayments.add(amount);
                successfulPayments++;
            } else if ("PENDING".equalsIgnoreCase(status)) {
                pendingPayments++;
            } else if ("FAILED".equalsIgnoreCase(status)) {
                failedPayments++;
            }
        }

        Map<String, Object> result = Map.of(
                "capturedPayments", capturedPayments,
                "totalPayments", totalPayments,
                "successfulPayments", successfulPayments,
                "pendingPayments", pendingPayments,
                "failedPayments", failedPayments
        );

        return ResponseEntity.ok(result);
    }
}
