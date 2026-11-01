package com.easylabour.trackingservice.infrastructure.kafka;

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

    @KafkaListener(topics = "booking-events", groupId = "tracking-group")
    public void consume(BookingEvent event) {
        log.info("Received booking event: {} for booking {}", event.getStatus(), event.getBookingId());
        if (event.getBookingId() != null) {
            bookingStore.put(event.getBookingId(), event);
        }
        
        switch (event.getStatus() != null ? event.getStatus() : "") {
            case "ACCEPTED":
            case "CONFIRMED":
            case "ON_THE_WAY":
            case "ARRIVED":
            case "SERVICE_STARTED":
                log.info("Tracking active for booking {}", event.getBookingId());
                break;
            case "COMPLETED":
            case "CANCELLED":
            case "PENDING":
                log.info("Tracking inactive for booking {} with status {}", event.getBookingId(), event.getStatus());
                break;
            default:
                log.debug("No tracking action for status {}", event.getStatus());
        }
    }

    public BookingEvent getBooking(String bookingId) {
        return bookingStore.get(bookingId);
    }

    public void putBooking(String bookingId, BookingEvent event) {
        bookingStore.put(bookingId, event);
    }

    public boolean isBookingActiveForTracking(String bookingId) {
        if (bookingId == null) {
            return false;
        }
        BookingEvent event = bookingStore.get(bookingId);
        if (event == null) {
            return false; // Fail closed when booking state is unknown
        }
        String status = event.getStatus();
        if ("CANCELLED".equals(status) || "COMPLETED".equals(status) || "PENDING".equals(status) || status == null) {
            return false;
        }
        return "ACCEPTED".equals(status) || "CONFIRMED".equals(status) || "ON_THE_WAY".equals(status) || "ARRIVED".equals(status) || "SERVICE_STARTED".equals(status);
    }

    public boolean verifyCustomerOwnership(String bookingId, String customerId) {
        if (bookingId == null || customerId == null) {
            return false;
        }
        BookingEvent event = bookingStore.get(bookingId);
        if (event == null || event.getCustomerId() == null) {
            return false; // Fail closed when ownership information is unknown
        }
        return event.getCustomerId().equals(customerId);
    }
}
