package com.easylabour.walletservice.web.controller;

import com.easylabour.walletservice.application.service.WalletService;
import com.easylabour.walletservice.web.dto.TopupRequest;
import com.easylabour.walletservice.web.dto.WalletResponse;
import com.easylabour.walletservice.web.dto.WalletTransactionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

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
        WalletResponse response = walletService.topUpWallet(userId, request.getPaymentId());
        return ResponseEntity.ok(response);
    }
}
