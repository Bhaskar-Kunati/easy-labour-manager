package com.easylabour.paymentservice.web.dto;

import lombok.Data;

@Data
public class VerifyRequest {
    private Long paymentId;
    private String gatewayPaymentId;
}
