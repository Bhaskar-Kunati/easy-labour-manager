package com.easylabour.walletservice.infrastructure.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletEvent {
    private String eventType; // WALLET_DEBITED, WALLET_CREDITED, TOPUP_SUCCESS
    private String userId;
    private BigDecimal amount;
    private String referenceId;
    private LocalDateTime timestamp;
}
