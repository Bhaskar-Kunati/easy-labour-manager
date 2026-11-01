package com.easylabour.bookingservice.application.service;

import com.easylabour.bookingservice.domain.model.Coupon;
import com.easylabour.bookingservice.domain.model.CouponUsage;
import com.easylabour.bookingservice.domain.repository.CouponRepository;
import com.easylabour.bookingservice.domain.repository.CouponUsageRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class CouponUsageTest {

    @Autowired
    private CouponService couponService;

    @MockBean
    private CouponRepository couponRepository;

    @MockBean
    private CouponUsageRepository couponUsageRepository;

    @Test
    void testRecordUsageSuccess() {
        Coupon coupon = Coupon.builder()
                .id(1L)
                .code("SAVE10")
                .build();

        Mockito.when(couponRepository.findByCode("SAVE10")).thenReturn(Optional.of(coupon));
        Mockito.when(couponUsageRepository.save(any(CouponUsage.class))).thenReturn(new CouponUsage());

        couponService.recordUsage("SAVE10", "cust-1", 100L, BigDecimal.valueOf(50));
        Mockito.verify(couponUsageRepository, Mockito.times(1)).save(any(CouponUsage.class));
    }
}
