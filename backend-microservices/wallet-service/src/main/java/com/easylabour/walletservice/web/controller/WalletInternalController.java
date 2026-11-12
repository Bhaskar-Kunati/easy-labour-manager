package com.easylabour.walletservice.web.controller;

import com.easylabour.walletservice.application.service.PayoutService;
import com.easylabour.walletservice.application.service.WalletService;
import com.easylabour.walletservice.domain.repository.WalletRepository;
import com.easylabour.walletservice.domain.repository.WalletTransactionRepository;
import com.easylabour.walletservice.web.dto.PayoutResponseDto;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletInternalController {

    private final WalletService walletService;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final PayoutService payoutService;

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

    @GetMapping("/internal/analytics")
    public ResponseEntity<Map<String, Object>> getWalletAnalytics(
            @RequestHeader(value = "X-Internal-Service-Key", required = false) String internalKey) {
        validateInternalKey(internalKey);

        var wallets = walletRepository.findAll();
        var transactions = walletTransactionRepository.findAll();

        BigDecimal walletLiabilities = BigDecimal.ZERO;
        for (var w : wallets) {
            if ("ACTIVE".equalsIgnoreCase(w.getStatus()) && "INR".equalsIgnoreCase(w.getCurrency())) {
                walletLiabilities = walletLiabilities.add(w.getBalance() != null ? w.getBalance() : BigDecimal.ZERO);
            }
        }

        BigDecimal walletTopups = BigDecimal.ZERO;
        BigDecimal workerEarningsCredits = BigDecimal.ZERO;
        BigDecimal walletRefundCredits = BigDecimal.ZERO;

        for (var tx : transactions) {
            String ref = tx.getReferenceId() != null ? tx.getReferenceId() : "";
            BigDecimal amt = tx.getAmount() != null ? tx.getAmount() : BigDecimal.ZERO;
            if ("CREDIT".equals(tx.getType().name())) {
                if (ref.startsWith("PAYMENT_")) {
                    walletTopups = walletTopups.add(amt);
                } else if (ref.startsWith("BOOKING_EARNINGS_")) {
                    workerEarningsCredits = workerEarningsCredits.add(amt);
                } else if (ref.startsWith("REFUND_")) {
                    walletRefundCredits = walletRefundCredits.add(amt);
                }
            }
        }

        Map<String, Object> result = Map.of(
                "walletLiabilities", walletLiabilities,
                "walletTopups", walletTopups,
                "workerEarningsCredits", workerEarningsCredits,
                "walletRefundCredits", walletRefundCredits
        );

        return ResponseEntity.ok(result);
    }

    @GetMapping("/internal/payouts")
    public ResponseEntity<List<PayoutResponseDto>> getAllPayouts(
            @RequestHeader(value = "X-Internal-Service-Key", required = false) String internalKey) {
        validateInternalKey(internalKey);
        return ResponseEntity.ok(payoutService.getAllPayouts());
    }

    @PostMapping("/internal/payouts/{id}/approve")
    public ResponseEntity<PayoutResponseDto> approvePayout(
            @RequestHeader(value = "X-Internal-Service-Key", required = false) String internalKey,
            @PathVariable Long id) {
        validateInternalKey(internalKey);
        return ResponseEntity.ok(payoutService.approvePayout(id));
    }
}
