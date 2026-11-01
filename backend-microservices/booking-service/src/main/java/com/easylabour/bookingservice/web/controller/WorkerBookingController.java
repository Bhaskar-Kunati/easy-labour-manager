package com.easylabour.bookingservice.web.controller;

import com.easylabour.bookingservice.application.service.BookingService;
import com.easylabour.bookingservice.domain.model.Booking;
import com.easylabour.bookingservice.domain.model.BookingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workers/bookings")
@RequiredArgsConstructor
public class WorkerBookingController {

    private final BookingService bookingService;

    @GetMapping
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<List<Booking>> getAssignedBookings(Authentication authentication) {
        String workerId = authentication.getName();
        return ResponseEntity.ok(bookingService.getAssignedBookings(workerId));
    }

    @GetMapping("/{bookingId}")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<Booking> getBookingDetails(@PathVariable Long bookingId, Authentication authentication) {
        String workerId = authentication.getName();
        return ResponseEntity.ok(bookingService.getWorkerBookingDetails(bookingId, workerId));
    }

    @PutMapping("/{bookingId}/status")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<Booking> updateBookingStatus(
            @PathVariable Long bookingId,
            @RequestParam BookingStatus status,
            Authentication authentication) {
        String workerId = authentication.getName();
        return ResponseEntity.ok(bookingService.updateWorkerBookingStatus(bookingId, workerId, status));
    }
}
