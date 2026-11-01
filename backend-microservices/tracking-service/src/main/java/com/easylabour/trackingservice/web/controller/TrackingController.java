package com.easylabour.trackingservice.web.controller;

import com.easylabour.common.event.TrackingEvent;
import com.easylabour.trackingservice.application.service.TrackingService;
import com.easylabour.trackingservice.domain.model.LocationHistory;
import com.easylabour.trackingservice.domain.repository.LocationRepository;
import com.easylabour.trackingservice.infrastructure.kafka.BookingEventConsumer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingService trackingService;
    private final LocationRepository locationRepository;
    private final BookingEventConsumer bookingEventConsumer;

    @PostMapping("/update")
    @PreAuthorize("hasRole('WORKER') or hasRole('ADMIN') or hasRole('SYSTEM')")
    public ResponseEntity<Void> updateLocation(@RequestBody @Valid TrackingEvent event) {
        // Validate coordinates
        if (event.getLatitude() == null || event.getLatitude() < -90.0 || event.getLatitude() > 90.0 ||
            event.getLongitude() == null || event.getLongitude() < -180.0 || event.getLongitude() > 180.0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid latitude or longitude range");
        }
        if (event.getBookingId() == null || event.getBookingId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking ID is required");
        }

        // Check active booking state
        if (!bookingEventConsumer.isBookingActiveForTracking(event.getBookingId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Booking is not in an active tracking state");
        }

        trackingService.processLocationUpdate(event);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history/{bookingId}")
    public ResponseEntity<List<LocationHistory>> getHistory(@PathVariable String bookingId) {
        if (bookingId == null || bookingId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking ID is required");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        boolean isAdminOrSystem = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SYSTEM") || a.getAuthority().equals("ADMIN") || a.getAuthority().equals("SYSTEM"));

        String currentUserId = auth.getName();
        if (!isAdminOrSystem && !bookingEventConsumer.verifyCustomerOwnership(bookingId, currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized booking access");
        }

        List<LocationHistory> history = locationRepository.findByBookingId(bookingId);
        return ResponseEntity.ok(history);
    }
}
