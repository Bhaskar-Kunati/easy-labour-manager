package com.easylabour.userservice.infrastructure.kafka;

import com.easylabour.common.event.BookingEvent;
import com.easylabour.userservice.application.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookingEventConsumer {
    private final UserService userService;

    @KafkaListener(topics = "booking-events", groupId = "user-booking-group")
    public void consume(BookingEvent event) {
        log.info("Received booking event: {} for booking {}", event.getStatus(), event.getBookingId());
        if ("COMPLETED".equals(event.getStatus())) {
            log.info("Incrementing completed bookings count for labourer {}", event.getLabourId());
            try {
                userService.incrementCompletedBookings(event.getLabourId());
            } catch (Exception e) {
                log.error("Failed to increment completed bookings for labourer {}", event.getLabourId(), e);
            }
        }
    }
}
