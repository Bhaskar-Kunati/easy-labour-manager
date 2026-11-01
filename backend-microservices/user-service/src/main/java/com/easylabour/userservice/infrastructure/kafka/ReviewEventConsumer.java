package com.easylabour.userservice.infrastructure.kafka;

import com.easylabour.common.event.ReviewCreatedEvent;
import com.easylabour.userservice.application.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReviewEventConsumer {
    private final UserService userService;

    @KafkaListener(topics = "review-events", groupId = "user-group")
    public void consume(ReviewCreatedEvent event) {
        log.info("Received review event for ratee {} with rating {}", event.getRateeId(), event.getRating());
        try {
            userService.updateUserRating(event.getRateeId(), event.getRating());
        } catch (Exception e) {
            log.error("Failed to update user rating for ratee {}", event.getRateeId(), e);
        }
    }
}
