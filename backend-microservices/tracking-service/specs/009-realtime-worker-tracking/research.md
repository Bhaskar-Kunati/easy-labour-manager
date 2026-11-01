# Research: Real-Time Service & Worker Tracking

**Feature**: `009-realtime-worker-tracking`

## Research Findings & Technical Decisions

### 1. Spring Security & JWT Context Derivation for REST & WebSocket
- **Decision**: Extract authenticated principal from Spring Security `SecurityContextHolder` or `@AuthenticationPrincipal` for REST endpoints (`GET /api/tracking/history/{bookingId}`), and enforce interceptors/channel interceptors for WebSocket STOMP connections (`/ws-tracking`).
- **Rationale**: Prevents trusting client-supplied IDs and ensures booking ownership validation.
- **Alternatives Considered**: Client-provided customer ID headers (rejected for security vulnerabilities).

### 2. Booking State Validation via Kafka & Booking Service Integration
- **Decision**: Consume `booking-events` via `BookingEventConsumer` to cache or verify booking lifecycle states (`CONFIRMED`, `CANCELLED`, `COMPLETED`), ensuring active tracking is restricted to `CONFIRMED` bookings.
- **Rationale**: Reuses existing Kafka event streaming infrastructure without introducing redundant REST calls or separate tracking state tables.
- **Alternatives Considered**: Polling booking-service via REST on every location update (rejected due to latency and tight coupling).

### 3. Location Input Validation
- **Decision**: Apply JSR-380 (`@NotNull`, `@DecimalMin`, `@DecimalMax`) validation annotations on `TrackingEvent` fields (latitude between -90 and 90, longitude between -180 and 180, non-empty booking ID).
- **Rationale**: Ensures malformed coordinate inputs are rejected early with validation errors.
- **Alternatives Considered**: Manual programmatic checks inside controller (rejected in favor of standard validation annotations).

### 4. WebSocket STOMP Channel Authorization
- **Decision**: Configure a `ChannelInterceptor` in `WebSocketConfig` to inspect CONNECT/SUBSCRIBE STOMP frames, validating authentication credentials and booking ownership before allowing subscription to `/topic/tracking/{bookingId}`.
- **Rationale**: Secures real-time event streams against unauthorized subscribers.
- **Alternatives Considered**: Unsecured public WebSocket topics (rejected due to privacy and security requirements).
