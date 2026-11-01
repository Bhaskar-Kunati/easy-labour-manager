# Tasks: Booking Notifications & Event-Driven Alerts

**Input**: Design documents from `backend-microservices/notification-service/specs/007-booking-notifications/`

**Prerequisites**: plan.md, spec.md, data-model.md, contracts/, research.md, quickstart.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3, US4)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure verification for notification-service

- [X] T001 Verify `notification-service` project structure and build configuration in `backend-microservices/notification-service/build.gradle`
- [X] T002 [P] Verify database schema and Flyway migration scripts for `notifications` table supporting `userId`, `title`, `message`, `notificationType`, `referenceType`, `referenceId`, `isRead`, and `createdAt`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core repository and security infrastructure that MUST be verified before user story implementation/testing

- [X] T003 Verify `NotificationRepository` in `backend-microservices/notification-service/src/main/java/com/easylabour/notificationservice/domain/repository/NotificationRepository.java`
- [X] T004 [P] Verify Spring Security configuration and JWT extraction of user identity in `notification-service`

---

## Phase 3: User Story 1 & 2 - Booking Created & Cancelled Event Consumption (Priority: P1) 🎯 MVP

**Goal**: Automatically create unread in-app notifications (`isRead = false`) when `BOOKING_CREATED` and `BOOKING_CANCELLED` events are consumed from Kafka `booking-events` topic, with idempotency deduplication.

**Independent Test**: Publish `BOOKING_CREATED` and `BOOKING_CANCELLED` events to Kafka and verify corresponding unread notifications are persisted in PostgreSQL without duplicates on redelivery.

### Tests for User Story 1 & 2

- [X] T005 [P] [US1] Create unit test for Kafka event consumer deduplication and notification creation in `backend-microservices/notification-service/src/test/java/com/easylabour/notificationservice/infrastructure/kafka/EventConsumerTest.java`

### Implementation for User Story 1 & 2

- [X] T006 [P] [US1] Inspect `Notification` model in `backend-microservices/notification-service/src/main/java/com/easylabour/notificationservice/domain/model/Notification.java`
- [X] T007 [US1] Implement notification creation and idempotency deduplication logic in `NotificationService` in `backend-microservices/notification-service/src/main/java/com/easylabour/notificationservice/application/service/NotificationService.java` checking existing notifications by reference ID and notification type/status
- [X] T008 [US1] Update `EventConsumer.consumeBookingEvent(...)` in `backend-microservices/notification-service/src/main/java/com/easylabour/notificationservice/infrastructure/kafka/EventConsumer.java` to handle `BOOKING_CREATED` and `BOOKING_CANCELLED` status events from `booking-events` topic

**Checkpoint**: User Stories 1 and 2 are functional and testable independently.

---

## Phase 4: User Story 3 - View Notification History (Priority: P1)

**Goal**: Authenticated customers can retrieve their own notification history ordered newest first via `GET /api/v1/notifications`, with customer identity extracted exclusively from the security context.

**Independent Test**: Send authenticated `GET /api/v1/notifications` request and verify notifications belonging exclusively to the authenticated user are returned ordered newest first, with zero caller-supplied `userId` parameters trusted.

### Tests for User Story 3

- [X] T009 [P] [US3] Create controller test for `GET /api/v1/notifications` in `backend-microservices/notification-service/src/test/java/com/easylabour/notificationservice/web/controller/NotificationControllerTest.java` verifying security context identity extraction and newest-first ordering

### Implementation for User Story 3

- [X] T010 [US3] Implement customer-scoped retrieval query in `NotificationRepository` (`findByUserIdOrderByCreatedAtDesc`) in `backend-microservices/notification-service/src/main/java/com/easylabour/notificationservice/domain/repository/NotificationRepository.java`
- [X] T011 [US3] Update `NotificationController.getNotifications(...)` in `backend-microservices/notification-service/src/main/java/com/easylabour/notificationservice/web/controller/NotificationController.java` to extract identity strictly from `SecurityContextHolder` adhering to OpenAPI contract in `contracts/notification-api.yaml`

**Checkpoint**: User Story 3 is functional and testable independently.

---

## Phase 5: User Story 4 - Mark Notification as Read (Priority: P2)

**Goal**: Authenticated customers can mark their notifications as read via `PUT /api/v1/notifications/{id}/read`, returning HTTP 204 No Content with strict ownership validation.

**Independent Test**: Send authenticated `PUT /api/v1/notifications/{id}/read` request and verify notification is marked `isRead = true` with HTTP 204 No Content. Verify unauthorized or cross-customer access returns HTTP 403/404.

### Tests for User Story 4

- [X] T012 [P] [US4] Add controller test for `PUT /api/v1/notifications/{id}/read` in `backend-microservices/notification-service/src/test/java/com/easylabour/notificationservice/web/controller/NotificationControllerTest.java` verifying ownership enforcement, idempotent success (HTTP 204), and 403/404 handling

### Implementation for User Story 4

- [X] T013 [US4] Implement mark-as-read service method in `NotificationService` in `backend-microservices/notification-service/src/main/java/com/easylabour/notificationservice/application/service/NotificationService.java` validating ownership against authenticated user
- [X] T014 [US4] Update `NotificationController.markAsRead(...)` in `backend-microservices/notification-service/src/main/java/com/easylabour/notificationservice/web/controller/NotificationController.java` to extract identity from `SecurityContextHolder` and return HTTP 204 No Content adhering to OpenAPI contract in `contracts/notification-api.yaml`

**Checkpoint**: All user stories should now be independently functional.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Validation, testing, and quickstart execution

- [X] T015 [P] Add test dependencies (`spring-boot-starter-test`, `spring-security-test`) in `backend-microservices/notification-service/build.gradle` if missing
- [X] T016 Run unit and integration tests for `notification-service` using `./gradlew :notification-service:test`
- [X] T017 Execute end-to-end validation scenarios per `quickstart.md`
- [X] T018 Code cleanup and verification against project coding conventions
