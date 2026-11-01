package com.easylabour.bookingservice.web.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingRequest {
    private Long serviceId;
    private LocalDateTime serviceDateTime;
    private String location;
    private String couponCode;
}
