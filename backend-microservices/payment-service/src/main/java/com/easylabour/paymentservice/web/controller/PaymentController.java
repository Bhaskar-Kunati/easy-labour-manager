package com.easylabour.paymentservice.web.controller;

import com.easylabour.paymentservice.application.service.PaymentService;
import com.easylabour.paymentservice.domain.model.Payment;
import com.easylabour.paymentservice.web.dto.OrderRequest;
import com.easylabour.paymentservice.web.dto.VerifyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<Payment> createOrder(@RequestBody OrderRequest request) {
        String customerId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(paymentService.createPaymentOrder(
            request.getBookingId(), customerId));
    }

    @PostMapping("/verify")
    public ResponseEntity<Boolean> verify(@RequestBody VerifyRequest request) {
        return ResponseEntity.ok(paymentService.verifyPayment(
            request.getPaymentId(), request.getGatewayPaymentId()));
    }

    @GetMapping("/status/{bookingId}")
    public ResponseEntity<Payment> getStatus(@PathVariable Long bookingId) {
        String customerId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(paymentService.getPaymentStatus(bookingId, customerId));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }
}
