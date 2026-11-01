package com.easylabour.reviewservice.infrastructure.kafka;

import com.easylabour.common.event.ReviewCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReviewEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "review-events";

    public void sendReviewCreatedEvent(ReviewCreatedEvent event) {
        log.info("Sending review created event for booking {} and ratee {}", event.getBookingId(), event.getRateeId());
        kafkaTemplate.send(TOPIC, event.getRateeId(), event);
    }
}
