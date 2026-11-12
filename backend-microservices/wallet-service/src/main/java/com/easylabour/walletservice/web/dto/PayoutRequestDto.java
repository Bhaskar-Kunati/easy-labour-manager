package com.easylabour.walletservice.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayoutRequestDto {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Payout amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "Destination ID is required")
    private String destinationId;

    @NotBlank(message = "Destination type is required")
    private String destinationType; // BANK_ACCOUNT or UPI
}
