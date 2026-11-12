package com.easylabour.walletservice.infrastructure.gateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@Slf4j
public class MockPayoutGateway {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PayoutGatewayResult {
        private String status; // SUCCESS, FAILED, UNKNOWN
        private String gatewayReference;
        private String failureReason;
    }

    public PayoutGatewayResult processPayout(String referenceId, String destinationId, String destinationType, BigDecimal amount) {
        log.info("Processing mock payout via gateway for reference: {}, destination: {}, amount: {}", referenceId, destinationId, amount);

        if (destinationId != null && destinationId.toLowerCase().contains("fail")) {
            return PayoutGatewayResult.builder()
                    .status("FAILED")
                    .failureReason("Simulated gateway rejection due to destination error")
                    .build();
        }

        if (destinationId != null && destinationId.toLowerCase().contains("unknown")) {
            return PayoutGatewayResult.builder()
                    .status("UNKNOWN")
                    .failureReason("Gateway timeout / unknown status response")
                    .build();
        }

        return PayoutGatewayResult.builder()
                .status("SUCCESS")
                .gatewayReference("MOCK_GW_" + UUID.randomUUID().toString())
                .build();
    }
}
