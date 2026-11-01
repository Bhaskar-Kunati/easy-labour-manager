package com.easylabour.paymentservice.infrastructure.gateway;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.UUID;

@Component
public class MockPaymentGateway implements PaymentGateway {
    @Override
    public String createOrder(BigDecimal amount, String currency) {
        return "mock_order_" + UUID.randomUUID().toString();
    }

    @Override
    public boolean verifyPayment(String gatewayOrderId, String gatewayPaymentId) {
        return true; // Simulate success
    }

    @Override
    public boolean refundPayment(String gatewayPaymentId, BigDecimal amount) {
        return true; // Simulate success
    }
}
