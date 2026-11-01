package com.easylabour.walletservice.web.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentResponseDto {
    private Long id;
    private Long bookingId;
    private String customerId;
    private BigDecimal amount;
    private String paymentGateway;
    private String gatewayOrderId;
    private String gatewayPaymentId;
    private String paymentStatus;
}
