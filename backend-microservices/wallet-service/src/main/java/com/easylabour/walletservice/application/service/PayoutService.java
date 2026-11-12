package com.easylabour.walletservice.application.service;

import com.easylabour.walletservice.domain.model.PayoutRequest;
import com.easylabour.walletservice.domain.model.TransactionType;
import com.easylabour.walletservice.domain.model.Wallet;
import com.easylabour.walletservice.domain.model.WalletTransaction;
import com.easylabour.walletservice.domain.repository.PayoutRequestRepository;
import com.easylabour.walletservice.domain.repository.WalletRepository;
import com.easylabour.walletservice.domain.repository.WalletTransactionRepository;
import com.easylabour.walletservice.infrastructure.client.UserServiceClient;
import com.easylabour.walletservice.infrastructure.gateway.MockPayoutGateway;
import com.easylabour.walletservice.web.dto.PayoutRequestDto;
import com.easylabour.walletservice.web.dto.PayoutResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayoutService {

    private final WalletRepository walletRepository;
    private final PayoutRequestRepository payoutRequestRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final UserServiceClient userServiceClient;
    private final MockPayoutGateway mockPayoutGateway;

    @Transactional
    public PayoutResponseDto requestPayout(String userId, PayoutRequestDto requestDto) {
        // 1. Verify destination via user-service
        boolean validDestination = userServiceClient.verifyDestination(userId, requestDto.getDestinationId(), requestDto.getDestinationType());
        if (!validDestination) {
            throw new IllegalArgumentException("Invalid or inactive payout destination");
        }

        // 2. Lock wallet with PESSIMISTIC_WRITE
        Wallet wallet = walletRepository.findByUserIdWithLock(userId)
                .orElseGet(() -> walletRepository.save(Wallet.builder()
                        .userId(userId)
                        .balance(BigDecimal.ZERO)
                        .reservedBalance(BigDecimal.ZERO)
                        .currency("INR")
                        .status("ACTIVE")
                        .build()));

        if (!"ACTIVE".equals(wallet.getStatus())) {
            throw new IllegalStateException("Wallet is not active");
        }

        // 3. Check sufficient available balance
        if (wallet.getBalance().compareTo(requestDto.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient available balance for withdrawal");
        }

        // 4. Move funds from available to reserved balance atomically
        wallet.setBalance(wallet.getBalance().subtract(requestDto.getAmount()));
        wallet.setReservedBalance(wallet.getReservedBalance().add(requestDto.getAmount()));
        walletRepository.save(wallet);

        // 5. Create PayoutRequest
        String referenceId = "PAYOUT_" + UUID.randomUUID().toString();
        PayoutRequest payoutRequest = PayoutRequest.builder()
                .walletId(wallet.getId())
                .userId(userId)
                .amount(requestDto.getAmount())
                .currency(wallet.getCurrency())
                .destinationId(requestDto.getDestinationId())
                .destinationType(requestDto.getDestinationType())
                .status("PENDING")
                .referenceId(referenceId)
                .build();

        payoutRequest = payoutRequestRepository.save(payoutRequest);
        log.info("Created payout request {} for user {} with amount {}", referenceId, userId, requestDto.getAmount());

        return PayoutResponseDto.fromEntity(payoutRequest);
    }

    public List<PayoutResponseDto> getWorkerPayouts(String userId) {
        return payoutRequestRepository.findByUserId(userId).stream()
                .map(PayoutResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<PayoutResponseDto> getAllPayouts() {
        return payoutRequestRepository.findAll().stream()
                .map(PayoutResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public PayoutResponseDto approvePayout(Long payoutId) {
        PayoutRequest payout = payoutRequestRepository.findById(payoutId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payout request not found: " + payoutId));

        if (!"PENDING".equalsIgnoreCase(payout.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payout request is not in PENDING status: " + payout.getStatus());
        }

        payout.setStatus("PROCESSING");
        payoutRequestRepository.save(payout);

        MockPayoutGateway.PayoutGatewayResult result = mockPayoutGateway.processPayout(
                payout.getReferenceId(),
                payout.getDestinationId(),
                payout.getDestinationType(),
                payout.getAmount()
        );

        Wallet wallet = walletRepository.findById(payout.getWalletId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found for payout"));

        if ("SUCCESS".equalsIgnoreCase(result.getStatus())) {
            // Convert reserved funds into completed debit ledger entry
            wallet.setReservedBalance(wallet.getReservedBalance().subtract(payout.getAmount()));
            walletRepository.save(wallet);

            WalletTransaction tx = WalletTransaction.builder()
                    .walletId(wallet.getId())
                    .amount(payout.getAmount())
                    .type(TransactionType.DEBIT)
                    .referenceId("PAYOUT_" + payout.getId())
                    .description("Completed payout withdrawal via " + payout.getDestinationType())
                    .build();
            walletTransactionRepository.save(tx);

            payout.setStatus("COMPLETED");
            payout.setGatewayReference(result.getGatewayReference());
            log.info("Payout request {} completed successfully via gateway reference {}", payoutId, result.getGatewayReference());
        } else if ("FAILED".equalsIgnoreCase(result.getStatus())) {
            // Release reserved funds back to available balance
            wallet.setBalance(wallet.getBalance().add(payout.getAmount()));
            wallet.setReservedBalance(wallet.getReservedBalance().subtract(payout.getAmount()));
            walletRepository.save(wallet);

            payout.setStatus("FAILED");
            payout.setFailureReason(result.getFailureReason());
            log.warn("Payout request {} failed: {}", payoutId, result.getFailureReason());
        } else {
            // UNKNOWN status - retain fund reservations, do not auto-retry or release
            payout.setStatus("UNKNOWN");
            payout.setFailureReason(result.getFailureReason() != null ? result.getFailureReason() : "Gateway returned UNKNOWN status");
            log.warn("Payout request {} returned UNKNOWN gateway status. Retaining reservation.", payoutId);
        }

        payoutRequestRepository.save(payout);
        return PayoutResponseDto.fromEntity(payout);
    }
}
