package com.easylabour.bookingservice.application.service;

import com.easylabour.bookingservice.domain.model.Coupon;
import com.easylabour.bookingservice.domain.model.CouponStatus;
import com.easylabour.bookingservice.domain.model.DiscountType;
import com.easylabour.bookingservice.domain.repository.CouponRepository;
import com.easylabour.bookingservice.domain.repository.CouponUsageRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CouponValidationTest {

    @Autowired
    private CouponService couponService;

    @MockBean
    private CouponRepository couponRepository;

    @MockBean
    private CouponUsageRepository couponUsageRepository;

    @Test
    void testValidateAndCalculatePercentageDiscount() {
        Coupon coupon = Coupon.builder()
                .id(1L)
                .code("SAVE20")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(BigDecimal.valueOf(20))
                .status(CouponStatus.ACTIVE)
                .build();

        Mockito.when(couponRepository.findByCodeAndStatus("SAVE20", CouponStatus.ACTIVE))
                .thenReturn(Optional.of(coupon));
        Mockito.when(couponUsageRepository.countByCouponId(1L)).thenReturn(0L);

        BigDecimal discount = couponService.validateAndCalculateDiscount("SAVE20", "cust-1", BigDecimal.valueOf(1000));
        assertEquals(0, BigDecimal.valueOf(200.00).compareTo(discount));
    }

    @Test
    void testExpiredCouponThrowsException() {
        Coupon coupon = Coupon.builder()
                .id(1L)
                .code("EXPIRED")
                .discountType(DiscountType.FIXED)
                .discountValue(BigDecimal.valueOf(50))
                .startDate(LocalDateTime.now().minusDays(10))
                .endDate(LocalDateTime.now().minusDays(1))
                .status(CouponStatus.ACTIVE)
                .build();

        Mockito.when(couponRepository.findByCodeAndStatus("EXPIRED", CouponStatus.ACTIVE))
                .thenReturn(Optional.of(coupon));

        assertThrows(ResponseStatusException.class, () -> {
            couponService.validateAndCalculateDiscount("EXPIRED", "cust-1", BigDecimal.valueOf(500));
        });
    }

    @Test
    void testBelowMinimumOrderAmountThrowsException() {
        Coupon coupon = Coupon.builder()
                .id(1L)
                .code("MIN500")
                .discountType(DiscountType.FIXED)
                .discountValue(BigDecimal.valueOf(50))
                .minimumOrderAmount(BigDecimal.valueOf(500))
                .status(CouponStatus.ACTIVE)
                .build();

        Mockito.when(couponRepository.findByCodeAndStatus("MIN500", CouponStatus.ACTIVE))
                .thenReturn(Optional.of(coupon));

        assertThrows(ResponseStatusException.class, () -> {
            couponService.validateAndCalculateDiscount("MIN500", "cust-1", BigDecimal.valueOf(300));
        });
    }
}
