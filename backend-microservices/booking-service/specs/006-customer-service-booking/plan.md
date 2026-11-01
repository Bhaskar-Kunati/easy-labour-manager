# Implementation Plan: Customer Service Booking and Scheduling

**Branch**: `006-customer-service-booking` | **Date**: 2026-10-04 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `backend-microservices/booking-service/specs/006-customer-service-booking/spec.md`

## Summary

Implement MVP customer service booking and scheduling in `booking-service` (`POST /api/v1/bookings`, `GET /api/v1/bookings`, `GET /api/v1/bookings/{id}`, `PUT /api/v1/bookings/{id}/cancel`) supporting scheduled bookings, client idempotency keys, pricing snapshots, customer ownership security, and Kafka event emission (`BOOKING_CREATED`, `BOOKING_CANCELLED`).

## Technical Context

**Language/Version**: Java 19 (Spring Boot 3.2)

**Primary Dependencies**: Spring Boot, Spring Security, Spring Data JPA, Spring Kafka, Lombok, PostgreSQL driver

**Storage**: PostgreSQL (`bookings` table, `booking_history` table)

**Testing**: JUnit 5, Spring Boot Test, Mockito

**Target Platform**: Java JVM / Docker / Local Environment

**Project Type**: Spring Boot Microservice (`booking-service`)

**Performance Goals**: < 300ms API response time for booking operations

**Constraints**: JWT security enforcement, strict customer ownership security, idempotency protection, pricing snapshot

**Scale/Scope**: Microservice-level feature for customer booking lifecycle management

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- Reuses existing codebase architecture and service boundaries: **PASS**
- No duplicate entities or database tables: **PASS**
- Proper security isolation and JWT integration: **PASS**

## Project Structure

### Documentation (this feature)

```text
backend-microservices/booking-service/specs/006-customer-service-booking/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
└── contracts/           # Phase 1 output
    └── booking-api.yaml
```

### Source Code (repository root)

```text
backend-microservices/booking-service/
├── src/main/java/com/easylabour/bookingservice/
│   ├── domain/model/
│   │   ├── Booking.java
│   │   ├── BookingStatus.java
│   │   ├── BookingHistory.java
│   │   └── BookingWorker.java
│   ├── domain/repository/
│   │   ├── BookingRepository.java
│   │   └── BookingHistoryRepository.java
│   ├── application/service/
│   │   ├── BookingService.java
│   │   ├── PricingService.java
│   │   └── BookingReminderService.java
│   └── web/controller/
│       ├── BookingController.java
│       └── AdminBookingController.java
└── src/test/java/com/easylabour/bookingservice/
```

**Structure Decision**: Reuses existing `booking-service` module structure, entities, repositories, services, and controllers.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

None. All constraints satisfied without violations.
