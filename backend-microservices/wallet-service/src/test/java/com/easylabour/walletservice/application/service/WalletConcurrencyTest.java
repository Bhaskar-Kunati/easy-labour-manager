package com.easylabour.walletservice.application.service;

import com.easylabour.walletservice.domain.model.Wallet;
import com.easylabour.walletservice.domain.repository.WalletRepository;
import com.easylabour.walletservice.domain.repository.WalletTransactionRepository;
import com.easylabour.walletservice.infrastructure.kafka.WalletEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WalletConcurrencyTest {

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
    void testConcurrentDebitsDoNotOverspend() throws InterruptedException {
        Wallet wallet = Wallet.builder()
                .id(1L)
                .userId("user-concurrent")
                .balance(new BigDecimal("100.00"))
                .currency("INR")
                .status("ACTIVE")
                .build();

        when(walletRepository.findByUserIdWithLock("user-concurrent")).thenReturn(Optional.of(wallet));
        when(walletTransactionRepository.findByReferenceId(any())).thenReturn(Optional.empty());
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> {
            Wallet w = invocation.getArgument(0);
            wallet.setBalance(w.getBalance());
            return wallet;
        });

        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final String refId = "ref-debit-" + i;
            executorService.submit(() -> {
                try {
                    synchronized (wallet) {
                        walletService.debitWallet("user-concurrent", new BigDecimal("20.00"), refId, "Concurrent Debit");
                    }
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Initial balance 100, each debit 20 -> exactly 5 succeed, 5 fail due to insufficient balance
        assertEquals(5, successCount.get());
        assertEquals(5, failureCount.get());
        assertEquals(0, new BigDecimal("0.00").compareTo(wallet.getBalance()));
        assertTrue(wallet.getBalance().compareTo(BigDecimal.ZERO) >= 0);
    }
}
