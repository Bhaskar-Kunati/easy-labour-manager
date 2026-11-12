package com.easylabour.walletservice.application.service;

import com.easylabour.walletservice.domain.model.Wallet;
import com.easylabour.walletservice.domain.model.WalletTransaction;
import com.easylabour.walletservice.domain.repository.WalletRepository;
import com.easylabour.walletservice.domain.repository.WalletTransactionRepository;
import com.easylabour.walletservice.infrastructure.kafka.WalletEventProducer;
import com.easylabour.walletservice.web.dto.WalletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletTransactionRepository walletTransactionRepository;

    @Mock
    private WalletEventProducer eventProducer;

    @InjectMocks
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testTopUpWalletSuccess() {
        Wallet wallet = Wallet.builder()
                .id(1L)
                .userId("user123")
                .balance(new BigDecimal("100.00"))
                .currency("INR")
                .status("ACTIVE")
                .build();

        when(walletTransactionRepository.findByReferenceId("ref-1")).thenReturn(Optional.empty());
        when(walletRepository.findByUserIdWithLock("user123")).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        WalletResponse response = walletService.topUpWallet("user123", new BigDecimal("50.00"), "ref-1", "Test Topup");

        assertNotNull(response);
        assertEquals(0, new BigDecimal("150.00").compareTo(response.getBalance()));
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    void testDebitWalletInsufficientBalance() {
        Wallet wallet = Wallet.builder()
                .id(1L)
                .userId("user123")
                .balance(new BigDecimal("30.00"))
                .currency("INR")
                .status("ACTIVE")
                .build();

        when(walletTransactionRepository.findByReferenceId("ref-2")).thenReturn(Optional.empty());
        when(walletRepository.findByUserIdWithLock("user123")).thenReturn(Optional.of(wallet));

        assertThrows(ResponseStatusException.class, () -> {
            walletService.debitWallet("user123", new BigDecimal("50.00"), "ref-2", "Test Debit");
        });
    }

    @Test
    void testIdempotentDuplicateSuccess() {
        Wallet wallet = Wallet.builder()
                .id(1L)
                .userId("user123")
                .balance(new BigDecimal("100.00"))
                .currency("INR")
                .status("ACTIVE")
                .build();

        WalletTransaction existingTx = WalletTransaction.builder()
                .id(10L)
                .walletId(1L)
                .amount(new BigDecimal("50.00"))
                .type(com.easylabour.walletservice.domain.model.TransactionType.CREDIT)
                .referenceId("ref-dup")
                .build();

        when(walletTransactionRepository.findByReferenceId("ref-dup")).thenReturn(Optional.of(existingTx));
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        WalletResponse response = walletService.topUpWallet("user123", new BigDecimal("50.00"), "ref-dup", "Duplicate Topup");

        assertNotNull(response);
        assertEquals(0, new BigDecimal("100.00").compareTo(response.getBalance()));
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(walletTransactionRepository, never()).save(any(WalletTransaction.class));
    }

    @Test
    void testReferenceDifferentUserRejected() {
        Wallet wallet1 = Wallet.builder()
                .id(1L)
                .userId("user1")
                .balance(new BigDecimal("100.00"))
                .currency("INR")
                .status("ACTIVE")
                .build();

        WalletTransaction existingTx = WalletTransaction.builder()
                .id(10L)
                .walletId(1L)
                .amount(new BigDecimal("50.00"))
                .type(com.easylabour.walletservice.domain.model.TransactionType.CREDIT)
                .referenceId("ref-user")
                .build();

        when(walletTransactionRepository.findByReferenceId("ref-user")).thenReturn(Optional.of(existingTx));
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet1));

        assertThrows(ResponseStatusException.class, () -> {
            walletService.topUpWallet("user2", new BigDecimal("50.00"), "ref-user", "Wrong User");
        });
    }

    @Test
    void testReferenceDifferentAmountRejected() {
        Wallet wallet = Wallet.builder()
                .id(1L)
                .userId("user123")
                .balance(new BigDecimal("100.00"))
                .currency("INR")
                .status("ACTIVE")
                .build();

        WalletTransaction existingTx = WalletTransaction.builder()
                .id(10L)
                .walletId(1L)
                .amount(new BigDecimal("50.00"))
                .type(com.easylabour.walletservice.domain.model.TransactionType.CREDIT)
                .referenceId("ref-amount")
                .build();

        when(walletTransactionRepository.findByReferenceId("ref-amount")).thenReturn(Optional.of(existingTx));
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        assertThrows(ResponseStatusException.class, () -> {
            walletService.topUpWallet("user123", new BigDecimal("75.00"), "ref-amount", "Different Amount");
        });
    }

    @Test
    void testReferenceDifferentTypeRejected() {
        Wallet wallet = Wallet.builder()
                .id(1L)
                .userId("user123")
                .balance(new BigDecimal("100.00"))
                .currency("INR")
                .status("ACTIVE")
                .build();

        WalletTransaction existingTx = WalletTransaction.builder()
                .id(10L)
                .walletId(1L)
                .amount(new BigDecimal("50.00"))
                .type(com.easylabour.walletservice.domain.model.TransactionType.DEBIT)
                .referenceId("ref-type")
                .build();

        when(walletTransactionRepository.findByReferenceId("ref-type")).thenReturn(Optional.of(existingTx));
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        assertThrows(ResponseStatusException.class, () -> {
            walletService.topUpWallet("user123", new BigDecimal("50.00"), "ref-type", "Different Type");
        });
    }
}
