package com.easylabour.trackingservice.application.service;

import com.easylabour.common.event.TrackingEvent;
import com.easylabour.trackingservice.domain.model.LocationHistory;
import com.easylabour.trackingservice.domain.repository.LocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackingServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private TrackingService trackingService;

    @Test
    void testProcessLocationUpdatePersistsAndBroadcasts() {
        TrackingEvent event = TrackingEvent.builder()
                .bookingId("booking-123")
                .latitude(12.34)
                .longitude(56.78)
                .timestamp(LocalDateTime.now())
                .build();

        trackingService.processLocationUpdate(event);

        // Verify persistence to MongoDB repository
        ArgumentCaptor<LocationHistory> captor = ArgumentCaptor.forClass(LocationHistory.class);
        verify(locationRepository, times(1)).save(captor.capture());
        LocationHistory saved = captor.getValue();
        assertEquals("booking-123", saved.getBookingId());
        assertEquals(12.34, saved.getLatitude());
        assertEquals(56.78, saved.getLongitude());

        // Verify STOMP broadcasting after successful persistence
        verify(messagingTemplate, times(1)).convertAndSend("/topic/tracking/booking-123", event);
    }
}
