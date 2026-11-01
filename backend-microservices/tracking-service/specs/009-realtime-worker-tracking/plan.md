# Implementation Plan: Real-Time Service & Worker Tracking

**Branch**: `009-realtime-worker-tracking` | **Date**: October 5, 2026 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `backend-microservices/tracking-service/specs/009-realtime-worker-tracking/spec.md`

## Summary

Enable authenticated customers to securely view tracking history (`GET /api/tracking/history/{bookingId}`) and real-time location updates via WebSocket STOMP destination (`/topic/tracking/{bookingId}` over `/ws-tracking`) for active `CONFIRMED` bookings, while providing an authenticated endpoint (`POST /api/tracking/update`) for authorized workers/system actors to submit location updates persisted to MongoDB `LocationHistory` and broadcasted in real time.

## Technical Context

**Language/Version**: Java 19 (Spring Boot 3.2)

**Primary Dependencies**: Spring Web, Spring WebSocket, Spring Data MongoDB, Spring Kafka, Lombok, Spring Security

**Storage**: MongoDB (`trackingdb` / `LocationHistory` collection)

**Testing**: JUnit 5, Mockito, Spring Boot Test

**Target Platform**: JVM / Docker Container / Kubernetes

**Project Type**: Spring Boot Microservice (`tracking-service`)

**Performance Goals**: <500ms history retrieval, <200ms location update broadcast

**Constraints**: Strict booking lifecycle enforcement (`CONFIRMED` required for active tracking; `PENDING`, `CANCELLED`, `COMPLETED` restricted), security context customer ownership derivation, no third-party maps or tracking databases.

**Scale/Scope**: Microservice extension reusing existing tracking-service architecture and Kafka `booking-events`.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Modular Design**: Reuses existing `tracking-service`, `LocationRepository`, `TrackingService`, `TrackingController`, and `BookingEventConsumer`.
- **Security & Authorization**: Customer ownership enforced via security context; worker/system updates authenticated.
- **Simplicity**: No unnecessary new databases, tracking status models, or Kafka topics introduced.

## Project Structure

### Documentation (this feature)

```text
backend-microservices/tracking-service/specs/009-realtime-worker-tracking/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output
│   ├── rest-api.yaml
│   └── websocket-stomp.md
└── tasks.md             # Phase 2 output (generated later via /speckit.tasks)
```

### Source Code (`backend-microservices/tracking-service`)

```text
backend-microservices/tracking-service/
├── src/main/java/com/easylabour/trackingservice/
│   ├── TrackingServiceApplication.java
│   ├── application/service/TrackingService.java
│   ├── domain/model/LocationHistory.java
│   ├── domain/repository/LocationRepository.java
│   ├── infrastructure/
│   │   ├── kafka/BookingEventConsumer.java
│   │   └── websocket/WebSocketConfig.java
│   └── web/controller/TrackingController.java
└── src/test/java/com/easylabour/trackingservice/
```

**Structure Decision**: Reuses the existing structured Spring Boot microservice layout in `tracking-service`.
