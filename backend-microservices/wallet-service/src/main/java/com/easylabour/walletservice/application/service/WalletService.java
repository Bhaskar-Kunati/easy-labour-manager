package com.easylabour.walletservice.application.service;

import com.easylabour.walletservice.infrastructure.client.PaymentClient;
import com.easylabour.walletservice.web.dto.PaymentResponseDto;
import com.easylabour.walletservice.domain.model.Wallet;
import com.easylabour.walletservice.domain.model.WalletTransaction;
import com.easylabour.walletservice.domain.model.TransactionType;
import com.easylabour.walletservice.domain.repository.WalletRepository;
import com.easylabour.walletservice.domain.repository.WalletTransactionRepository;
import com.easylabour.walletservice.infrastructure.kafka.WalletEvent;
import com.easylabour.walletservice.infrastructure.kafka.WalletEventProducer;
import com.easylabour.walletservice.web.dto.WalletResponse;
import com.easylabour.walletservice.web.dto.WalletTransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final WalletEventProducer eventProducer;
    private final PaymentClient paymentClient;

    @Transactional
    public Wallet getOrCreateWallet(String userId) {
        return walletRepository.findByUserId(userId).orElseGet(() -> {
            Wallet newWallet = Wallet.builder()
                    .userId(userId)
                    .balance(BigDecimal.ZERO)
                    .currency("INR")
                    .status("ACTIVE")
                    .build();
            return walletRepository.save(newWallet);
        });
    }

    @Transactional(readOnly = true)
    public WalletResponse getWalletBalance(String userId) {
        Wallet wallet = getOrCreateWallet(userId);
        return WalletResponse.builder()
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .status(wallet.getStatus())
                .build();
    }

    @Transactional(readOnly = true)
    public List<WalletTransactionResponse> getTransactionHistory(String userId) {
        Wallet wallet = getOrCreateWallet(userId);
        List<WalletTransaction> transactions = walletTransactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId());
        return transactions.stream().map(tx -> WalletTransactionResponse.builder()
                .id(tx.getId())
                .amount(tx.getAmount())
                .type(tx.getType())
                .referenceId(tx.getReferenceId())
                .description(tx.getDescription())
                .createdAt(tx.getCreatedAt())
                .build()).collect(Collectors.toList());
    }

    private WalletResponse validateReferenceAndGetIdempotentResponseIfMatch(String userId, BigDecimal amount, String referenceId, TransactionType expectedType) {
        if (referenceId != null && !referenceId.isBlank()) {
            Optional<WalletTransaction> existingTxOpt = walletTransactionRepository.findByReferenceId(referenceId);
            if (existingTxOpt.isPresent()) {
                WalletTransaction existingTx = existingTxOpt.get();
                if (existingTx.getType() != expectedType) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Reference ID already exists with a different transaction type");
                }
                if (existingTx.getAmount() == null || existingTx.getAmount().compareTo(amount) != 0) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Reference ID already exists with a different amount");
                }
                Wallet existingWallet = walletRepository.findById(existingTx.getWalletId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Existing transaction wallet not found"));
                if (!existingWallet.getUserId().equals(userId)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Reference ID already exists for a different user");
                }
                // Exact match: idempotent success
                return WalletResponse.builder()
                        .userId(existingWallet.getUserId())
                        .balance(existingWallet.getBalance())
                        .currency(existingWallet.getCurrency())
                        .status(existingWallet.getStatus())
                        .build();
            }
        }
        return null;
    }

    @Transactional
    public WalletResponse topUpWallet(String userId, Long paymentId) {
        if (paymentId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment ID is required");
        }

        String referenceId = "PAYMENT_" + paymentId;

        PaymentResponseDto payment;
        try {
            payment = paymentClient.getPayment(paymentId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or unknown payment ID: " + paymentId);
        }

        if (payment == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment not found");
        }

        if (!payment.getCustomerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Payment does not belong to the authenticated user");
        }

        String status = payment.getPaymentStatus();
        if (!"SUCCESS".equalsIgnoreCase(status) && !"COMPLETED".equalsIgnoreCase(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment is not successful (status: " + status + ")");
        }

        BigDecimal amount = payment.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid payment amount");
        }

        // Validate reference attributes and handle idempotency match
        WalletResponse idempotentResponse = validateReferenceAndGetIdempotentResponseIfMatch(userId, amount, referenceId, TransactionType.CREDIT);
        if (idempotentResponse != null) {
            return idempotentResponse;
        }

        Wallet wallet = walletRepository.findByUserIdWithLock(userId).orElseGet(() -> {
            Wallet newWallet = Wallet.builder()
                    .userId(userId)
                    .balance(BigDecimal.ZERO)
                    .currency("INR")
                    .status("ACTIVE")
                    .build();
            return walletRepository.save(newWallet);
        });

        if (!"ACTIVE".equalsIgnoreCase(wallet.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wallet is suspended or inactive");
        }

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        WalletTransaction tx = WalletTransaction.builder()
                .walletId(wallet.getId())
                .amount(amount)
                .type(TransactionType.CREDIT)
                .referenceId(referenceId)
                .description("Wallet Top-up via Payment #" + paymentId)
                .build();

        try {
            walletTransactionRepository.save(tx);
        } catch (DataIntegrityViolationException e) {
            WalletResponse retryMatch = validateReferenceAndGetIdempotentResponseIfMatch(userId, amount, referenceId, TransactionType.CREDIT);
            if (retryMatch != null) {
                return retryMatch;
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Concurrent transaction reference conflict", e);
        }

        registerPostCommitEvent(WalletEvent.builder()
                .eventType("TOPUP_SUCCESS")
                .userId(userId)
                .amount(amount)
                .referenceId(referenceId)
                .timestamp(LocalDateTime.now())
                .build());

        return WalletResponse.builder()
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .status(wallet.getStatus())
                .build();
    }

    @Transactional
    public WalletResponse debitWallet(String userId, BigDecimal amount, String referenceId, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debit amount must be positive");
        }

        WalletResponse idempotentResponse = validateReferenceAndGetIdempotentResponseIfMatch(userId, amount, referenceId, TransactionType.DEBIT);
        if (idempotentResponse != null) {
            return idempotentResponse;
        }

        Wallet wallet = walletRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found"));

        if (!"ACTIVE".equalsIgnoreCase(wallet.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wallet is suspended or inactive");
        }

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient wallet balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        WalletTransaction tx = WalletTransaction.builder()
                .walletId(wallet.getId())
                .amount(amount)
                .type(TransactionType.DEBIT)
                .referenceId(referenceId)
                .description(description != null ? description : "Wallet Debit")
                .build();

        try {
            walletTransactionRepository.save(tx);
        } catch (DataIntegrityViolationException e) {
            WalletResponse retryMatch = validateReferenceAndGetIdempotentResponseIfMatch(userId, amount, referenceId, TransactionType.DEBIT);
            if (retryMatch != null) {
                return retryMatch;
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Concurrent transaction reference conflict", e);
        }

        registerPostCommitEvent(WalletEvent.builder()
                .eventType("WALLET_DEBITED")
                .userId(userId)
                .amount(amount)
                .referenceId(referenceId)
                .timestamp(LocalDateTime.now())
                .build());

        return WalletResponse.builder()
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .status(wallet.getStatus())
                .build();
    }

    @Transactional
    public WalletResponse creditWallet(String userId, BigDecimal amount, String referenceId, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credit amount must be positive");
        }

        WalletResponse idempotentResponse = validateReferenceAndGetIdempotentResponseIfMatch(userId, amount, referenceId, TransactionType.CREDIT);
        if (idempotentResponse != null) {
            return idempotentResponse;
        }

        Wallet wallet = walletRepository.findByUserIdWithLock(userId).orElseGet(() -> {
            Wallet newWallet = Wallet.builder()
                    .userId(userId)
                    .balance(BigDecimal.ZERO)
                    .currency("INR")
                    .status("ACTIVE")
                    .build();
            return walletRepository.save(newWallet);
        });

        if (!"ACTIVE".equalsIgnoreCase(wallet.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wallet is suspended or inactive");
        }

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        WalletTransaction tx = WalletTransaction.builder()
                .walletId(wallet.getId())
                .amount(amount)
                .type(TransactionType.CREDIT)
                .referenceId(referenceId)
                .description(description != null ? description : "Wallet Credit")
                .build();

        try {
            walletTransactionRepository.save(tx);
        } catch (DataIntegrityViolationException e) {
            WalletResponse retryMatch = validateReferenceAndGetIdempotentResponseIfMatch(userId, amount, referenceId, TransactionType.CREDIT);
            if (retryMatch != null) {
                return retryMatch;
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Concurrent transaction reference conflict", e);
        }

        registerPostCommitEvent(WalletEvent.builder()
                .eventType("WALLET_CREDITED")
                .userId(userId)
                .amount(amount)
                .referenceId(referenceId)
                .timestamp(LocalDateTime.now())
                .build());

        return WalletResponse.builder()
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .status(wallet.getStatus())
                .build();
    }

    @Transactional(readOnly = true)
    public List<Wallet> getAllWalletsForAdmin() {
        return walletRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<WalletTransactionResponse> getTransactionsForAdmin(String userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found"));
        List<WalletTransaction> transactions = walletTransactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId());
        return transactions.stream().map(tx -> WalletTransactionResponse.builder()
                .id(tx.getId())
                .amount(tx.getAmount())
                .type(tx.getType())
                .referenceId(tx.getReferenceId())
                .description(tx.getDescription())
                .createdAt(tx.getCreatedAt())
                .build()).collect(Collectors.toList());
    }

    private void registerPostCommitEvent(WalletEvent event) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    eventProducer.sendWalletEvent(event);
                }
            });
        } else {
            eventProducer.sendWalletEvent(event);
        }
    }
}
