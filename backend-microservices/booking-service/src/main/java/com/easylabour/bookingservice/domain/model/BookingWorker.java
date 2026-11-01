package com.easylabour.bookingservice.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_workers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingWorker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private String workerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingWorkerStatus status;

    private LocalDateTime assignedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime onTheWayAt;
    private LocalDateTime arrivalAt;
    private LocalDateTime serviceStartedAt;
    private LocalDateTime serviceCompletedAt;
    
    private BigDecimal earnings;

    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
