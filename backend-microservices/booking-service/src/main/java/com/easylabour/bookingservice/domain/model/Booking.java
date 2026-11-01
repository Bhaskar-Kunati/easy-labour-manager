package com.easylabour.bookingservice.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String bookingNumber;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private Long serviceId;

    private String serviceName;
    private BigDecimal bookedPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingType bookingType;

    private Integer requestedWorkerCount;

    @Builder.Default
    private Integer assignedWorkerCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    private LocalDateTime serviceDateTime;
    private LocalDateTime scheduledStartTime;
    private Integer estimatedDurationMinutes;

    private String location;
    private String address;
    private String landmark;
    private String specialInstructions;

    private BigDecimal subtotal;
    private BigDecimal platformFee;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal totalAmount;

    private String paymentStatus;

    @Column(unique = true)
    private String idempotencyKey;

    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<BookingWorker> bookingWorkers;

    @Transient
    private String assignedWorkerName;

    @Transient
    private Double assignedWorkerRating;

    @Transient
    private String assignedWorkerProfileImage;
}
