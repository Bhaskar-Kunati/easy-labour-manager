package com.easylabour.bookingservice.application.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class PricingService {
    // Basic implementation: fetch from catalog-service in a real scenario
    public BigDecimal calculateBasePrice(Long serviceId, Integer workerCount, Integer durationMinutes) {
        // Dummy logic
        return new BigDecimal("100.00").multiply(new BigDecimal(workerCount))
            .multiply(new BigDecimal(durationMinutes).divide(new BigDecimal(60)));
    }

    public BigDecimal calculatePlatformFee(BigDecimal basePrice) {
        return basePrice.multiply(new BigDecimal("0.10"));
    }

    public BigDecimal calculateTax(BigDecimal basePrice, BigDecimal platformFee) {
        return basePrice.add(platformFee).multiply(new BigDecimal("0.05"));
    }
}
