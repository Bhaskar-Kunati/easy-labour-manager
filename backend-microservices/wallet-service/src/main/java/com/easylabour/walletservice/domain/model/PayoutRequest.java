package com.easylabour.walletservice.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payout_requests", indexes = {
    @Index(name = "idx_payout_user_id", columnList = "userId"),
    @Index(name = "idx_payout_status", columnList = "status"),
    @Index(name = "idx_payout_reference_id", columnList = "referenceId", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayoutRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long walletId;

    @Column(nullable = false, length = 100)
    private String userId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    @Builder.Default
    private String currency = "INR";

    @Column(nullable = false, length = 100)
    private String destinationId;

    @Column(nullable = false, length = 20)
    private String destinationType; // BANK_ACCOUNT or UPI

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED, PROCESSING, COMPLETED, FAILED, UNKNOWN

    @Column(nullable = false, unique = true, length = 100)
    private String referenceId;

    @Column(length = 100)
    private String gatewayReference;

    @Column(length = 255)
    private String failureReason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (currency == null) {
            currency = "INR";
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
