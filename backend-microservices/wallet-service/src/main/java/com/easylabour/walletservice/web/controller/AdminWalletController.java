package com.easylabour.walletservice.web.controller;

import com.easylabour.walletservice.application.service.WalletService;
import com.easylabour.walletservice.domain.model.Wallet;
import com.easylabour.walletservice.web.dto.WalletTransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/wallets")
@RequiredArgsConstructor
public class AdminWalletController {

    private final WalletService walletService;

    @GetMapping
    public ResponseEntity<List<Wallet>> getAllWallets() {
        List<Wallet> wallets = walletService.getAllWalletsForAdmin();
        return ResponseEntity.ok(wallets);
    }

    @GetMapping("/{userId}/transactions")
    public ResponseEntity<List<WalletTransactionResponse>> getWalletTransactionsForAdmin(@PathVariable("userId") String userId) {
        List<WalletTransactionResponse> transactions = walletService.getTransactionsForAdmin(userId);
        return ResponseEntity.ok(transactions);
    }
}
