---
description: "Task list for Feature 009: Real-Time Service & Worker Tracking"
---

# Tasks: Real-Time Service & Worker Tracking

**Input**: Design documents from `backend-microservices/tracking-service/specs/009-realtime-worker-tracking/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic verification for tracking-service

- [X] T001 Verify tracking-service gradle build and dependencies in `backend-microservices/tracking-service/build.gradle`
- [X] T002 [P] Verify MongoDB and Kafka configuration properties in `backend-microservices/tracking-service/src/main/resources/application.yml`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T003 Implement or verify `LocationHistory` document model with fields (`bookingId`, `latitude`, `longitude`, `timestamp`) in `backend-microservices/tracking-service/src/main/java/com/easylabour/trackingservice/domain/model/LocationHistory.java`
- [X] T004 [P] Implement `LocationRepository` interface extending MongoRepository in `backend-microservices/tracking-service/src/main/java/com/easylabour/trackingservice/domain/repository/LocationRepository.java`
- [X] T005 Implement `BookingEventConsumer` to consume Kafka `booking-events` and maintain active/confirmed booking state tracking in `backend-microservices/tracking-service/src/main/java/com/easylabour/trackingservice/infrastructure/kafka/BookingEventConsumer.java`
- [X] T006 Configure Spring WebSocket STOMP message broker and endpoint `/ws-tracking` in `backend-microservices/tracking-service/src/main/java/com/easylabour/trackingservice/infrastructure/websocket/WebSocketConfig.java`

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Secure Historical & Real-Time Tracking for Customers (Priority: P1) 🎯 MVP

**Goal**: Enable authenticated customers to securely retrieve tracking history and subscribe to real-time WebSocket updates for their `CONFIRMED` bookings.

**Independent Test**: Can be tested by requesting `GET /api/tracking/history/{bookingId}` with owner credentials and connecting to `/ws-tracking` to subscribe to `/topic/tracking/{bookingId}`.

### Tests for User Story 1 (OPTIONAL)

- [X] T007 [P] [US1] Unit/Integration test for history retrieval and customer ownership authorization in `backend-microservices/tracking-service/src/test/java/com/easylabour/trackingservice/web/controller/TrackingControllerTest.java`
- [X] T008 [P] [US1] WebSocket authorization test for STOMP subscription verification in `backend-microservices/tracking-service/src/main/java/com/easylabour/trackingservice/infrastructure/websocket/WebSocketAuthTest.java`

### Implementation for User Story 1

- [X] T009 [US1] Implement `GET /api/tracking/history/{bookingId}` endpoint in `backend-microservices/tracking-service/src/main/java/com/easylabour/trackingservice/web/controller/TrackingController.java` enforcing security context customer ownership and active `CONFIRMED` booking state validation
- [X] T010 [US1] Add WebSocket STOMP channel interceptor in `backend-microservices/tracking-service/src/main/java/com/easylabour/trackingservice/infrastructure/websocket/WebSocketConfig.java` to verify JWT authentication and booking ownership upon subscription to `/topic/tracking/{bookingId}`

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - Authorized Worker/System Location Submission (Priority: P2)

**Goal**: Enable authorized workers or system actors to submit real-time location updates via `POST /api/tracking/update`, validating input coordinates, persisting to MongoDB, and broadcasting via STOMP.

**Independent Test**: Tested by invoking `POST /api/tracking/update` with valid coordinates for a `CONFIRMED` booking and verifying persistence and STOMP broadcast.

### Tests for User Story 2 (OPTIONAL)

- [X] T011 [P] [US2] Integration test for location submission, input validation (-90 to 90 lat, -180 to 180 lon), persistence, and STOMP broadcast in `backend-microservices/tracking-service/src/test/java/com/easylabour/trackingservice/application/service/TrackingServiceTest.java`

### Implementation for User Story 2

- [X] T012 [US2] Implement input validation annotations (`@NotNull`, `@DecimalMin("-90.0")`, `@DecimalMax("90.0")`, `@DecimalMin("-180.0")`, `@DecimalMax("180.0")`) on `TrackingEvent` DTO in `backend-microservices/common-lib/src/main/java/com/easylabour/common/event/TrackingEvent.java` (or local DTO equivalent)
- [X] T013 [US2] Implement `POST /api/tracking/update` endpoint in `backend-microservices/tracking-service/src/main/java/com/easylabour/trackingservice/web/controller/TrackingController.java` with worker/system authorization and active `CONFIRMED` booking state verification
- [X] T014 [US2] Implement `TrackingService.processLocationUpdate` method in `backend-microservices/tracking-service/src/main/java/com/easylabour/trackingservice/application/service/TrackingService.java` to validate booking state, persist `LocationHistory` to MongoDB, and broadcast via `SimpMessagingTemplate` to `/topic/tracking/{bookingId}`

**Checkpoint**: All user stories should now be independently functional

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [X] T015 [P] Update documentation and README instructions in `backend-microservices/tracking-service/README.md`
- [X] T016 Code cleanup, linting checks, and warning resolution across `tracking-service`
- [X] T017 Execute validation scenarios from `quickstart.md` against running microservices and MongoDB/Kafka infrastructure

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User Story 1 (P1) can proceed independently
  - User Story 2 (P2) can proceed independently
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on US2
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Operates independently of US1 customers

### Within Each User Story

- Tests (if included) before implementation
- Models/repositories before services
- Services before controllers/endpoints
- Core implementation before integration

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes, User Story 1 and User Story 2 can be worked on in parallel by different developers
- Tests within a story marked [P] can run in parallel

---

## Parallel Example: User Story 1

```bash
# Launch tests for User Story 1 together:
Task: "Unit/Integration test for history retrieval... in TrackingControllerTest.java"
Task: "WebSocket authorization test... in WebSocketAuthTest.java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test customer tracking history and WebSocket subscription independently

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo
4. Each story adds value without breaking previous stories

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story is independently completable and testable
