package com.easylabour.walletservice.web.dto;

import com.easylabour.walletservice.domain.model.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class WalletTransactionResponse {
    private Long id;
    private BigDecimal amount;
    private TransactionType type;
    private String referenceId;
    private String description;
    private LocalDateTime createdAt;
}
