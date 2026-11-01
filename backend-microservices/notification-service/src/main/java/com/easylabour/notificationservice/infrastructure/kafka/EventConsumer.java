package com.easylabour.notificationservice.infrastructure.kafka;

import com.easylabour.common.event.BookingEvent;
import com.easylabour.common.event.PaymentEvent;
import com.easylabour.common.event.TrackingEvent;
import com.easylabour.notificationservice.application.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {
    private final NotificationService notificationService;

    @KafkaListener(topics = "booking-events", groupId = "notification-group")
    public void consumeBookingEvent(BookingEvent event) {
        log.info("Consumed booking event: {}", event);
        notificationService.processBookingEvent(event);
    }

    @KafkaListener(topics = "payment-events", groupId = "notification-group")
    public void consumePaymentEvent(PaymentEvent event) {
        log.info("Consumed payment event: {}", event);
    }

    @KafkaListener(topics = "tracking-events", groupId = "notification-group")
    public void consumeTrackingEvent(TrackingEvent event) {
        log.info("Consumed tracking event: {}", event);
    }
}
