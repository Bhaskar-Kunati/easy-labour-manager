package com.easylabour.trackingservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "location_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationHistory {
    @Id
    private String id;
    private String bookingId;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
}
