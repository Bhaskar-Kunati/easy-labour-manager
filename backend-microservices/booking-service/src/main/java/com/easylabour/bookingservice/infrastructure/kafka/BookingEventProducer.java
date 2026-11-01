package com.easylabour.bookingservice.infrastructure.kafka;

import com.easylabour.common.event.BookingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingEventProducer {
    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;
    private static final String TOPIC = "booking-events";

    public void sendBookingEvent(BookingEvent event) {
        kafkaTemplate.send(TOPIC, event.getBookingId(), event);
    }
}
