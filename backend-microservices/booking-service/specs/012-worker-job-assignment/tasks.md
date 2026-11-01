---
description: "Task list for feature 012: Worker Booking / Job Assignment & Management"
---

# Tasks: Worker Booking / Job Assignment & Management

**Input**: Design documents from `backend-microservices/booking-service/specs/012-worker-job-assignment/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Tests are included (unit, controller, security, and integration tests).

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure for `booking-service` worker features

- [ ] T001 Verify project structure and `build.gradle` configuration in `backend-microservices/booking-service/build.gradle`
- [ ] T002 [P] Verify `application.yml` configuration and database connection settings in `backend-microservices/booking-service/src/main/resources/application.yml`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core repositories and DTOs that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T003 Verify existing `BookingRepository` in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/domain/repository/BookingRepository.java`
- [ ] T004 [P] Verify existing `BookingWorkerRepository` in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/domain/repository/BookingWorkerRepository.java`
- [ ] T005 [P] Verify existing `Booking` entity in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/domain/model/Booking.java` and `BookingWorker` entity in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/domain/model/BookingWorker.java`

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Worker Job Visibility & Access Control (Priority: P1) 🎯 MVP

**Goal**: Enable authenticated registered workers to view bookings assigned to them and inspect their details securely, while blocking cross-worker access, customer access, and unauthenticated requests.

**Independent Test**: Test worker job listing via `GET /api/v1/workers/bookings` and booking details via `GET /api/v1/workers/bookings/{bookingId}` enforcing worker identity and ownership verification.

### Tests for User Story 1
- [ ] T006 [P] [US1] Create controller and security tests for worker job listing and detail retrieval in `backend-microservices/booking-service/src/test/java/com/easylabour/bookingservice/web/controller/WorkerBookingControllerTest.java`

### Implementation for User Story 1
- [ ] T007 [US1] Implement worker booking service methods in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/application/service/BookingService.java` for retrieving bookings assigned to authenticated worker ID extracted from SecurityContext
- [ ] T008 [US1] Implement worker booking endpoints in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/web/controller/WorkerBookingController.java` exposing `GET /api/v1/workers/bookings` and `GET /api/v1/workers/bookings/{bookingId}` enforcing worker identity and ownership checks (returning HTTP 403 for cross-worker access)

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - Worker Job Assignment & Eligibility (Priority: P2)

**Goal**: Enable authorized admins or system dispatchers to assign eligible workers to bookings with strict validation of verification status, matching skills, availability, duplicate prevention, and scheduling conflict checks.

**Independent Test**: Test worker assignment via `POST /api/v1/admin/bookings/{id}/assign-worker` validating eligibility rules, conflict prevention, and admin authorization.

### Tests for User Story 2
- [ ] T009 [P] [US2] Create controller and eligibility tests for worker assignment in `backend-microservices/booking-service/src/test/java/com/easylabour/bookingservice/web/controller/AdminBookingAssignmentTest.java`

### Implementation for User Story 2
- [ ] T010 [US2] Implement worker eligibility verification methods in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/application/service/BookingService.java` checking worker approval status (`APPROVED`), matching catalog skill, and availability status (`AVAILABLE`) via User Service integration or domain validation
- [ ] T011 [US2] Implement scheduling conflict and duplicate assignment prevention logic in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/application/service/WorkerAvailabilityService.java`
- [ ] T012 [US2] Implement or refine worker assignment endpoint in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/web/controller/AdminBookingController.java` exposing `POST /api/v1/admin/bookings/{id}/assign-worker` with `@PreAuthorize("hasRole('ADMIN')")` and strict validation

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - Worker Job Status Management (Priority: P3)

**Goal**: Enable authenticated assigned workers to update job status using existing platform booking statuses without introducing unapproved new lifecycle statuses or violating Feature 006 lifecycle rules.

**Independent Test**: Test worker job status update via `PUT /api/v1/workers/bookings/{bookingId}/status` with valid assigned worker JWT and existing booking status transitions.

### Tests for User Story 3
- [ ] T013 [P] [US3] Create controller tests for worker job status management in `backend-microservices/booking-service/src/test/java/com/easylabour/bookingservice/web/controller/WorkerBookingStatusTest.java`

### Implementation for User Story 3
- [ ] T014 [US3] Implement worker job status update service method in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/application/service/BookingService.java` validating assigned worker ownership and permitted status transitions using existing `BookingStatus` and `BookingWorkerStatus` enums
- [ ] T015 [US3] Implement worker job status endpoint in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/web/controller/WorkerBookingController.java` exposing `PUT /api/v1/workers/bookings/{bookingId}/status`

**Checkpoint**: At this point, User Stories 1, 2, AND 3 should work independently

---

## Phase 6: User Story 4 - Customer Booking Worker Visibility (Priority: P4)

**Goal**: Enable customers to view assigned worker summary details (full name, rating, profile image) for their own confirmed bookings.

**Independent Test**: Test customer booking retrieval via `GET /api/v1/bookings/{id}` verifying assigned worker summary is included.

### Tests for User Story 4
- [ ] T016 [P] [US4] Create controller tests for customer assigned-worker visibility in `backend-microservices/booking-service/src/test/java/com/easylabour/bookingservice/web/controller/CustomerBookingVisibilityTest.java`

### Implementation for User Story 4
- [ ] T017 [US4] Enhance customer booking detail retrieval service method in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/application/service/BookingService.java` to fetch and include assigned worker summary details from User Service / `BookingWorker` relation

**Checkpoint**: All user stories implemented and testable independently

---

## Phase 7: Polish, Negative/Security Testing & Regression Validation

**Purpose**: Comprehensive negative security testing, error handling, and regression testing for Features 006–011 compatibility.

- [ ] T018 [P] Add comprehensive negative security tests covering unauthenticated access, customer access to worker APIs, workers accessing/modifying other workers' bookings, and customers accessing other customers' bookings in `backend-microservices/booking-service/src/test/java/com/easylabour/bookingservice/security/BookingSecurityTest.java`
- [ ] T019 [P] Add validation and error handling tests covering unapproved workers, unavailable workers, missing skills, duplicate assignments, scheduling conflicts, and invalid status transitions in `backend-microservices/booking-service/src/test/java/com/easylabour/bookingservice/application/service/BookingValidationTest.java`
- [ ] T020 Code cleanup, formatting, and validation against project guidelines
- [ ] T021 Execute unit/integration test suite via `./gradlew :booking-service:test` and quickstart validation

---

## Dependencies & Execution Order

### Phase Dependencies
- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - US1 (P1) -> US2 (P2) -> US3 (P3) -> US4 (P4)
- **Polish (Final Phase)**: Depends on all user stories being complete

### Parallel Opportunities
- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel
- Test tasks marked [P] can run in parallel with design/implementation preparation
