package com.easylabour.bookingservice.application.service;

import com.easylabour.bookingservice.domain.model.Coupon;
import com.easylabour.bookingservice.domain.model.CouponStatus;
import com.easylabour.bookingservice.domain.model.DiscountType;
import com.easylabour.bookingservice.domain.repository.CouponRepository;
import com.easylabour.bookingservice.domain.repository.CouponUsageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CouponConcurrencyTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponUsageRepository couponUsageRepository;

    @Test
    void testConcurrentGlobalUsageLimit() throws InterruptedException {
        Coupon coupon = Coupon.builder()
                .code("CONC1")
                .discountType(DiscountType.FIXED)
                .discountValue(BigDecimal.valueOf(100))
                .usageLimit(1)
                .status(CouponStatus.ACTIVE)
                .build();
        coupon = couponRepository.save(coupon);
        Long couponId = coupon.getId();

        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final String customerId = "cust-" + i;
            executor.submit(() -> {
                try {
                    latch.await();
                    couponService.applyAndRecordCoupon("CONC1", customerId, BigDecimal.valueOf(1000), 999L + Integer.parseInt(customerId.replace("cust-", "")));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                }
            });
        }

        latch.countDown();
        executor.shutdown();
        while (!executor.isTerminated()) {
            Thread.sleep(50);
        }

        assertEquals(1, successCount.get(), "Exactly one concurrent redemption should succeed");
        assertEquals(threadCount - 1, failureCount.get(), "Other concurrent redemption attempts should be rejected");
        assertEquals(1, couponUsageRepository.countByCouponId(couponId));
    }
}
