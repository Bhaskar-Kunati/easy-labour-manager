# Tasks: Customer Login with Mobile, Password, and JWT

**Input**: Design documents from `/specs/001-customer-login/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/auth-api.yaml

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story, leveraging existing auth-service infrastructure.

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

**Purpose**: Verify existing build and configuration setup in auth-service

- [x] T001 Verify existing Gradle dependencies in backend-microservices/auth-service/build.gradle
- [x] T002 [P] Verify application configuration properties in backend-microservices/auth-service/src/main/resources/application.yml

---

## Phase 2: Foundational

**Purpose**: Verify existing database schema, User entity, UserRepository, JwtService, and SecurityConfig

- [x] T003 Verify existing Flyway migration script in backend-microservices/auth-service/src/main/resources/db/migration/V1__init_schema.sql
- [x] T004 [P] Verify existing User entity in backend-microservices/auth-service/src/main/java/com/easylabour/authservice/domain/model/User.java
- [x] T005 [P] Verify existing UserRepository in backend-microservices/auth-service/src/main/java/com/easylabour/authservice/domain/repository/UserRepository.java
- [x] T006 [P] Verify existing JwtService in backend-microservices/auth-service/src/main/java/com/easylabour/authservice/security/JwtService.java
- [x] T007 Verify existing SecurityConfig and PasswordEncoder bean setup in backend-microservices/auth-service/src/main/java/com/easylabour/authservice/config/SecurityConfig.java

**Checkpoint**: Existing foundation verified - proceed to User Story implementation

---

## Phase 3: User Story 1 - Customer Authentication & Token Generation (Priority: P1) 🎯 MVP

**Goal**: Refine and verify customer login via `POST /api/v1/auth/login`, enforcing syntactic mobile validation, account status checks (`ACTIVE`, `SUSPENDED`, `BLOCKED`), secure JWT issuance, and database failure handling (HTTP 503).

**Independent Test**: Submit valid credentials for an active customer to `/api/v1/auth/login` and verify HTTP 200 with `{ "token": "...", "user": { ... } }`. Verify suspended/blocked accounts receive HTTP 401.

### Automated Tests for User Story 1 (Must write/verify)
- [x] T008 [P] [US1] Create unit test for successful login of an ACTIVE customer returning HTTP 200 and JWT token in backend-microservices/auth-service/src/test/java/com/easylabour/authservice/web/controller/AuthControllerTest.java
- [x] T009 [P] [US1] Create test for invalid password/credentials returning HTTP 401 in backend-microservices/auth-service/src/test/java/com/easylabour/authservice/web/controller/AuthControllerTest.java
- [x] T010 [P] [US1] Create test for invalid mobile number format returning validation error (HTTP 400) in backend-microservices/auth-service/src/test/java/com/easylabour/authservice/web/controller/AuthControllerTest.java
- [x] T011 [P] [US1] Create test for missing mobile/password fields returning HTTP 400 in backend-microservices/auth-service/src/test/java/com/easylabour/authservice/web/controller/AuthControllerTest.java
- [x] T012 [P] [US1] Create test for SUSPENDED customer returning HTTP 401 without exposing account status in backend-microservices/auth-service/src/test/java/com/easylabour/authservice/web/controller/AuthControllerTest.java
- [x] T013 [P] [US1] Create test for BLOCKED customer returning HTTP 401 in backend-microservices/auth-service/src/test/java/com/easylabour/authservice/web/controller/AuthControllerTest.java
- [x] T014 [P] [US1] Create test verifying JWT access token is signed, contains required claims, and expiration time in backend-microservices/auth-service/src/test/java/com/easylabour/authservice/web/controller/AuthControllerTest.java
- [x] T015 [P] [US1] Create test for authentication dependency/datastore failure returning HTTP 503 in backend-microservices/auth-service/src/test/java/com/easylabour/authservice/web/controller/AuthControllerTest.java

### Implementation & Adjustment for User Story 1
- [x] T016 [US1] Adjust existing login endpoint in backend-microservices/auth-service/src/main/java/com/easylabour/authservice/web/controller/AuthController.java to incorporate syntactic mobile validation and explicit `UserStatus` (`ACTIVE`, `SUSPENDED`, `BLOCKED`) verification
- [x] T017 [US1] Ensure login endpoint preserves existing response contract `{ "token": "...", "user": { ... } }` and robust exception handling for database failures returning HTTP 503

**Checkpoint**: User Story 1 is fully functional and independently testable

---

## Phase 4: User Story 2 - Authenticated Resource Access via JWT Integration (Priority: P2)

**Goal**: Verify JWT token validation and API Gateway integration for protected resources.

**Independent Test**: Make requests to protected endpoints with and without valid JWT tokens.

### Automated Tests for User Story 2 (Must write/verify)
- [x] T018 [P] [US2] Verify JwtAuthenticationFilter integration in backend-microservices/auth-service/src/main/java/com/easylabour/authservice/security/JwtAuthenticationFilter.java
- [x] T019 [P] [US2] Verify API Gateway routing configuration in backend-microservices/api-gateway/src/main/resources/application.yml for auth-service endpoints

### Implementation & Verification for User Story 2
- [x] T020 [US2] Verify `JwtAuthenticationFilter` in backend-microservices/auth-service/src/main/java/com/easylabour/authservice/security/JwtAuthenticationFilter.java correctly filters incoming requests
- [x] T021 [US2] Verify API Gateway routing configuration in backend-microservices/api-gateway/src/main/resources/application.yml for auth-service routes (modify only if gap found)

**Checkpoint**: User Stories 1 and 2 work seamlessly together

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Security hardening, documentation, and validation execution

- [x] T022 [P] Update quickstart instructions in backend-microservices/auth-service/specs/001-customer-login/quickstart.md
- [x] T023 Verify no plaintext passwords or sensitive details appear in logs or responses
- [x] T024 Execute quickstart validation scenarios and run `./gradlew :auth-service:test`

---

## Dependencies & Execution Order

### Phase Dependencies
- **Setup (Phase 1)**: No dependencies
- **Foundational (Phase 2)**: Depends on Phase 1 - Blocks user stories
- **User Story 1 (Phase 3)**: Depends on Phase 2 (MVP)
- **User Story 2 (Phase 4)**: Depends on Phase 3
- **Polish (Phase 5)**: Depends on Phase 3 and Phase 4

### Parallel Opportunities
- T001, T002 can run in parallel.
- T004, T005, T006 can run in parallel.
- T008 through T015 (test creation tasks) can run in parallel.
- T022 can run in parallel.

---

## Implementation Strategy

### MVP First (User Story 1 Only)
1. Complete Phase 1 and Phase 2.
2. Complete Phase 3 (User Story 1 tests and controller adjustments).
3. Validate login endpoint using `quickstart.md` and tests.
