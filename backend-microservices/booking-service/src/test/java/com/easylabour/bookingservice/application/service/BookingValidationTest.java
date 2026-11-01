package com.easylabour.bookingservice.application.service;

import com.easylabour.bookingservice.domain.model.Booking;
import com.easylabour.bookingservice.domain.model.BookingStatus;
import com.easylabour.bookingservice.domain.repository.BookingRepository;
import com.easylabour.bookingservice.domain.repository.BookingWorkerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
class BookingValidationTest {

    @Autowired
    private BookingService bookingService;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private BookingWorkerRepository bookingWorkerRepository;

    @MockBean
    private WorkerAvailabilityService workerAvailabilityService;

    @Test
    void testAssignWorkerSchedulingConflictThrowsException() {
        Booking booking = Booking.builder()
                .id(1L)
                .serviceDateTime(LocalDateTime.now().plusDays(1))
                .estimatedDurationMinutes(60)
                .status(BookingStatus.PENDING)
                .assignedWorkerCount(0)
                .build();

        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        Mockito.when(bookingWorkerRepository.findByBookingIdAndWorkerId(1L, "worker-1")).thenReturn(Optional.empty());
        Mockito.when(workerAvailabilityService.isWorkerAvailable(anyString(), any(LocalDateTime.now().getClass()), any())).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> {
            bookingService.assignWorker(1L, "worker-1");
        });
    }

    @Test
    void testAssignWorkerDuplicateAssignmentThrowsException() {
        Booking booking = Booking.builder()
                .id(1L)
                .serviceDateTime(LocalDateTime.now().plusDays(1))
                .estimatedDurationMinutes(60)
                .status(BookingStatus.PENDING)
                .assignedWorkerCount(0)
                .build();

        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        Mockito.when(bookingWorkerRepository.findByBookingIdAndWorkerId(1L, "worker-1"))
                .thenReturn(Optional.of(com.easylabour.bookingservice.domain.model.BookingWorker.builder().id(1L).build()));

        assertThrows(ResponseStatusException.class, () -> {
            bookingService.assignWorker(1L, "worker-1");
        });
    }
}
