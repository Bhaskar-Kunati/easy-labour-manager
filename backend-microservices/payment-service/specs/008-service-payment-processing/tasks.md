---
description: "Task list for Service Payment Processing (Feature 008)"
---

# Tasks: Service Payment Processing (Feature 008)

**Input**: Design documents from `/specs/008-service-payment-processing/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/payment-api.yaml

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3, US4, US5)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic configuration for `payment-service`

- [x] T001 Verify project structure and Maven/Gradle dependencies in backend-microservices/payment-service/build.gradle
- [x] T002 [P] Configure application properties in backend-microservices/payment-service/src/main/resources/application.yml for DB and Kafka
- [x] T003 [P] Verify Flyway database migration V1__init_payment_schema.sql in backend-microservices/payment-service/src/main/resources/db/migration/V1__init_payment_schema.sql

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T004 Implement Payment entity model in backend-microservices/payment-service/src/main/java/com/easylabour/paymentservice/domain/model/Payment.java
- [x] T005 [P] Implement PaymentTransaction entity model in backend-microservices/payment-service/src/main/java/com/easylabour/paymentservice/domain/model/PaymentTransaction.java
- [x] T006 [P] Implement PaymentRepository interface in backend-microservices/payment-service/src/main/java/com/easylabour/paymentservice/domain/repository/PaymentRepository.java
- [x] T007 [P] Implement PaymentTransactionRepository interface in backend-microservices/payment-service/src/main/java/com/easylabour/paymentservice/domain/repository/PaymentTransactionRepository.java
- [x] T008 Configure Spring Security and JWT authentication filter in backend-microservices/payment-service
- [x] T009 Configure Kafka producer for `payment-events` topic using `PaymentEvent` from `common-lib` in backend-microservices/payment-service/src/main/java/com/easylabour/paymentservice/infrastructure/kafka/PaymentEventProducer.java

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Customer Initiates Payment (Priority: P1) 🎯 MVP

**Goal**: Allow authenticated customers to initiate payment for an eligible booking where the payment amount is retrieved from the booking context.

**Independent Test**: Invoke `POST /api/v1/payments/create-order` with valid JWT token and booking ID, verifying a `PENDING` payment record is created.

- [x] T010 [P] [US1] Create OrderRequest DTO in backend-microservices/payment-service/src/main/java/com/easylabour/paymentservice/web/dto/OrderRequest.java
- [x] T011 [US1] Implement MockPaymentGateway in backend-microservices/payment-service/src/main/java/com/easylabour/paymentservice/infrastructure/gateway/MockPaymentGateway.java
- [x] T012 [US1] Implement payment order creation logic and security context customer extraction in backend-microservices/payment-service/src/main/java/com/easylabour/paymentservice/application/service/PaymentService.java
- [x] T013 [US1] Implement PaymentController `POST /api/v1/payments/create-order` endpoint in backend-microservices/payment-service/src/main/java/com/easylabour/paymentservice/web/controller/PaymentController.java

**Checkpoint**: User Story 1 (MVP) fully functional and testable independently.

---

## Phase 4: User Story 2 - Successful Payment & Event Notification (Priority: P2)

**Goal**: Record successful payment verification through MockPaymentGateway and publish `PAYMENT_COMPLETED` event to Kafka `payment-events`.

**Independent Test**: Invoke `POST /api/v1/payments/verify` with valid payment ID and gateway payment ID, verifying status updates to `SUCCESS` and `PAYMENT_COMPLETED` event is emitted.

- [x] T014 [P] [US2] Create VerifyRequest DTO in backend-microservices/payment-service/src/main/java/com/easylabour/paymentservice/web/dto/VerifyRequest.java
- [x] T015 [US2] Implement payment verification and successful transaction logging in backend-microservices/payment-service/src/main/java/com/easylabour/paymentservice/application/service/PaymentService.java
- [x] T016 [US2] Implement `PAYMENT_COMPLETED` event publishing in backend-microservices/payment-service/src/main/java/com/easylabour/paymentservice/application/service/PaymentService.java
- [x] T017 [US2] Implement PaymentController `POST /api/v1/payments/verify` endpoint in backend-microservices/payment-service/src/main/java/com/easylabour/paymentservice/web/controller/PaymentController.java

**Checkpoint**: User Stories 1 and 2 work independently.

---

## Phase 5: User Story 3 - Failed Payment & Event Notification (Priority: P3)

**Goal**: Record failed payment verification and publish `PAYMENT_FAILED` event to Kafka `payment-events` without marking bookings as paid.

**Independent Test**: Simulate verification failure via MockPaymentGateway, verifying status updates to `FAILED` and `PAYMENT_FAILED` event is emitted.

- [x] T018 [US3] Implement failure handling and status persistence in backend-microservices/payment-service/src/main/java/com/easylabour/paymentservice/application/service/PaymentService.java
- [x] T019 [US3] Implement `PAYMENT_FAILED` event publishing in backend-microservices/payment-service/src/main/java/com/easylabour/paymentservice/application/service/PaymentService.java

**Checkpoint**: User Stories 1, 2, and 3 functional.

---

## Phase 6: User Story 4 - View Payment Status (Priority: P4)

**Goal**: Allow authenticated customers to view the payment status for their booking securely via `GET /api/v1/payments/status/{bookingId}`.

**Independent Test**: Invoke `GET /api/v1/payments/status/{bookingId}` with valid JWT token for an owned booking, verifying correct payment status is returned.

- [x] T020 [US4] Implement get payment status by booking ID method in backend-microservices/payment-service/src/main/java/com/easylabour/paymentservice/application/service/PaymentService.java
- [x] T021 [US4] Implement PaymentController `GET /api/v1/payments/status/{bookingId}` endpoint with ownership checks in backend-microservices/payment-service/src/main/java/com/easylabour/paymentservice/web/controller/PaymentController.java

**Checkpoint**: User Stories 1-4 functional.

---

## Phase 7: User Story 5 - Payment Idempotency and Safety (Priority: P5)

**Goal**: Ensure payment processing is safe against retries and duplicate requests, preventing double-charging on already paid bookings.

**Independent Test**: Attempt duplicate payment initiation or verification for an already paid booking, verifying rejection or safe handling.

- [x] T022 [US5] Implement idempotency check and guard against duplicate successful charges for already paid bookings in backend-microservices/payment-service/src/main/java/com/easylabour/paymentservice/application/service/PaymentService.java
- [x] T023 [US5] Enforce strict customer ownership validation from security context across all payment methods in backend-microservices/payment-service/src/main/java/com/easylabour/paymentservice/application/service/PaymentService.java

**Checkpoint**: All user stories fully implemented and secure.

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Improvements, validation, and documentation updates.

- [x] T024 [P] Update documentation and specs in backend-microservices/payment-service/specs/008-service-payment-processing/
- [x] T025 Run quickstart.md validation scenarios against running services
- [x] T026 Code cleanup and log verification across backend-microservices/payment-service

---

## Dependencies & Execution Order

### Phase Dependencies
- **Setup (Phase 1)**: No dependencies
- **Foundational (Phase 2)**: Depends on Setup - BLOCKS all user stories
- **User Stories (Phase 3-7)**: Depend on Foundational phase
- **Polish (Phase 8)**: Depends on all user stories

### User Story Dependencies
- **US1 (P1)**: Depends on Phase 2
- **US2 (P2)**: Depends on US1
- **US3 (P3)**: Depends on US1
- **US4 (P4)**: Depends on US1
- **US5 (P5)**: Depends on US1, US2, US3

### Parallel Opportunities
- T002, T003 in Phase 1
- T005, T006, T007 in Phase 2
- T010 in Phase 3
- T014 in Phase 4
- T024 in Phase 8
