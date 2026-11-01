# Implementation Plan: Worker Booking / Job Assignment & Management

**Branch**: `012-worker-job-assignment` | **Date**: 2026-10-06 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `backend-microservices/booking-service/specs/012-worker-job-assignment/spec.md`

## Summary

Implement Feature 012 to enable secure worker job/booking visibility, robust worker assignment with eligibility checks (verification status, skills, availability), worker job status management using existing booking statuses (`BookingStatus`, `BookingWorkerStatus`), customer worker visibility, and strict JWT/SecurityContext authorization in `booking-service`.

## Technical Context

**Language/Version**: Java 19 (Spring Boot 3.2)

**Primary Dependencies**: Spring Boot Web, Spring Data JPA, Spring Security, Lombok, PostgreSQL, Spring Cloud Netflix Eureka / Feign (or RestTemplate), Kafka.

**Storage**: PostgreSQL (`el_db`) — Reusing existing `bookings`, `booking_workers`, and user-service tables (`worker_profiles`, `worker_skills`).

**Testing**: JUnit 5, Mockito, Spring Boot Test / MockMvc.

**Target Platform**: JVM / Docker container / Kubernetes.

**Project Type**: Spring Boot Microservice (`booking-service`).

**Performance Goals**: Sub-second API responses for worker job listings and booking detail retrievals.

**Constraints**: Strict JWT identity extraction via `SecurityContextHolder`, zero trust of client-supplied IDs, strict adherence to existing Feature 006 booking lifecycle and Feature 011 worker profiles.

**Scale/Scope**: Microservice module (`booking-service` and integration with `user-service`).

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Security & System Integrity**: Credentials protected, JWT-based security context used. [PASS]
- **Conventions & Style**: Adheres to Spring Boot 3.2 conventions, Java 19 best practices, Lombok, clean architecture. [PASS]
- **Types & Integrity**: Strongly typed, no warnings bypassed, robust exception handling. [PASS]

## Project Structure

### Documentation (this feature)
```text
backend-microservices/booking-service/specs/012-worker-job-assignment/
├── plan.md              # This file
├── research.md          # Phase 0 research findings
├── data-model.md        # Phase 1 data entities and relationships
├── quickstart.md        # Phase 1 validation guide
└── contracts/           # Phase 1 API contracts
    └── rest-api.yaml    # OpenAPI contract for worker & admin assignment APIs
```

### Source Code (`booking-service`)
```text
backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/
├── domain/
│   ├── model/           # Booking, BookingStatus, BookingWorker, BookingWorkerStatus
│   └── repository/      # BookingRepository, BookingWorkerRepository
├── application/
│   └── service/         # BookingService, WorkerAvailabilityService
└── web/
    ├── controller/      # WorkerBookingController, AdminBookingController, BookingController
    └── dto/             # Request/Response DTOs
```

## Proposed Changes & Architecture

1. **Worker Booking Endpoints (`WorkerBookingController`)**:
   - `GET /api/v1/workers/bookings`: List bookings assigned to authenticated worker (`SecurityContextHolder`).
   - `GET /api/v1/workers/bookings/{bookingId}`: Retrieve details of an assigned booking, enforcing ownership check.
   - `PUT /api/v1/workers/bookings/{bookingId}/status`: Update job status using existing booking/worker status enums.

2. **Admin / System Assignment (`AdminBookingController` / `BookingService`)**:
   - `POST /api/v1/bookings/{bookingId}/assign` (or `POST /api/v1/admin/bookings/{id}/assign-worker`): Validate worker eligibility (`WorkerVerificationStatus.APPROVED`, matching catalog skill, `WorkerAvailability.AVAILABLE`), prevent conflicts/duplicates, and assign worker.

3. **Customer Assigned-Worker Visibility**:
   - Enhance customer booking details retrieval (`GET /api/v1/bookings/{id}`) to include assigned worker summary details (full name, rating, profile image) fetched securely from user-service or via `BookingWorker` relationship.
