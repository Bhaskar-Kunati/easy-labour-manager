package com.easylabour.bookingservice.application.service;

import com.easylabour.bookingservice.domain.model.Booking;
import com.easylabour.bookingservice.domain.model.BookingWorker;
import com.easylabour.bookingservice.domain.repository.BookingWorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkerAvailabilityService {
    private final BookingWorkerRepository bookingWorkerRepository;

    public boolean isWorkerAvailable(String workerId, LocalDateTime newStart, Integer durationMinutes) {
        if (newStart == null) {
            return true;
        }
        int duration = durationMinutes != null ? durationMinutes : 60;
        LocalDateTime newEnd = newStart.plusMinutes(duration);
        
        List<BookingWorker> assignments = bookingWorkerRepository.findByWorkerId(workerId);

        for (BookingWorker bw : assignments) {
            Booking b = bw.getBooking();
            if (b != null && b.getStatus() != null && b.getStatus().name().equals("CANCELLED")) {
                continue;
            }
            if (b != null && b.getServiceDateTime() != null) {
                LocalDateTime existingStart = b.getServiceDateTime();
                int existingDuration = b.getEstimatedDurationMinutes() != null ? b.getEstimatedDurationMinutes() : 60;
                LocalDateTime existingEnd = existingStart.plusMinutes(existingDuration);

                // Conflict: newStart < existingEnd AND newEnd > existingStart
                if (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)) {
                    return false;
                }
            }
        }
        return true;
    }
}
