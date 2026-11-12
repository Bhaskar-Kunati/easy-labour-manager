package com.easylabour.bookingservice.application.service;

import com.easylabour.bookingservice.domain.model.*;
import com.easylabour.bookingservice.domain.repository.BookingOutboxRepository;
import com.easylabour.bookingservice.domain.repository.BookingRepository;
import com.easylabour.bookingservice.domain.repository.BookingWorkerRepository;
import com.easylabour.bookingservice.infrastructure.client.WalletClient;
import com.easylabour.bookingservice.infrastructure.kafka.BookingEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookingOutboxTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingWorkerRepository bookingWorkerRepository;

    @Mock
    private BookingOutboxRepository bookingOutboxRepository;

    @Mock
    private WalletClient walletClient;

    @Mock
    private BookingEventProducer eventProducer;

    @InjectMocks
    private BookingService bookingService;

    @InjectMocks
    private BookingOutboxProcessor outboxProcessor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSuccessfulBookingCompletionCreatesOutboxEvent() {
        Booking booking = Booking.builder()
                .id(1L)
                .customerId("customer1")
                .totalAmount(new BigDecimal("500.00"))
                .status(BookingStatus.CONFIRMED)
                .build();

        BookingWorker worker = BookingWorker.builder()
                .id(1L)
                .booking(booking)
                .workerId("worker-123")
                .status(BookingWorkerStatus.ACCEPTED)
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingWorkerRepository.findByBookingId(1L)).thenReturn(List.of(worker));
        when(bookingOutboxRepository.findByReferenceId("BOOKING_EARNINGS_1")).thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking completed = bookingService.completeBooking(1L);

        assertEquals(BookingStatus.COMPLETED, completed.getStatus());
        verify(bookingOutboxRepository, times(1)).save(any(BookingOutboxEvent.class));
    }

    @Test
    void testCompletionWithoutWorkerFails() {
        Booking booking = Booking.builder()
                .id(2L)
                .customerId("customer1")
                .totalAmount(new BigDecimal("500.00"))
                .status(BookingStatus.CONFIRMED)
                .build();

        when(bookingRepository.findById(2L)).thenReturn(Optional.of(booking));
        when(bookingWorkerRepository.findByBookingId(2L)).thenReturn(Collections.emptyList());

        assertThrows(ResponseStatusException.class, () -> bookingService.completeBooking(2L));
        verify(bookingOutboxRepository, never()).save(any());
    }

    @Test
    void testCancelledBookingCannotGenerateEarnings() {
        Booking booking = Booking.builder()
                .id(3L)
                .customerId("customer1")
                .totalAmount(new BigDecimal("500.00"))
                .status(BookingStatus.CANCELLED)
                .build();

        when(bookingRepository.findById(3L)).thenReturn(Optional.of(booking));

        assertThrows(ResponseStatusException.class, () -> bookingService.completeBooking(3L));
        verify(bookingOutboxRepository, never()).save(any());
    }

    @Test
    void testDuplicateCompletionDoesNotCreateDuplicateOutboxEvent() {
        Booking booking = Booking.builder()
                .id(4L)
                .customerId("customer1")
                .totalAmount(new BigDecimal("500.00"))
                .status(BookingStatus.CONFIRMED)
                .build();

        BookingWorker worker = BookingWorker.builder()
                .id(1L)
                .booking(booking)
                .workerId("worker-123")
                .status(BookingWorkerStatus.ACCEPTED)
                .build();

        BookingOutboxEvent existingEvent = BookingOutboxEvent.builder()
                .id(1L)
                .bookingId(4L)
                .workerId("worker-123")
                .amount(new BigDecimal("500.00"))
                .referenceId("BOOKING_EARNINGS_4")
                .status("PENDING")
                .build();

        when(bookingRepository.findById(4L)).thenReturn(Optional.of(booking));
        when(bookingWorkerRepository.findByBookingId(4L)).thenReturn(List.of(worker));
        when(bookingOutboxRepository.findByReferenceId("BOOKING_EARNINGS_4")).thenReturn(Optional.of(existingEvent));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        bookingService.completeBooking(4L);

        // Should not save a new outbox event if one with referenceId already exists
        verify(bookingOutboxRepository, never()).save(any(BookingOutboxEvent.class));
    }

    @Test
    void testOutboxProcessorSuccessfulDelivery() {
        BookingOutboxEvent event = BookingOutboxEvent.builder()
                .id(1L)
                .bookingId(10L)
                .workerId("worker-999")
                .amount(new BigDecimal("250.00"))
                .referenceId("BOOKING_EARNINGS_10")
                .status("PENDING")
                .retryCount(0)
                .nextRetryTime(LocalDateTime.now().minusMinutes(1))
                .build();

        when(bookingOutboxRepository.findPendingEventsForProcessingWithLock(any())).thenReturn(List.of(event));

        outboxProcessor.processOutboxEvents();

        verify(walletClient, times(1)).creditWallet(eq("worker-999"), eq(new BigDecimal("250.00")), eq("BOOKING_EARNINGS_10"), anyString());
        assertEquals("COMPLETED", event.getStatus());
        verify(bookingOutboxRepository, times(1)).save(event);
    }

    @Test
    void testWalletServiceOutageTriggersRetry() {
        BookingOutboxEvent event = BookingOutboxEvent.builder()
                .id(1L)
                .bookingId(11L)
                .workerId("worker-999")
                .amount(new BigDecimal("250.00"))
                .referenceId("BOOKING_EARNINGS_11")
                .status("PENDING")
                .retryCount(0)
                .nextRetryTime(LocalDateTime.now().minusMinutes(1))
                .build();

        when(bookingOutboxRepository.findPendingEventsForProcessingWithLock(any())).thenReturn(List.of(event));
        doThrow(new RuntimeException("Connection timeout")).when(walletClient).creditWallet(anyString(), any(), anyString(), anyString());

        outboxProcessor.processOutboxEvents();

        assertEquals("PENDING", event.getStatus());
        assertEquals(1, event.getRetryCount());
        assertNotNull(event.getLastError());
        verify(bookingOutboxRepository, times(1)).save(event);
    }

    @Test
    void testSecurityFailureMarksEventFailedWithoutEndlessRetry() {
        BookingOutboxEvent event = BookingOutboxEvent.builder()
                .id(1L)
                .bookingId(12L)
                .workerId("worker-999")
                .amount(new BigDecimal("250.00"))
                .referenceId("BOOKING_EARNINGS_12")
                .status("PENDING")
                .retryCount(0)
                .nextRetryTime(LocalDateTime.now().minusMinutes(1))
                .build();

        when(bookingOutboxRepository.findPendingEventsForProcessingWithLock(any())).thenReturn(List.of(event));
        doThrow(new SecurityException("403 Forbidden")).when(walletClient).creditWallet(anyString(), any(), anyString(), anyString());

        outboxProcessor.processOutboxEvents();

        assertEquals("FAILED", event.getStatus());
        verify(bookingOutboxRepository, times(1)).save(event);
    }
}
