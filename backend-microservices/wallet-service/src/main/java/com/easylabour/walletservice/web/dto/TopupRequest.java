package com.easylabour.walletservice.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TopupRequest {
    @NotNull(message = "Payment ID is required")
    private Long paymentId;
}
