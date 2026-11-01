package com.easylabour.walletservice.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WalletEventProducer {
    private final KafkaTemplate<String, WalletEvent> kafkaTemplate;
    private static final String TOPIC = "wallet-events";

    public void sendWalletEvent(WalletEvent event) {
        kafkaTemplate.send(TOPIC, event.getUserId(), event);
    }
}
