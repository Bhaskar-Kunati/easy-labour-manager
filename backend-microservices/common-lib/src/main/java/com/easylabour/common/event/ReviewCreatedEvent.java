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
public class ReviewCreatedEvent {
    private Long reviewId;
    private String bookingId;
    private String raterId;
    private String rateeId;
    private Integer rating;
    private LocalDateTime timestamp;
}
