---
description: "Task list for feature 010: Service Reviews, Ratings & Customer Support"
---

# Tasks: Service Reviews, Ratings & Customer Support

**Input**: Design documents from `backend-microservices/review-service/specs/010-service-reviews-ratings-and-support/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Tests are included (unit and integration tests as mandated by Constitution).

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure for `review-service`

- [X] T001 Verify project structure and `build.gradle` configuration in `backend-microservices/review-service/build.gradle`
- [X] T002 [P] Verify `application.yml` configuration and database connection settings in `backend-microservices/review-service/src/main/resources/application.yml`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure and database migration that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T003 Create Flyway migration script `V1__init_review_support_schema.sql` in `backend-microservices/review-service/src/main/resources/db/migration/V1__init_review_support_schema.sql` defining `reviews` and `support_tickets` tables with constraints and indexes
- [X] T004 [P] Create `ReviewRepository` interface in `backend-microservices/review-service/src/main/java/com/easylabour/reviewservice/domain/repository/ReviewRepository.java`
- [X] T005 [P] Create `SupportTicketRepository` interface in `backend-microservices/review-service/src/main/java/com/easylabour/reviewservice/domain/repository/SupportTicketRepository.java`

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Submit and Retrieve Service/Worker Reviews (Priority: P1) 🎯 MVP

**Goal**: Enable authenticated customers to submit 1–5 star ratings and feedback for completed bookings, prevent duplicate/invalid reviews, and retrieve worker reviews with ratings aggregation.

**Independent Test**: Test review submission via `POST /api/v1/reviews` and worker reviews retrieval via `GET /api/v1/reviews/worker/{workerId}`.

### Tests for User Story 1
> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [X] T006 [P] [US1] Create unit and integration tests for ReviewController and ReviewService in `backend-microservices/review-service/src/test/java/com/easylabour/reviewservice/web/controller/ReviewControllerTest.java`

### Implementation for User Story 1

- [X] T007 [P] [US1] Create `Review` domain model entity in `backend-microservices/review-service/src/main/java/com/easylabour/reviewservice/domain/model/Review.java` with fields: `id` (Long), `bookingId` (String, Unique, Not Null), `customerId` (String, Not Null), `workerId` (String, Not Null), `serviceId` (String, Nullable), `rating` (Integer, 1-5 constraint, Not Null), `comment` (String, Nullable), `createdAt` (LocalDateTime, Not Null)
- [X] T008 [US1] Implement `ReviewService` in `backend-microservices/review-service/src/main/java/com/easylabour/reviewservice/application/service/ReviewService.java` handling review submission, booking status checks (`COMPLETED`), ownership validation, duplicate review prevention, and rating aggregation
- [X] T009 [US1] Implement `ReviewController` in `backend-microservices/review-service/src/main/java/com/easylabour/reviewservice/web/controller/ReviewController.java` exposing `POST /api/v1/reviews` and `GET /api/v1/reviews/worker/{workerId}` endpoints adhering to security context and JWT extraction
- [X] T010 [P] [US1] Create Kafka event producer for review creation in `backend-microservices/review-service/src/main/java/com/easylabour/reviewservice/infrastructure/kafka/ReviewEventProducer.java`

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - Customer Support Ticketing (Priority: P2)

**Goal**: Enable authenticated customers to create support tickets/complaints related to their bookings and retrieve only their own tickets securely.

**Independent Test**: Test support ticket creation via `POST /api/v1/support` and retrieval via `GET /api/v1/support`.

### Tests for User Story 2
> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [X] T011 [P] [US2] Create unit and integration tests for SupportController and SupportService in `backend-microservices/review-service/src/test/java/com/easylabour/reviewservice/web/controller/SupportControllerTest.java`

### Implementation for User Story 2

- [X] T012 [P] [US2] Create `SupportTicket` domain model entity in `backend-microservices/review-service/src/main/java/com/easylabour/reviewservice/domain/model/SupportTicket.java` with fields: `id` (Long), `ticketId` (String, Unique, Not Null), `bookingId` (String, Nullable), `customerId` (String, Not Null), `subject` (String, Not Null), `message` (String, Not Null), `status` (String, Not Null), `createdAt` (LocalDateTime, Not Null), `updatedAt` (LocalDateTime, Nullable)
- [X] T013 [US2] Implement `SupportService` in `backend-microservices/review-service/src/main/java/com/easylabour/reviewservice/application/service/SupportService.java` handling support ticket creation and retrieval scoped strictly to authenticated customer ID
- [X] T014 [US2] Implement `SupportController` in `backend-microservices/review-service/src/main/java/com/easylabour/reviewservice/web/controller/SupportController.java` exposing `POST /api/v1/support` and `GET /api/v1/support` endpoints

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories and validation

- [X] T015 [P] Add global exception handling and error response mapping in `backend-microservices/review-service`
- [X] T016 Code cleanup, formatting, and validation against project guidelines
- [X] T017 Run quickstart.md validation and execute unit/integration test suite via `./gradlew :review-service:test`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2)
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Independently testable

### Within Each User Story

- Tests MUST be written and FAIL before implementation
- Models before services
- Services before endpoints
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes, all user stories can start in parallel (if team capacity allows)
- All tests for a user story marked [P] can run in parallel
- Models within a story marked [P] can run in parallel

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 independently

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo
