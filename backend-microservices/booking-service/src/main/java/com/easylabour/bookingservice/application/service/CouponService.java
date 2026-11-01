package com.easylabour.bookingservice.application.service;

import com.easylabour.bookingservice.domain.model.Coupon;
import com.easylabour.bookingservice.domain.model.CouponStatus;
import com.easylabour.bookingservice.domain.model.CouponUsage;
import com.easylabour.bookingservice.domain.model.DiscountType;
import com.easylabour.bookingservice.domain.repository.CouponRepository;
import com.easylabour.bookingservice.domain.repository.CouponUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;

    @Transactional
    public Coupon createCoupon(Coupon coupon) {
        if (couponRepository.findByCode(coupon.getCode()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Coupon code already exists");
        }
        if (coupon.getStatus() == null) {
            coupon.setStatus(CouponStatus.ACTIVE);
        }
        return couponRepository.save(coupon);
    }

    @Transactional(readOnly = true)
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Coupon getCouponById(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon not found"));
    }

    @Transactional
    public Coupon updateCoupon(Long id, Coupon updated) {
        Coupon coupon = getCouponById(id);
        coupon.setDescription(updated.getDescription());
        coupon.setDiscountType(updated.getDiscountType());
        coupon.setDiscountValue(updated.getDiscountValue());
        coupon.setMinimumOrderAmount(updated.getMinimumOrderAmount());
        coupon.setMaximumDiscount(updated.getMaximumDiscount());
        coupon.setStartDate(updated.getStartDate());
        coupon.setEndDate(updated.getEndDate());
        coupon.setUsageLimit(updated.getUsageLimit());
        coupon.setPerCustomerLimit(updated.getPerCustomerLimit());
        coupon.setStatus(updated.getStatus());
        return couponRepository.save(coupon);
    }

    @Transactional
    public void deleteCoupon(Long id) {
        Coupon coupon = getCouponById(id);
        coupon.setStatus(CouponStatus.INACTIVE);
        couponRepository.save(coupon);
    }

    @Transactional
    public BigDecimal validateAndCalculateDiscount(String code, String customerId, BigDecimal orderAmount) {
        if (code == null || code.isBlank()) {
            return BigDecimal.ZERO;
        }

        Coupon coupon = couponRepository.findByCodeAndStatus(code, CouponStatus.ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or inactive coupon code"));

        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon is not yet active");
        }
        if (coupon.getEndDate() != null && now.isAfter(coupon.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon has expired");
        }

        if (coupon.getMinimumOrderAmount() != null && orderAmount.compareTo(coupon.getMinimumOrderAmount()) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order amount is below minimum required for this coupon");
        }

        if (coupon.getUsageLimit() != null) {
            long totalUsage = couponUsageRepository.countByCouponId(coupon.getId());
            if (totalUsage >= coupon.getUsageLimit()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon usage limit has been reached");
            }
        }

        if (coupon.getPerCustomerLimit() != null && customerId != null) {
            long customerUsage = couponUsageRepository.countByCouponIdAndCustomerId(coupon.getId(), customerId);
            if (customerUsage >= coupon.getPerCustomerLimit()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon per-customer usage limit has been reached");
            }
        }

        BigDecimal discount = BigDecimal.ZERO;
        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            discount = orderAmount.multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (coupon.getMaximumDiscount() != null && discount.compareTo(coupon.getMaximumDiscount()) > 0) {
                discount = coupon.getMaximumDiscount();
            }
        } else if (coupon.getDiscountType() == DiscountType.FIXED) {
            discount = coupon.getDiscountValue();
        }

        if (discount.compareTo(orderAmount) > 0) {
            discount = orderAmount;
        }
        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            discount = BigDecimal.ZERO;
        }

        return discount;
    }

    @Transactional
    public BigDecimal applyAndRecordCoupon(String code, String customerId, BigDecimal orderAmount, Long bookingId) {
        if (code == null || code.isBlank()) {
            return BigDecimal.ZERO;
        }

        // Use pessimistic write lock on the coupon row to prevent race conditions under high concurrency
        Coupon coupon = couponRepository.findByCodeAndStatusWithLock(code, CouponStatus.ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or inactive coupon code"));

        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon is not yet active");
        }
        if (coupon.getEndDate() != null && now.isAfter(coupon.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon has expired");
        }

        if (coupon.getMinimumOrderAmount() != null && orderAmount.compareTo(coupon.getMinimumOrderAmount()) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order amount is below minimum required for this coupon");
        }

        if (coupon.getUsageLimit() != null) {
            long totalUsage = couponUsageRepository.countByCouponId(coupon.getId());
            if (totalUsage >= coupon.getUsageLimit()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon usage limit has been reached");
            }
        }

        if (coupon.getPerCustomerLimit() != null && customerId != null) {
            long customerUsage = couponUsageRepository.countByCouponIdAndCustomerId(coupon.getId(), customerId);
            if (customerUsage >= coupon.getPerCustomerLimit()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon per-customer usage limit has been reached");
            }
        }

        BigDecimal discount = BigDecimal.ZERO;
        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            discount = orderAmount.multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (coupon.getMaximumDiscount() != null && discount.compareTo(coupon.getMaximumDiscount()) > 0) {
                discount = coupon.getMaximumDiscount();
            }
        } else if (coupon.getDiscountType() == DiscountType.FIXED) {
            discount = coupon.getDiscountValue();
        }

        if (discount.compareTo(orderAmount) > 0) {
            discount = orderAmount;
        }
        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            discount = BigDecimal.ZERO;
        }

        CouponUsage usage = CouponUsage.builder()
                .couponId(coupon.getId())
                .customerId(customerId)
                .bookingId(bookingId)
                .discountAmount(discount)
                .build();
        couponUsageRepository.save(usage);

        return discount;
    }

    @Transactional
    public void recordUsage(String code, String customerId, Long bookingId, BigDecimal discountAmount) {
        if (code == null || code.isBlank()) {
            return;
        }
        Coupon coupon = couponRepository.findByCode(code).orElse(null);
        if (coupon != null) {
            CouponUsage usage = CouponUsage.builder()
                    .couponId(coupon.getId())
                    .customerId(customerId)
                    .bookingId(bookingId)
                    .discountAmount(discountAmount)
                    .build();
            couponUsageRepository.save(usage);
        }
    }
}
