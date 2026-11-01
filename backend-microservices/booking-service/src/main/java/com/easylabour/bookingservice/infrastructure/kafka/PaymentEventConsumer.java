package com.easylabour.bookingservice.infrastructure.kafka;

import com.easylabour.bookingservice.application.service.BookingService;
import com.easylabour.bookingservice.domain.model.BookingStatus;
import com.easylabour.common.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventConsumer {
    private final BookingService bookingService;

    @KafkaListener(topics = "payment-events", groupId = "booking-group")
    public void consume(PaymentEvent event) {
        log.info("Received payment event: {} for booking {}", event.getStatus(), event.getBookingId());
        if ("COMPLETED".equals(event.getStatus())) {
            log.info("Updating booking {} status to COMPLETED", event.getBookingId());
            try {
                bookingService.updateStatus(Long.valueOf(event.getBookingId()), BookingStatus.SERVICE_COMPLETED);
            } catch (Exception e) {
                log.error("Failed to update booking status for booking {}", event.getBookingId(), e);
            }
        }
    }
}
