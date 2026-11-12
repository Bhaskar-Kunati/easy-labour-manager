package com.easylabour.walletservice.application.service;

import com.easylabour.walletservice.domain.model.PayoutRequest;
import com.easylabour.walletservice.domain.model.Wallet;
import com.easylabour.walletservice.domain.repository.PayoutRequestRepository;
import com.easylabour.walletservice.domain.repository.WalletRepository;
import com.easylabour.walletservice.infrastructure.client.UserServiceClient;
import com.easylabour.walletservice.web.dto.PayoutRequestDto;
import com.easylabour.walletservice.web.dto.PayoutResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PayoutServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private PayoutRequestRepository payoutRequestRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private PayoutService payoutService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRequestPayoutSuccess() {
        Wallet wallet = Wallet.builder()
                .id(1L)
                .userId("worker1")
                .balance(new BigDecimal("1000.00"))
                .reservedBalance(BigDecimal.ZERO)
                .currency("INR")
                .status("ACTIVE")
                .build();

        PayoutRequestDto requestDto = PayoutRequestDto.builder()
                .amount(new BigDecimal("500.00"))
                .destinationId("dest_123")
                .destinationType("BANK_ACCOUNT")
                .build();

        when(userServiceClient.verifyDestination("worker1", "dest_123", "BANK_ACCOUNT")).thenReturn(true);
        when(walletRepository.findByUserIdWithLock("worker1")).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(payoutRequestRepository.save(any(PayoutRequest.class))).thenAnswer(invocation -> {
            PayoutRequest req = invocation.getArgument(0);
            req.setId(1L);
            return req;
        });

        PayoutResponseDto response = payoutService.requestPayout("worker1", requestDto);

        assertNotNull(response);
        assertEquals(0, new BigDecimal("500.00").compareTo(wallet.getBalance()));
        assertEquals(0, new BigDecimal("500.00").compareTo(wallet.getReservedBalance()));
        assertEquals("PENDING", response.getStatus());
        verify(walletRepository, times(1)).save(any(Wallet.class));
        verify(payoutRequestRepository, times(1)).save(any(PayoutRequest.class));
    }

    @Test
    void testRequestPayoutInsufficientBalance() {
        Wallet wallet = Wallet.builder()
                .id(1L)
                .userId("worker1")
                .balance(new BigDecimal("100.00"))
                .reservedBalance(BigDecimal.ZERO)
                .currency("INR")
                .status("ACTIVE")
                .build();

        PayoutRequestDto requestDto = PayoutRequestDto.builder()
                .amount(new BigDecimal("500.00"))
                .destinationId("dest_123")
                .destinationType("BANK_ACCOUNT")
                .build();

        when(userServiceClient.verifyDestination("worker1", "dest_123", "BANK_ACCOUNT")).thenReturn(true);
        when(walletRepository.findByUserIdWithLock("worker1")).thenReturn(Optional.of(wallet));

        assertThrows(IllegalArgumentException.class, () -> {
            payoutService.requestPayout("worker1", requestDto);
        });
    }

    @Test
    void testRequestPayoutInvalidDestination() {
        PayoutRequestDto requestDto = PayoutRequestDto.builder()
                .amount(new BigDecimal("500.00"))
                .destinationId("dest_invalid")
                .destinationType("BANK_ACCOUNT")
                .build();

        when(userServiceClient.verifyDestination("worker1", "dest_invalid", "BANK_ACCOUNT")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            payoutService.requestPayout("worker1", requestDto);
        });
    }
}
