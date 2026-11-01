package com.easylabour.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEvent {
    private String bookingId;
    private String customerId;
    private String customerName;
    private String labourId;
    private String labourName;
    private String serviceType;
    private String location;
    private String duration;
    private String availabilityWindow;
    private String status;
    private LocalDateTime timestamp;
}
