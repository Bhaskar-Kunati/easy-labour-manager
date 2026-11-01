package com.easylabour.bookingservice.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupon_usage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @CreationTimestamp
    @Column(name = "used_at")
    private LocalDateTime usedAt;
}
