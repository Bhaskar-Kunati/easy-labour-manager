package com.easylabour.walletservice.application.service;

import com.easylabour.walletservice.domain.model.TransactionType;
import com.easylabour.walletservice.domain.model.Wallet;
import com.easylabour.walletservice.domain.model.WalletTransaction;
import com.easylabour.walletservice.domain.repository.WalletRepository;
import com.easylabour.walletservice.domain.repository.WalletTransactionRepository;
import com.easylabour.walletservice.infrastructure.client.PaymentClient;
import com.easylabour.walletservice.infrastructure.kafka.WalletEventProducer;
import com.easylabour.walletservice.web.dto.PaymentResponseDto;
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

    @Mock
    private PaymentClient paymentClient;

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

        PaymentResponseDto payment = new PaymentResponseDto();
        payment.setId(100L);
        payment.setCustomerId("user123");
        payment.setAmount(new BigDecimal("50.00"));
        payment.setPaymentStatus("SUCCESS");

        when(walletTransactionRepository.findByReferenceId("PAYMENT_100")).thenReturn(Optional.empty());
        when(paymentClient.getPayment(100L)).thenReturn(payment);
        when(walletRepository.findByUserIdWithLock("user123")).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        WalletResponse response = walletService.topUpWallet("user123", 100L);

        assertNotNull(response);
        assertEquals(0, new BigDecimal("150.00").compareTo(response.getBalance()));
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    void testTopUpWalletCustomerMismatch() {
        PaymentResponseDto payment = new PaymentResponseDto();
        payment.setId(100L);
        payment.setCustomerId("otherUser");
        payment.setAmount(new BigDecimal("50.00"));
        payment.setPaymentStatus("SUCCESS");

        when(walletTransactionRepository.findByReferenceId("PAYMENT_100")).thenReturn(Optional.empty());
        when(paymentClient.getPayment(100L)).thenReturn(payment);

        assertThrows(ResponseStatusException.class, () -> {
            walletService.topUpWallet("user123", 100L);
        });
    }

    @Test
    void testTopUpWalletPaymentFailed() {
        PaymentResponseDto payment = new PaymentResponseDto();
        payment.setId(100L);
        payment.setCustomerId("user123");
        payment.setAmount(new BigDecimal("50.00"));
        payment.setPaymentStatus("FAILED");

        when(walletTransactionRepository.findByReferenceId("PAYMENT_100")).thenReturn(Optional.empty());
        when(paymentClient.getPayment(100L)).thenReturn(payment);

        assertThrows(ResponseStatusException.class, () -> {
            walletService.topUpWallet("user123", 100L);
        });
    }

    @Test
    void testTopUpWalletPaymentNotFound() {
        when(walletTransactionRepository.findByReferenceId("PAYMENT_100")).thenReturn(Optional.empty());
        when(paymentClient.getPayment(100L)).thenThrow(new RuntimeException("Not found"));

        assertThrows(ResponseStatusException.class, () -> {
            walletService.topUpWallet("user123", 100L);
        });
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
    void testCreditWalletIdempotentSuccess() {
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
                .type(TransactionType.CREDIT)
                .referenceId("REFUND_1")
                .build();

        when(walletTransactionRepository.findByReferenceId("REFUND_1")).thenReturn(Optional.of(existingTx));
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        WalletResponse response = walletService.creditWallet("user123", new BigDecimal("50.00"), "REFUND_1", "Test Refund");

        assertNotNull(response);
        assertEquals(0, new BigDecimal("100.00").compareTo(response.getBalance()));
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void testCreditWalletDifferentUserConflict() {
        Wallet wallet = Wallet.builder()
                .id(2L)
                .userId("otherUser")
                .balance(new BigDecimal("100.00"))
                .currency("INR")
                .status("ACTIVE")
                .build();

        WalletTransaction existingTx = WalletTransaction.builder()
                .id(10L)
                .walletId(2L)
                .amount(new BigDecimal("50.00"))
                .type(TransactionType.CREDIT)
                .referenceId("REFUND_1")
                .build();

        when(walletTransactionRepository.findByReferenceId("REFUND_1")).thenReturn(Optional.of(existingTx));
        when(walletRepository.findById(2L)).thenReturn(Optional.of(wallet));

        assertThrows(ResponseStatusException.class, () -> {
            walletService.creditWallet("user123", new BigDecimal("50.00"), "REFUND_1", "Test Refund");
        });
    }

    @Test
    void testCreditWalletDifferentAmountConflict() {
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
                .amount(new BigDecimal("75.00"))
                .type(TransactionType.CREDIT)
                .referenceId("REFUND_1")
                .build();

        when(walletTransactionRepository.findByReferenceId("REFUND_1")).thenReturn(Optional.of(existingTx));
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        assertThrows(ResponseStatusException.class, () -> {
            walletService.creditWallet("user123", new BigDecimal("50.00"), "REFUND_1", "Test Refund");
        });
    }

    @Test
    void testCreditWalletDifferentTypeConflict() {
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
                .type(TransactionType.DEBIT)
                .referenceId("REFUND_1")
                .build();

        when(walletTransactionRepository.findByReferenceId("REFUND_1")).thenReturn(Optional.of(existingTx));
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        assertThrows(ResponseStatusException.class, () -> {
            walletService.creditWallet("user123", new BigDecimal("50.00"), "REFUND_1", "Test Refund");
        });
    }
}
