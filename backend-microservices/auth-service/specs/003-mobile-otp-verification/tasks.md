# Tasks: Production-Grade Customer Mobile OTP Verification in Auth Service

**Input**: Design documents from `/specs/003-mobile-otp-verification/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/otp-api.yaml

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story, leveraging existing auth-service infrastructure (`OtpProvider`, `MockOtpProvider`, `UserRepository`, `User`).

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)
- Include exact file paths in descriptions

## Path Conventions
- **Component root**: `backend-microservices/auth-service/src/main/java/com/easylabour/authservice/`
- **Test root**: `backend-microservices/auth-service/src/test/java/com/easylabour/authservice/`
- **Resources root**: `backend-microservices/auth-service/src/main/resources/`

---

## Phase 1: Setup

**Purpose**: Verify build and configuration setup for mobile OTP verification

- [ ] T001 Verify existing Gradle dependencies in backend-microservices/auth-service/build.gradle
- [ ] T002 [P] Verify application configuration properties in backend-microservices/auth-service/src/main/resources/application.yml

---

## Phase 2: Foundational

**Purpose**: Verify existing `OtpProvider`, `MockOtpProvider`, `UserRepository`, and `User` entity (`isVerified` field)

- [ ] T003 Verify existing OtpProvider interface in backend-microservices/auth-service/src/main/java/com/easylabour/authservice/domain/service/OtpProvider.java
- [ ] T004 [P] Verify existing MockOtpProvider in backend-microservices/auth-service/src/main/java/com/easylabour/authservice/infrastructure/otp/MockOtpProvider.java
- [ ] T005 [P] Verify existing User entity and `isVerified` field in backend-microservices/auth-service/src/main/java/com/easylabour/authservice/domain/model/User.java
- [ ] T006 Verify existing UserRepository in backend-microservices/auth-service/src/main/java/com/easylabour/authservice/domain/repository/UserRepository.java

**Checkpoint**: Existing foundation verified - proceed to User Story implementation

---

## Phase 3: User Story 1 - Send Mobile OTP (Priority: P1)

**Goal**: Implement production-grade OTP generation and dispatch via `POST /api/v1/auth/send-otp`, tracking 5-minute expiration, superseding previous active OTPs, preventing user-enumeration (returning HTTP 400 for unknown mobile numbers), and handling provider outages (HTTP 503) without exposing plaintext OTPs in responses or logs.

**Independent Test**: Submit a registered mobile number to `/api/v1/auth/send-otp` and verify HTTP 200 success response with success message and zero OTP exposure.

### Automated Tests for User Story 1 (Must add to existing AuthControllerTest.java)
- [ ] T007 [P] [US1] Add unit test for successful OTP dispatch to a valid registered mobile number returning HTTP 200 in backend-microservices/auth-service/src/test/java/com/easylabour/authservice/web/controller/AuthControllerTest.java
- [ ] T008 [P] [US1] Add test for unknown mobile number returning HTTP 400 Bad Request to prevent user-enumeration in backend-microservices/auth-service/src/test/java/com/easylabour/authservice/web/controller/AuthControllerTest.java
- [ ] T009 [P] [US1] Add test for malformed or missing mobile number returning HTTP 400 Bad Request in backend-microservices/auth-service/src/test/java/com/easylabour/authservice/web/controller/AuthControllerTest.java
- [ ] T010 [P] [US1] Add test for OTP provider or infrastructure outage returning HTTP 503 Service Unavailable in backend-microservices/auth-service/src/test/java/com/easylabour/authservice/web/controller/AuthControllerTest.java

### Implementation for User Story 1
- [ ] T011 [US1] Refine `send-otp` endpoint in backend-microservices/auth-service/src/main/java/com/easylabour/authservice/web/controller/AuthController.java to generate a secure 6-digit OTP with a 5-minute expiration window, store it in transient session state (superseding previous active OTPs), dispatch via `OtpProvider`, protect against user-enumeration with HTTP 400 for unknown mobile numbers, and handle infrastructure failures with HTTP 503

**Checkpoint**: User Story 1 is fully functional and independently testable

---

## Phase 4: User Story 2 - Verify Mobile OTP & Update Verification State (Priority: P1) 🎯 MVP

**Goal**: Implement OTP verification via `POST /api/v1/auth/verify-otp`, validating authenticity and 5-minute expiration, updating `User.isVerified` to `true` on success, invalidating the OTP upon use, returning HTTP 400 for invalid/expired OTPs or unknown mobiles, and enforcing brute-force / rate-limiting protection.

**Independent Test**: Submit a valid OTP for a mobile number to `/api/v1/auth/verify-otp` and verify HTTP 200 success and that `User.isVerified` becomes `true` in the database.

### Automated Tests for User Story 2 (Must add to existing AuthControllerTest.java)
- [ ] T012 [P] [US2] Add unit test for successful OTP verification updating `User.isVerified` to `true` and returning HTTP 200 in backend-microservices/auth-service/src/test/java/com/easylabour/authservice/web/controller/AuthControllerTest.java
- [ ] T013 [P] [US2] Add test for incorrect/invalid OTP code returning HTTP 400 Bad Request in backend-microservices/auth-service/src/test/java/com/easylabour/authservice/web/controller/AuthControllerTest.java
- [ ] T014 [P] [US2] Add test for expired OTP code returning HTTP 400 Bad Request in backend-microservices/auth-service/src/test/java/com/easylabour/authservice/web/controller/AuthControllerTest.java
- [ ] T015 [P] [US2] Add test for unknown mobile number during verification returning HTTP 400 Bad Request in backend-microservices/auth-service/src/test/java/com/easylabour/authservice/web/controller/AuthControllerTest.java
- [ ] T016 [P] [US2] Add test for excessive failed verification attempts triggering brute-force protection (HTTP 400) in backend-microservices/auth-service/src/test/java/com/easylabour/authservice/web/controller/AuthControllerTest.java

### Implementation for User Story 2
- [ ] T017 [US2] Refine `verify-otp` endpoint in backend-microservices/auth-service/src/main/java/com/easylabour/authservice/web/controller/AuthController.java to validate OTP code and 5-minute expiration, update `User.isVerified` to `true` on success, invalidate single-use OTP, return HTTP 400 for invalid/expired codes or unknown mobiles, and implement brute-force / attempt tracking safeguards

**Checkpoint**: User Story 2 is fully functional and independently testable

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Security hardening, documentation, and validation execution

- [ ] T018 [P] Update quickstart instructions in backend-microservices/auth-service/specs/003-mobile-otp-verification/quickstart.md
- [ ] T019 Verify no plaintext OTP values appear in logs or API responses
- [ ] T020 Execute quickstart validation scenarios and run `./gradlew :auth-service:test`

---

## Dependencies & Execution Order

### Phase Dependencies
- **Setup (Phase 1)**: No dependencies
- **Foundational (Phase 2)**: Depends on Phase 1 - Blocks user stories
- **Send Mobile OTP (Phase 3)**: Depends on Phase 2
- **Verify Mobile OTP (Phase 4)**: Depends on Phase 3 (MVP)
- **Polish (Phase 5)**: Depends on Phase 4

### Parallel Opportunities
- T001, T002 can run in parallel.
- T004, T005 can run in parallel.
- T007 through T010 (Send OTP test tasks) can run in parallel.
- T012 through T016 (Verify OTP test tasks) can run in parallel.
- T018 can run in parallel.

---

## Implementation Strategy

### MVP First (Complete OTP Workflow)
1. Complete Phase 1 and Phase 2.
2. Complete Phase 3 (Send Mobile OTP).
3. Complete Phase 4 (Verify Mobile OTP & Update `isVerified`).
4. Validate end-to-end OTP flow using `quickstart.md` and tests.
