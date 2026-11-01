package com.easylabour.paymentservice.infrastructure.kafka;

import com.easylabour.common.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventProducer {
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;
    private static final String TOPIC = "payment-events";

    public void sendPaymentEvent(PaymentEvent event) {
        kafkaTemplate.send(TOPIC, event.getPaymentId(), event);
    }
}
