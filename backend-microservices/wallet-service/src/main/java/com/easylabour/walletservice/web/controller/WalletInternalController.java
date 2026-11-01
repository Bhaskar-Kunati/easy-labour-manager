package com.easylabour.walletservice.web.controller;

import com.easylabour.walletservice.application.service.WalletService;
import com.easylabour.walletservice.web.dto.WalletCreditRequest;
import com.easylabour.walletservice.web.dto.WalletDebitRequest;
import com.easylabour.walletservice.web.dto.WalletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletInternalController {

    private final WalletService walletService;

    @Value("${wallet.internal.secret-key}")
    private String internalServiceKey;

    private void validateInternalKey(String internalKey) {
        if (internalServiceKey == null || internalServiceKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal service key not configured");
        }
        if (internalKey == null || !internalKey.equals(internalServiceKey)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid or missing internal service key");
        }
    }

    @PostMapping("/debit")
    public ResponseEntity<WalletResponse> debitWallet(
            @RequestHeader(value = "X-Internal-Service-Key", required = false) String internalKey,
            @Valid @RequestBody WalletDebitRequest request) {
        validateInternalKey(internalKey);
        WalletResponse response = walletService.debitWallet(request.getUserId(), request.getAmount(), request.getReferenceId(), request.getDescription());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/credit")
    public ResponseEntity<WalletResponse> creditWallet(
            @RequestHeader(value = "X-Internal-Service-Key", required = false) String internalKey,
            @Valid @RequestBody WalletCreditRequest request) {
        validateInternalKey(internalKey);
        WalletResponse response = walletService.creditWallet(request.getUserId(), request.getAmount(), request.getReferenceId(), request.getDescription());
        return ResponseEntity.ok(response);
    }
}
