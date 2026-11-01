# WebSocket & STOMP Contract: Real-Time Tracking

**Feature**: `009-realtime-worker-tracking`

## Connection Endpoint
- **URL / SockJS / WebSocket Endpoint**: `/ws-tracking`
- **Protocol**: STOMP over WebSocket

## Authentication & Authorization
- **Connection Header**: Must include Authorization header with valid Bearer JWT token (`Authorization: Bearer <token>`).
- **Subscription Authorization**: When subscribing to `/topic/tracking/{bookingId}`, the server validates that the authenticated principal is authorized as the owner of the specified `bookingId`.

## Destinations

### 1. Subscription Destination (Customer Real-Time Feed)
- **Destination**: `/topic/tracking/{bookingId}`
- **Payload**: `TrackingEvent` JSON object:
  ```json
  {
    "bookingId": "string",
    "latitude": 0.0,
    "longitude": 0.0,
    "timestamp": "2026-10-05T12:00:00"
  }
  ```
