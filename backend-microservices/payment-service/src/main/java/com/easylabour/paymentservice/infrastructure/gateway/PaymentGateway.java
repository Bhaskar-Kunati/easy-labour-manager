package com.easylabour.paymentservice.infrastructure.gateway;

import java.math.BigDecimal;

public interface PaymentGateway {
    String createOrder(BigDecimal amount, String currency);
    boolean verifyPayment(String gatewayOrderId, String gatewayPaymentId);
    boolean refundPayment(String gatewayPaymentId, BigDecimal amount);
}
