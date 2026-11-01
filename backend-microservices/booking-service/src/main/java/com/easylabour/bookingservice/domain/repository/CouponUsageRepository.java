package com.easylabour.bookingservice.domain.repository;

import com.easylabour.bookingservice.domain.model.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {
    long countByCouponId(Long couponId);
    long countByCouponIdAndCustomerId(Long couponId, String customerId);
    List<CouponUsage> findByCustomerId(String customerId);
    List<CouponUsage> findByCouponId(Long couponId);
}
