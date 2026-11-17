package com.easylabour.bookingservice.web.controller;

import com.easylabour.bookingservice.domain.model.Booking;
import com.easylabour.bookingservice.domain.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/internal/bookings")
@RequiredArgsConstructor
public class BookingInternalController {

    private final BookingRepository bookingRepository;

    @Value("${booking.internal.admin-secret:}")
    private String internalServiceKey;

    private boolean secureEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        byte[] aBytes = a.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] bBytes = b.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return java.security.MessageDigest.isEqual(aBytes, bBytes);
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
    public ResponseEntity<Map<String, Object>> getBookingAnalytics(
            @RequestHeader(value = "X-Internal-Service-Key", required = false) String internalKey) {
        validateInternalKey(internalKey);

        List<Booking> bookings = bookingRepository.findAll();

        BigDecimal gmv = BigDecimal.ZERO;
        BigDecimal platformCommission = BigDecimal.ZERO;
        long activeBookings = 0;
        long completedBookings = 0;
        Map<String, Long> statusCounts = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getStatus().name(), Collectors.counting()));

        for (Booking b : bookings) {
            String status = b.getStatus().name();
            if ("COMPLETED".equals(status) || "SERVICE_COMPLETED".equals(status)) {
                gmv = gmv.add(b.getTotalAmount() != null ? b.getTotalAmount() : BigDecimal.ZERO);
                platformCommission = platformCommission.add(b.getPlatformFee() != null ? b.getPlatformFee() : BigDecimal.ZERO);
                completedBookings++;
            } else if ("CONFIRMED".equals(status) || "PENDING".equals(status) || "WORKER_ASSIGNED".equals(status)) {
                activeBookings++;
            }
        }

        Map<String, Object> result = Map.of(
                "gmv", gmv,
                "platformCommission", platformCommission,
                "activeBookings", activeBookings,
                "completedBookings", completedBookings,
                "statusCounts", statusCounts,
                "allBookings", bookings
        );

        return ResponseEntity.ok(result);
    }
}
