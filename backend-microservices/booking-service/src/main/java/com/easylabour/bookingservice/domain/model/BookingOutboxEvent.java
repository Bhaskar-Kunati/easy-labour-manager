package com.easylabour.bookingservice.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_outbox_events", indexes = {
    @Index(name = "idx_outbox_status_next_retry", columnList = "status, nextRetryTime"),
    @Index(name = "idx_outbox_reference", columnList = "referenceId", unique = true)
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingOutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long bookingId;

    @Column(nullable = false)
    private String workerId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, unique = true)
    private String referenceId;

    @Column(nullable = false)
    private String status; // PENDING, COMPLETED, FAILED

    @Column(nullable = false)
    private int retryCount;

    @Column(nullable = false)
    private LocalDateTime nextRetryTime;

    @Column(length = 1000)
    private String lastError;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (nextRetryTime == null) {
            nextRetryTime = LocalDateTime.now();
        }
        if (status == null) {
            status = "PENDING";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
