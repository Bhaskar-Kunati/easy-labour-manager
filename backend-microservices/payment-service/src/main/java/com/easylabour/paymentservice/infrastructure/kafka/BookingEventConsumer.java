package com.easylabour.paymentservice.infrastructure.kafka;

import com.easylabour.common.event.BookingEvent;
import com.easylabour.paymentservice.application.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookingEventConsumer {
    private final PaymentService paymentService;

    @KafkaListener(topics = "booking-events", groupId = "payment-group")
    public void consume(BookingEvent event) {
        log.info("Received booking event: {} for booking {}", event.getStatus(), event.getBookingId());
    }
}
