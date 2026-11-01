# Implementation Plan: Booking Notifications & Event-Driven Alerts

**Branch**: `007-booking-notifications` | **Date**: 2026-10-04 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `backend-microservices/notification-service/specs/007-booking-notifications/spec.md`

## Summary

Implement MVP booking notifications in `notification-service` (`GET /api/v1/notifications`, `PUT /api/v1/notifications/{id}/read`, and Kafka event consumption of `BOOKING_CREATED` and `BOOKING_CANCELLED`) using PostgreSQL persistence, security context identity extraction, and idempotent event deduplication.

## Technical Context

**Language/Version**: Java 19 (Spring Boot 3.2)

**Primary Dependencies**: Spring Boot, Spring Security, Spring Data JPA, Spring Kafka, Lombok, PostgreSQL driver

**Storage**: PostgreSQL (`notifications` table)

**Testing**: JUnit 5, Spring Boot Test, Mockito

**Target Platform**: Java JVM / Docker / Local Environment

**Project Type**: Spring Boot Microservice (`notification-service`)

**Performance Goals**: < 200ms API response time for notification operations

**Constraints**: Security context customer identity extraction, Kafka event idempotency, PostgreSQL storage

**Scale/Scope**: Microservice-level feature for in-app booking notifications

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- Reuses existing codebase architecture and service boundaries: **PASS**
- No duplicate entities or database tables: **PASS**
- Proper security isolation and JWT integration: **PASS**

## Project Structure

### Documentation (this feature)

```text
backend-microservices/notification-service/specs/007-booking-notifications/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
└── contracts/           # Phase 1 output
    └── notification-api.yaml
```

### Source Code (repository root)

```text
backend-microservices/notification-service/
├── src/main/java/com/easylabour/notificationservice/
│   ├── domain/model/
│   │   └── Notification.java
│   ├── domain/repository/
│   │   └── NotificationRepository.java
│   ├── application/service/
│   │   └── NotificationService.java
│   ├── infrastructure/kafka/
│   │   └── EventConsumer.java
│   └── web/controller/
│       └── NotificationController.java
└── src/test/java/com/easylabour/notificationservice/
```

**Structure Decision**: Reuses existing `notification-service` module structure, entity, repository, service, controller, and Kafka consumer.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

None. All constraints satisfied without violations.
