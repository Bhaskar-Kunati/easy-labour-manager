package com.easylabour.bookingservice.web.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CatalogServiceDto {
    private Long id;
    private String name;
    private BigDecimal basePrice;
    private String pricingType;
    private String status;
}
