package com.easylabour.paymentservice.application.service;

import com.easylabour.common.event.PaymentEvent;
import com.easylabour.paymentservice.domain.model.Booking;
import com.easylabour.paymentservice.domain.model.Payment;
import com.easylabour.paymentservice.domain.model.PaymentTransaction;
import com.easylabour.paymentservice.domain.repository.BookingRepository;
import com.easylabour.paymentservice.domain.repository.PaymentRepository;
import com.easylabour.paymentservice.domain.repository.PaymentTransactionRepository;
import com.easylabour.paymentservice.infrastructure.gateway.PaymentGateway;
import com.easylabour.paymentservice.infrastructure.kafka.PaymentEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentTransactionRepository transactionRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private PaymentEventProducer paymentEventProducer;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePaymentOrderSuccessFromBookingContext() {
        Long bookingId = 100L;
        String customerId = "customer_123";
        BigDecimal trustedBookingAmount = BigDecimal.valueOf(750.00);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setCustomerId(customerId);
        booking.setTotalAmount(trustedBookingAmount);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(paymentRepository.findByBookingId(bookingId)).thenReturn(Optional.empty());
        when(paymentGateway.createOrder(trustedBookingAmount, "INR")).thenReturn("gateway_order_abc");
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> {
            Payment p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        Payment payment = paymentService.createPaymentOrder(bookingId, customerId);

        assertNotNull(payment);
        assertEquals("PENDING", payment.getPaymentStatus());
        assertEquals(customerId, payment.getCustomerId());
        assertEquals(bookingId, payment.getBookingId());
        assertEquals(trustedBookingAmount, payment.getAmount(), "Amount must be derived strictly from trusted booking context, not client input");
        verify(paymentRepository).save(any(Payment.class));
        verify(paymentGateway).createOrder(trustedBookingAmount, "INR");
    }

    @Test
    void testCreatePaymentOrderAlreadyPaidThrowsException() {
        Long bookingId = 100L;
        String customerId = "customer_123";
        BigDecimal trustedBookingAmount = BigDecimal.valueOf(500.00);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setCustomerId(customerId);
        booking.setTotalAmount(trustedBookingAmount);

        Payment existing = Payment.builder()
            .id(1L)
            .bookingId(bookingId)
            .customerId(customerId)
            .paymentStatus("SUCCESS")
            .amount(trustedBookingAmount)
            .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(paymentRepository.findByBookingId(bookingId)).thenReturn(Optional.of(existing));

        assertThrows(IllegalStateException.class, () -> {
            paymentService.createPaymentOrder(bookingId, customerId);
        });
    }

    @Test
    void testVerifyPaymentSuccess() {
        Long paymentId = 1L;
        String gatewayPaymentId = "pay_gateway_xyz";

        Payment payment = Payment.builder()
            .id(paymentId)
            .bookingId(100L)
            .customerId("customer_123")
            .amount(BigDecimal.valueOf(500.00))
            .gatewayOrderId("order_abc")
            .paymentStatus("PENDING")
            .build();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentGateway.verifyPayment("order_abc", gatewayPaymentId)).thenReturn(true);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        boolean result = paymentService.verifyPayment(paymentId, gatewayPaymentId);

        assertTrue(result);
        assertEquals("SUCCESS", payment.getPaymentStatus());
        verify(transactionRepository).save(any(PaymentTransaction.class));
        verify(paymentEventProducer).sendPaymentEvent(any(PaymentEvent.class));
    }

    @Test
    void testVerifyPaymentFailure() {
        Long paymentId = 1L;
        String gatewayPaymentId = "pay_gateway_xyz";

        Payment payment = Payment.builder()
            .id(paymentId)
            .bookingId(100L)
            .customerId("customer_123")
            .amount(BigDecimal.valueOf(500.00))
            .gatewayOrderId("order_abc")
            .paymentStatus("PENDING")
            .build();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentGateway.verifyPayment("order_abc", gatewayPaymentId)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        boolean result = paymentService.verifyPayment(paymentId, gatewayPaymentId);

        assertFalse(result);
        assertEquals("FAILED", payment.getPaymentStatus());
        verify(transactionRepository).save(any(PaymentTransaction.class));
        verify(paymentEventProducer).sendPaymentEvent(any(PaymentEvent.class));
    }

    @Test
    void testGetPaymentStatusSuccess() {
        Long bookingId = 100L;
        String customerId = "customer_123";

        Payment payment = Payment.builder()
            .id(1L)
            .bookingId(bookingId)
            .customerId(customerId)
            .paymentStatus("SUCCESS")
            .build();

        when(paymentRepository.findByBookingId(bookingId)).thenReturn(Optional.of(payment));

        Payment result = paymentService.getPaymentStatus(bookingId, customerId);
        assertNotNull(result);
        assertEquals("SUCCESS", result.getPaymentStatus());
    }

    @Test
    void testGetPaymentStatusUnauthorizedThrowsAccessDenied() {
        Long bookingId = 100L;
        String ownerId = "customer_123";
        String intruderId = "intruder_999";

        Payment payment = Payment.builder()
            .id(1L)
            .bookingId(bookingId)
            .customerId(ownerId)
            .paymentStatus("SUCCESS")
            .build();

        when(paymentRepository.findByBookingId(bookingId)).thenReturn(Optional.of(payment));

        assertThrows(AccessDeniedException.class, () -> {
            paymentService.getPaymentStatus(bookingId, intruderId);
        });
    }
}
