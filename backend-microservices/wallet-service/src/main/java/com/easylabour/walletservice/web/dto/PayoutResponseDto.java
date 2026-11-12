package com.easylabour.walletservice.web.dto;

import com.easylabour.walletservice.domain.model.PayoutRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayoutResponseDto {
    private Long id;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private String destinationId;
    private String destinationType;
    private String status;
    private String referenceId;
    private LocalDateTime createdAt;

    public static PayoutResponseDto fromEntity(PayoutRequest payout) {
        return PayoutResponseDto.builder()
                .id(payout.getId())
                .userId(payout.getUserId())
                .amount(payout.getAmount())
                .currency(payout.getCurrency())
                .destinationId(payout.getDestinationId())
                .destinationType(payout.getDestinationType())
                .status(payout.getStatus())
                .referenceId(payout.getReferenceId())
                .createdAt(payout.getCreatedAt())
                .build();
    }
}
