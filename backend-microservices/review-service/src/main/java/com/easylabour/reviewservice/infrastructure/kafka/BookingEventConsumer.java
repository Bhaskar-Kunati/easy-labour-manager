package com.easylabour.reviewservice.infrastructure.kafka;

import com.easylabour.common.event.BookingEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class BookingEventConsumer {

    private final Map<String, BookingEvent> bookingStore = new ConcurrentHashMap<>();

    @KafkaListener(topics = "booking-events", groupId = "review-group")
    public void consume(BookingEvent event) {
        log.info("Received booking event: {} for booking {}", event.getStatus(), event.getBookingId());
        if (event.getBookingId() != null) {
            bookingStore.put(event.getBookingId(), event);
        }
    }

    public BookingEvent getBooking(String bookingId) {
        return bookingStore.get(bookingId);
    }

    public void putBooking(String bookingId, BookingEvent event) {
        bookingStore.put(bookingId, event);
    }

    public boolean isBookingCompleted(String bookingId) {
        if (bookingId == null) {
            return false;
        }
        BookingEvent event = bookingStore.get(bookingId);
        if (event == null) {
            return false;
        }
        return "COMPLETED".equalsIgnoreCase(event.getStatus());
    }

    public boolean isBookingCompleted(Long bookingId) {
        if (bookingId == null) {
            return false;
        }
        return isBookingCompleted(String.valueOf(bookingId));
    }

    public boolean verifyCustomerOwnership(Long bookingId, String customerId) {
        if (bookingId == null || customerId == null) {
            return false;
        }
        BookingEvent event = bookingStore.get(String.valueOf(bookingId));
        if (event == null || event.getCustomerId() == null) {
            return false;
        }
        return event.getCustomerId().equals(customerId);
    }
}
