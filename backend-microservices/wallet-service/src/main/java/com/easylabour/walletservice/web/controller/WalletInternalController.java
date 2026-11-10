package com.easylabour.walletservice.web.controller;

import com.easylabour.walletservice.application.service.WalletService;
import com.easylabour.walletservice.web.dto.WalletCreditRequest;
import com.easylabour.walletservice.web.dto.WalletDebitRequest;
import com.easylabour.walletservice.web.dto.WalletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletInternalController {

    private final WalletService walletService;

    @PostMapping("/debit")
    public ResponseEntity<WalletResponse> debitWallet(
            @Valid @RequestBody WalletDebitRequest request) {
        WalletResponse response = walletService.debitWallet(request.getUserId(), request.getAmount(), request.getReferenceId(), request.getDescription());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/credit")
    public ResponseEntity<WalletResponse> creditWallet(
            @Valid @RequestBody WalletCreditRequest request) {
        WalletResponse response = walletService.creditWallet(request.getUserId(), request.getAmount(), request.getReferenceId(), request.getDescription());
        return ResponseEntity.ok(response);
    }
}
