package com.easylabour.bookingservice.web.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ScheduledBookingRequest {
    private Long serviceId;
    private Integer workerCount;
    private LocalDateTime scheduledStartTime;
    private Integer durationMinutes;
    private String address;
    private String landmark;
    private String specialInstructions;
    private String couponCode;
}
