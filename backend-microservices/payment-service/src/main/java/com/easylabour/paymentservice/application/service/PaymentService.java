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
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository transactionRepository;
    private final BookingRepository bookingRepository;
    private final PaymentGateway paymentGateway;
    private final PaymentEventProducer paymentEventProducer;

    @Transactional
    public Payment createPaymentOrder(Long bookingId, String customerId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getCustomerId().equals(customerId)) {
            throw new AccessDeniedException("Unauthorized access to booking");
        }

        BigDecimal amount = booking.getTotalAmount();
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }

        // Idempotency: check if payment already successful for booking
        Optional<Payment> existing = paymentRepository.findByBookingId(bookingId);
        if (existing.isPresent()) {
            Payment p = existing.get();
            if ("SUCCESS".equals(p.getPaymentStatus()) || "COMPLETED".equals(p.getPaymentStatus())) {
                throw new IllegalStateException("Booking is already paid.");
            }
            if (!p.getCustomerId().equals(customerId)) {
                throw new AccessDeniedException("Unauthorized access to booking payment.");
            }
            return p;
        }

        String gatewayOrderId = paymentGateway.createOrder(amount, "INR");
        Payment payment = Payment.builder()
            .bookingId(bookingId)
            .customerId(customerId)
            .amount(amount)
            .paymentGateway("MOCK")
            .gatewayOrderId(gatewayOrderId)
            .paymentStatus("PENDING")
            .build();
        return paymentRepository.save(payment);
    }

    @Transactional
    public boolean verifyPayment(Long paymentId, String gatewayPaymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if ("SUCCESS".equals(payment.getPaymentStatus()) || "COMPLETED".equals(payment.getPaymentStatus())) {
            return true; // Already verified
        }

        boolean isValid = paymentGateway.verifyPayment(payment.getGatewayOrderId(), gatewayPaymentId);
        
        if (isValid) {
            payment.setPaymentStatus("SUCCESS");
            payment.setGatewayPaymentId(gatewayPaymentId);
            paymentRepository.save(payment);
            
            PaymentTransaction tx = PaymentTransaction.builder()
                .payment(payment)
                .transactionType("CAPTURE")
                .amount(payment.getAmount())
                .status("SUCCESS")
                .gatewayResponse("Verified via gateway")
                .build();
            transactionRepository.save(tx);

            // Publish PAYMENT_COMPLETED event (mapped to COMPLETED status for booking-service consumer)
            PaymentEvent event = PaymentEvent.builder()
                .paymentId(payment.getId().toString())
                .bookingId(payment.getBookingId().toString())
                .amount(payment.getAmount())
                .status("COMPLETED")
                .timestamp(LocalDateTime.now())
                .build();
            paymentEventProducer.sendPaymentEvent(event);
        } else {
            payment.setPaymentStatus("FAILED");
            payment.setGatewayPaymentId(gatewayPaymentId);
            paymentRepository.save(payment);

            PaymentTransaction tx = PaymentTransaction.builder()
                .payment(payment)
                .transactionType("CAPTURE")
                .amount(payment.getAmount())
                .status("FAILED")
                .gatewayResponse("Verification failed")
                .build();
            transactionRepository.save(tx);

            // Publish PAYMENT_FAILED event
            PaymentEvent event = PaymentEvent.builder()
                .paymentId(payment.getId().toString())
                .bookingId(payment.getBookingId().toString())
                .amount(payment.getAmount())
                .status("FAILED")
                .timestamp(LocalDateTime.now())
                .build();
            paymentEventProducer.sendPaymentEvent(event);
        }
        return isValid;
    }

    @Transactional(readOnly = true)
    public Payment getPaymentStatus(Long bookingId, String customerId) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Payment not found for booking"));
        if (!payment.getCustomerId().equals(customerId)) {
            throw new AccessDeniedException("Unauthorized access to payment status");
        }
        return payment;
    }

    @Transactional(readOnly = true)
    public Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
            .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
    }
}
