package com.easylabour.bookingservice.integration;

import com.easylabour.bookingservice.application.service.BookingOutboxProcessor;
import com.easylabour.bookingservice.application.service.BookingService;
import com.easylabour.bookingservice.domain.model.*;
import com.easylabour.bookingservice.domain.repository.BookingOutboxRepository;
import com.easylabour.bookingservice.domain.repository.BookingRepository;
import com.easylabour.bookingservice.domain.repository.BookingWorkerRepository;
import com.easylabour.bookingservice.infrastructure.client.WalletClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class Feature014EndToEndIntegrationTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingWorkerRepository bookingWorkerRepository;

    @Mock
    private BookingOutboxRepository bookingOutboxRepository;

    @Mock
    private WalletClient walletClient;

    @InjectMocks
    private BookingService bookingService;

    @InjectMocks
    private BookingOutboxProcessor outboxProcessor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testEndToEndWorkerEarningsFlow() {
        Booking booking = Booking.builder()
                .id(777L)
                .customerId("cust-99")
                .totalAmount(new BigDecimal("1200.00"))
                .status(BookingStatus.CONFIRMED)
                .build();

        BookingWorker worker = BookingWorker.builder()
                .id(1L)
                .booking(booking)
                .workerId("worker-77")
                .status(BookingWorkerStatus.ACCEPTED)
                .build();

        when(bookingRepository.findById(777L)).thenReturn(Optional.of(booking));
        when(bookingWorkerRepository.findByBookingId(777L)).thenReturn(List.of(worker));
        when(bookingOutboxRepository.findByReferenceId("BOOKING_EARNINGS_777")).thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        // 1. Complete booking -> persists earnings outbox event with BOOKING_EARNINGS_777
        Booking completed = bookingService.completeBooking(777L);
        assertEquals(BookingStatus.COMPLETED, completed.getStatus());
        verify(bookingOutboxRepository, times(1)).save(any(BookingOutboxEvent.class));

        // 2. Outbox processor processes pending event and calls WalletClient with correct worker ID and amount
        BookingOutboxEvent outboxEvent = BookingOutboxEvent.builder()
                .id(10L)
                .bookingId(777L)
                .workerId("worker-77")
                .amount(new BigDecimal("1200.00"))
                .referenceId("BOOKING_EARNINGS_777")
                .status("PENDING")
                .retryCount(0)
                .nextRetryTime(LocalDateTime.now().minusMinutes(1))
                .build();

        when(bookingOutboxRepository.findPendingEventsForProcessingWithLock(any())).thenReturn(List.of(outboxEvent));

        outboxProcessor.processOutboxEvents();

        verify(walletClient, times(1)).creditWallet(
                eq("worker-77"),
                eq(new BigDecimal("1200.00")),
                eq("BOOKING_EARNINGS_777"),
                anyString()
        );
        assertEquals("COMPLETED", outboxEvent.getStatus());
    }
}
