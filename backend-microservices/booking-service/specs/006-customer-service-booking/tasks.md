# Tasks: Customer Service Booking and Scheduling

**Input**: Design documents from `backend-microservices/booking-service/specs/006-customer-service-booking/`

**Prerequisites**: plan.md, spec.md, data-model.md, contracts/, research.md, quickstart.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure verification for booking-service

- [X] T001 Verify `booking-service` project structure and build configuration in `backend-microservices/booking-service/build.gradle`
- [X] T002 [P] Verify database schema and Flyway migration scripts for `bookings` table supporting `customerId`, `serviceId`, `serviceName`, `bookedPrice`, `pricingType`, `serviceDateTime`, `location`, `status`, and unique `idempotencyKey`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core repository, Kafka producer, and security infrastructure that MUST be verified before user story implementation/testing

- [X] T003 Verify `BookingRepository` in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/domain/repository/BookingRepository.java`
- [X] T004 [P] Verify Spring Kafka configuration and `BookingEventProducer` in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/infrastructure/kafka/BookingEventProducer.java`
- [X] T005 Verify security configuration and JWT extraction of customer identity in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/config/SecurityConfig.java`

---

## Phase 3: User Story 1 - Create Scheduled Service Booking with Idempotency (Priority: P1) 🎯 MVP

**Goal**: Authenticated customers can create scheduled bookings with idempotency protection and pricing snapshot via `POST /api/v1/bookings`.

**Independent Test**: Send authenticated `POST /api/v1/bookings` request with valid payload and `X-Idempotency-Key` header; verify HTTP 201 Created, pricing snapshot, `PENDING` status, and `BOOKING_CREATED` event emission. Verify retry with same key returns existing booking (HTTP 200 OK) and different payload returns HTTP 409 Conflict.

### Tests for User Story 1

- [X] T006 [P] [US1] Implement explicit unit/integration tests in `backend-microservices/booking-service/src/test/java/com/easylabour/bookingservice/web/controller/BookingControllerTest.java` covering:
  - Successful creation by authenticated customer with valid service, future `serviceDateTime`, and booking location
  - Pricing snapshot preservation (`serviceName` and `bookedPrice`) and `PENDING` initial status
  - Rejection of invalid or non-existent service ID
  - Rejection of past date/time with HTTP 400
  - Rejection of unauthenticated requests with HTTP 401
  - Idempotency handling: same key + same payload returns existing booking (HTTP 200 OK), same key + different payload returns HTTP 409 Conflict, and `BOOKING_CREATED` event emission verification

### Implementation for User Story 1

- [X] T007 [P] [US1] Inspect `Booking` model in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/domain/model/Booking.java` and `BookingStatus` enum in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/domain/model/BookingStatus.java`
- [X] T008 [US1] Implement service logic in `BookingService.createBooking(...)` in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/application/service/BookingService.java` to validate service against catalog, capture pricing snapshot, enforce idempotency key, save booking with `PENDING` status, and dispatch `BOOKING_CREATED` event
- [X] T009 [US1] Implement `BookingController.createBooking(...)` in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/web/controller/BookingController.java` accepting `X-Idempotency-Key` header and validating future `serviceDateTime` adhering to OpenAPI contract in `contracts/booking-api.yaml`

**Checkpoint**: User Story 1 is functional and testable independently.

---

## Phase 4: User Story 2 - View Customer Bookings & Details (Priority: P1)

**Goal**: Authenticated customers can view their active and past bookings (`GET /api/v1/bookings`) and inspection details (`GET /api/v1/bookings/{id}`) with strict customer ownership security.

**Independent Test**: Send authenticated `GET /api/v1/bookings` and `GET /api/v1/bookings/{id}` requests and verify only bookings belonging to the authenticated customer are returned. Verify unauthorized or cross-customer access returns HTTP 403/404.

### Tests for User Story 2

- [X] T010 [P] [US2] Implement explicit unit/integration tests in `backend-microservices/booking-service/src/test/java/com/easylabour/bookingservice/web/controller/BookingControllerTest.java` covering:
  - Listing active and past bookings exclusively for the authenticated customer (`GET /api/v1/bookings`)
  - Retrieving booking details by ID (`GET /api/v1/bookings/{id}`)
  - Enforcing customer ownership security (attempting to access another customer's booking returns HTTP 403 or 404)
  - Handling non-existent booking IDs with HTTP 404

### Implementation for User Story 2

- [X] T011 [US2] Implement customer-scoped retrieval methods in `BookingService` in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/application/service/BookingService.java`
- [X] T012 [US2] Implement `BookingController` endpoints (`GET /api/v1/bookings`, `GET /api/v1/bookings/{id}`) in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/web/controller/BookingController.java` adhering to OpenAPI contract in `contracts/booking-api.yaml`

**Checkpoint**: User Stories 1 and 2 both work independently and collectively (MVP complete).

---

## Phase 5: User Story 3 - Cancel Eligible Booking (Priority: P2)

**Goal**: Authenticated customers can cancel eligible bookings (`PENDING` or `CONFIRMED`) via `PUT /api/v1/bookings/{id}/cancel`.

**Independent Test**: Send authenticated `PUT /api/v1/bookings/{id}/cancel` request for an active booking and verify status transitions to `CANCELLED` and `BOOKING_CANCELLED` event is emitted. Verify cancellation of completed/cancelled bookings returns HTTP 400.

### Tests for User Story 3

- [X] T013 [P] [US3] Implement explicit unit/integration tests in `backend-microservices/booking-service/src/test/java/com/easylabour/bookingservice/web/controller/BookingControllerTest.java` covering:
  - Cancelling a `PENDING` booking successfully
  - Cancelling a `CONFIRMED` booking successfully
  - Rejecting cancellation for already `CANCELLED` bookings with HTTP 400
  - Rejecting cancellation for `COMPLETED` bookings with HTTP 400
  - Enforcing customer ownership security on cancellation (cannot cancel another customer's booking)
  - Verifying `BOOKING_CANCELLED` Kafka event emission

### Implementation for User Story 3

- [X] T014 [US3] Implement cancellation method in `BookingService` in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/application/service/BookingService.java` verifying cancellation eligibility and publishing `BOOKING_CANCELLED` event
- [X] T015 [US3] Implement `BookingController.cancelBooking(...)` in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/web/controller/BookingController.java` adhering to OpenAPI contract in `contracts/booking-api.yaml`

**Checkpoint**: All user stories should now be independently functional.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Validation, testing, and quickstart execution

- [X] T016 [P] Verify test dependencies (`spring-boot-starter-test`, `spring-security-test`) in `backend-microservices/booking-service/build.gradle`
- [X] T017 Run unit and integration tests for `booking-service` using `./gradlew :booking-service:test`
- [X] T018 Execute end-to-end validation scenarios per `quickstart.md`
- [X] T019 Code cleanup and verification against project coding conventions
