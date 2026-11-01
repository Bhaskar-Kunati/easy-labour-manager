package com.easylabour.notificationservice.application.service;

import com.easylabour.common.event.BookingEvent;
import com.easylabour.notificationservice.domain.model.Notification;
import com.easylabour.notificationservice.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<Notification> getNotifications(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public Long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAsRead(Long id, String userId) {
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        
        if (!notification.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to notification");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void processBookingEvent(BookingEvent event) {
        if (event.getBookingId() == null || event.getStatus() == null) {
            return;
        }

        String eventType = event.getStatus(); // e.g. "BOOKING_CREATED", "BOOKING_CANCELLED"
        
        // Idempotency check: prevent duplicate notifications for same bookingId and eventType
        Optional<Notification> existing = notificationRepository.findByReferenceIdAndNotificationType(event.getBookingId(), eventType);
        if (existing.isPresent()) {
            return;
        }

        String title;
        String message;
        if ("BOOKING_CREATED".equalsIgnoreCase(eventType)) {
            title = "Booking Confirmation";
            message = "Your service booking (" + event.getServiceType() + ") has been successfully created.";
        } else if ("BOOKING_CANCELLED".equalsIgnoreCase(eventType)) {
            title = "Booking Cancelled";
            message = "Your service booking (" + event.getServiceType() + ") has been cancelled.";
        } else {
            title = "Booking Update";
            message = "Your booking status changed to " + eventType;
        }

        Notification notification = Notification.builder()
                .userId(event.getCustomerId())
                .title(title)
                .message(message)
                .notificationType(eventType)
                .referenceType("BOOKING")
                .referenceId(event.getBookingId())
                .isRead(false)
                .build();

        notificationRepository.save(notification);
    }

    @Transactional
    public void saveNotification(Notification notification) {
        notificationRepository.save(notification);
    }
}
