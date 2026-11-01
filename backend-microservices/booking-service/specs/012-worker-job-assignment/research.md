# Research: Worker Booking / Job Assignment & Management

**Feature**: `012-worker-job-assignment`

## Research Topics & Findings

### 1. Existing Booking & Worker Entities
- **Decision**: Reuse `Booking`, `BookingWorker`, `BookingStatus`, and `BookingWorkerStatus` without introducing new lifecycle statuses.
- **Rationale**: The codebase already provides a comprehensive set of booking statuses (`PENDING`, `CONFIRMED`, `WORKER_ASSIGNED`, `SERVICE_COMPLETED`, etc.) and a `booking_workers` relationship table linking `bookings` to `workerId`.

### 2. Worker Eligibility Validation
- **Decision**: Implement worker validation in `BookingService` by calling `user-service` APIs (or repositories if sharing DB/context, though microservices use REST/Feign) to verify:
  - Worker verification status (`WorkerVerificationStatus.APPROVED`)
  - Worker skills (`WorkerSkill` matching service category)
  - Worker availability (`WorkerAvailability.AVAILABLE`)
- **Rationale**: Ensures strict adherence to Feature 011 business rules before assigning a worker to a booking.

### 3. Security & Identity Resolution
- **Decision**: Extract authenticated worker or customer identity strictly from `SecurityContextHolder.getContext().getAuthentication().getName()` (phone number / user ID), resolving corresponding user records via User Service or internal mapping.
- **Rationale**: Prevents IDOR/BOLA vulnerabilities by ensuring clients cannot pass arbitrary `workerId` or `customerId` parameters for authorization.

### 4. Customer Assigned-Worker Visibility
- **Decision**: When a customer requests their booking (`GET /api/v1/bookings/{id}`), enrich the response DTO or entity representation with assigned worker summary information (full name, rating, profile image) fetched from `user-service`.
- **Rationale**: Fulfills User Story 4 requirements securely without exposing internal or private worker details.
