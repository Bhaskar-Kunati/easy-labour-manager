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

    @Value("${wallet.internal.secret-key:internal-secret-key}")
    private String internalServiceKey;

    private void validateInternalKey(String internalKey) {
        if (internalKey == null || !internalKey.equals(internalServiceKey)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid or missing internal service key");
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
