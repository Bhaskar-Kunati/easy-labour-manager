package com.easylabour.trackingservice.application.service;

import com.easylabour.common.event.TrackingEvent;
import com.easylabour.trackingservice.domain.model.LocationHistory;
import com.easylabour.trackingservice.domain.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingService {

    private final LocationRepository locationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void processLocationUpdate(TrackingEvent event) {
        log.info("Received location update for booking {}: lat={}, lon={}", 
            event.getBookingId(), event.getLatitude(), event.getLongitude());

        // Store in MongoDB
        LocationHistory history = LocationHistory.builder()
            .bookingId(event.getBookingId())
            .latitude(event.getLatitude())
            .longitude(event.getLongitude())
            .timestamp(event.getTimestamp() != null ? event.getTimestamp() : LocalDateTime.now())
            .build();
        locationRepository.save(history);

        // Stream to WebSocket
        messagingTemplate.convertAndSend("/topic/tracking/" + event.getBookingId(), event);
    }
}
