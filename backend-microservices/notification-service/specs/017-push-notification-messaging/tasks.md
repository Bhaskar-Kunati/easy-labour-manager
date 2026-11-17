# Tasks: Push Notification & In-App Messaging Center (Feature 017)

**Input**: Design documents from `backend-microservices/notification-service/specs/017-push-notification-messaging/`

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and Firebase / MongoDB configuration for `notification-service`

- [ ] T001 Add `firebase-admin` dependency to `backend-microservices/notification-service/build.gradle`
- [ ] T002 [P] Configure MongoDB collection properties in `backend-microservices/notification-service/src/main/resources/application.yml`
- [ ] T003 [P] Configure Firebase initialization properties and local dev mock profile in `backend-microservices/notification-service/src/main/resources/application.yml`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure and security filters that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T004 Setup Spring Security JWT filter integration in `backend-microservices/notification-service/`
- [ ] T005 [P] Create base DTOs and common response wrappers in `backend-microservices/notification-service/src/main/java/com/easylabour/notificationservice/web/dto/`
- [ ] T006 Configure exception handling and error responses in `backend-microservices/notification-service/src/main/java/com/easylabour/notificationservice/exception/`

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Automated Event Push & In-App Notification Delivery (Priority: P1) 🎯 MVP

**Goal**: Deliver automated push notifications and in-app message persistence triggered by Kafka events from booking, payment, and tracking services.

**Independent Test**: Trigger a test booking event via Kafka and verify MongoDB persistence and FCM push dispatch.

### Implementation for User Story 1

- [ ] T007 [P] [US1] Create Notification entity model in `backend-microservices/notification-service/src/main/java/com/easylabour/notificationservice/domain/Notification.java`
- [ ] T008 [P] [US1] Create NotificationRepository interface in `backend-microservices/notification-service/src/main/java/com/easylabour/notificationservice/infrastructure/persistence/NotificationRepository.java`
- [ ] T009 [US1] Implement FCM messaging client service with local mock fallback in `backend-microservices/notification-service/src/main/java/com/easylabour/notificationservice/infrastructure/fcm/FcmClient.java`
- [ ] T010 [US1] Implement NotificationService business logic in `backend-microservices/notification-service/src/main/java/com/easylabour/notificationservice/application/service/NotificationService.java`
- [ ] T011 [US1] Extend Kafka event consumers to handle booking, payment, and tracking events in `backend-microservices/notification-service/src/main/java/com/easylabour/notificationservice/infrastructure/kafka/NotificationEventConsumer.java`
- [ ] T012 [US1] Implement NotificationController REST endpoints for listing and marking notifications as read in `backend-microservices/notification-service/src/main/java/com/easylabour/notificationservice/web/controller/NotificationController.java`

**Checkpoint**: User Story 1 is fully functional and testable independently

---

## Phase 4: User Story 2 - Device Token Management & Android Client Integration (Priority: P2)

**Goal**: Enable secure device FCM token registration/deregistration and implement the Android Jetpack Compose notification inbox.

**Independent Test**: Register a device token via REST and verify retrieval in Android notification inbox.

### Implementation for User Story 2

- [ ] T013 [P] [US2] Create DeviceToken entity model in `backend-microservices/notification-service/src/main/java/com/easylabour/notificationservice/domain/DeviceToken.java`
- [ ] T014 [P] [US2] Create DeviceTokenRepository interface in `backend-microservices/notification-service/src/main/java/com/easylabour/notificationservice/infrastructure/persistence/DeviceTokenRepository.java`
- [ ] T015 [US2] Implement DeviceTokenController REST endpoint for token registration (`POST /api/v1/notifications/tokens`) in `backend-microservices/notification-service/src/main/java/com/easylabour/notificationservice/web/controller/DeviceTokenController.java`
- [ ] T016 [P] [US2] Integrate Firebase Messaging SDK and foreground/background push handlers in `android-app/app/src/main/java/com/easylabour/app/service/MyFirebaseMessagingService.kt`
- [ ] T017 [US2] Implement Notification Inbox screen and ViewModel with Jetpack Compose in `android-app/app/src/main/java/com/easylabour/app/ui/notifications/`

**Checkpoint**: User Stories 1 and 2 work independently

---

## Phase 5: User Story 3 - Admin Broadcast & Targeted Announcements (Priority: P3)

**Goal**: Allow administrators to broadcast announcements or target specific customer/worker groups.

**Independent Test**: Dispatch a broadcast announcement from admin API and verify receipt.

### Implementation for User Story 3

- [ ] T018 [P] [US3] Create Announcement entity model in `backend-microservices/notification-service/src/main/java/com/easylabour/notificationservice/domain/Announcement.java`
- [ ] T019 [P] [US3] Create AnnouncementRepository interface in `backend-microservices/notification-service/src/main/java/com/easylabour/notificationservice/infrastructure/persistence/AnnouncementRepository.java`
- [ ] T020 [US3] Implement Admin Announcement controller (`POST /api/v1/admin/announcements`) with `ROLE_ADMIN` security in `backend-microservices/notification-service/src/main/java/com/easylabour/notificationservice/web/controller/AdminAnnouncementController.java`

**Checkpoint**: All user stories functional

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Validation, testing, and quickstart verification

- [ ] T021 [P] Add unit tests for NotificationService in `backend-microservices/notification-service/src/test/java/com/easylabour/notificationservice/application/service/NotificationServiceTest.java`
- [ ] T022 [P] Add controller security and integration tests in `backend-microservices/notification-service/src/test/java/com/easylabour/notificationservice/web/controller/NotificationControllerTest.java`
- [ ] T023 Run quickstart.md validation scenarios and verify backend compilation (`./gradlew compileJava`)

---

## Dependencies & Execution Order

### Phase Dependencies
- **Setup (Phase 1)**: No dependencies
- **Foundational (Phase 2)**: Depends on Setup - BLOCKS user stories
- **User Stories (Phase 3-5)**: Depend on Foundational phase
- **Polish (Phase 6)**: Depends on completion of all user stories

### User Story Dependencies
- **US1 (P1)**: Independent core messaging and Kafka integration
- **US2 (P2)**: Depends on backend token APIs (US1 foundation) and Android UI framework
- **US3 (P3)**: Depends on base notification dispatch logic (US1)

---

## Implementation Strategy

### MVP First (User Story 1 Only)
1. Setup + Foundational
2. User Story 1 (Kafka event push + MongoDB inbox)
3. STOP and VALIDATE

### Incremental Delivery
1. Add US2 (Device tokens + Android app integration)
2. Add US3 (Admin announcements)
3. Polish & Test
