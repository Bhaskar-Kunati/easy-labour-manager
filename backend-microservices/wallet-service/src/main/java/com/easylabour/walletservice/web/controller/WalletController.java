package com.easylabour.walletservice.web.controller;

import com.easylabour.walletservice.application.service.WalletService;
import com.easylabour.walletservice.infrastructure.client.PaymentClient;
import com.easylabour.walletservice.web.dto.PaymentResponseDto;
import com.easylabour.walletservice.web.dto.TopupRequest;
import com.easylabour.walletservice.web.dto.WalletResponse;
import com.easylabour.walletservice.web.dto.WalletTransactionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final PaymentClient paymentClient;

    private String getAuthenticatedUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping("/balance")
    public ResponseEntity<WalletResponse> getBalance() {
        String userId = getAuthenticatedUserId();
        WalletResponse response = walletService.getWalletBalance(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<WalletTransactionResponse>> getTransactions() {
        String userId = getAuthenticatedUserId();
        List<WalletTransactionResponse> response = walletService.getTransactionHistory(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/topup/confirm")
    public ResponseEntity<WalletResponse> confirmTopUp(@Valid @RequestBody TopupRequest request) {
        String userId = getAuthenticatedUserId();
        PaymentResponseDto payment = paymentClient.getPayment(request.getPaymentId());
        if (payment == null || payment.getAmount() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid payment details");
        }
        String referenceId = "PAYMENT_" + request.getPaymentId();
        WalletResponse response = walletService.topUpWallet(userId, payment.getAmount(), referenceId, "Top-up from Payment " + request.getPaymentId());
        return ResponseEntity.ok(response);
    }
}
