package com.easylabour.walletservice.web.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WalletResponse {
    private String userId;
    private BigDecimal balance;
    private String currency;
    private String status;
}
