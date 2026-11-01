# Tasks: Customer Registration in Auth Service

**Input**: Design documents from `/specs/002-customer-registration/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/auth-register-api.yaml

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story, leveraging existing auth-service infrastructure.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1)
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

**Purpose**: Verify existing database migration schema, User entity, UserRepository, and PasswordEncoder

- [x] T003 Verify existing Flyway migration script in backend-microservices/auth-service/src/main/resources/db/migration/V1__init_schema.sql
- [x] T004 [P] Verify existing User entity in backend-microservices/auth-service/src/main/java/com/easylabour/authservice/domain/model/User.java
- [x] T005 [P] Verify existing UserRepository in backend-microservices/auth-service/src/main/java/com/easylabour/authservice/domain/repository/UserRepository.java
- [x] T006 Verify existing PasswordEncoder bean setup in backend-microservices/auth-service/src/main/java/com/easylabour/authservice/config/ApplicationConfig.java

**Checkpoint**: Existing foundation verified - proceed to User Story implementation

---

## Phase 3: User Story 1 - New Customer Registration (Priority: P1) 🎯 MVP

**Goal**: Implement customer registration via `POST /api/v1/auth/register`, enforcing syntactic mobile validation, duplicate mobile number checks returning HTTP 409 Conflict (with database uniqueness constraint as final safeguard), secure password hashing using existing `PasswordEncoder`, `CUSTOMER` role and `ACTIVE` status assignment, and deterministic error responses (HTTP 400, 409, 500, 503).

**Independent Test**: Submit valid unique credentials to `/api/v1/auth/register` and verify HTTP 200 success response with user created in database. Verify duplicate mobile returns HTTP 409.

### Automated Tests for User Story 1 (Added to existing AuthControllerTest.java)
- [x] T007 [P] [US1] Add unit test for successful registration of a new customer returning HTTP 200 in backend-microservices/auth-service/src/test/java/com/easylabour/authservice/web/controller/AuthControllerTest.java
- [x] T008 [P] [US1] Add test for duplicate mobile number registration returning HTTP 409 Conflict in backend-microservices/auth-service/src/test/java/com/easylabour/authservice/web/controller/AuthControllerTest.java
- [x] T009 [P] [US1] Add test for invalid mobile number format returning HTTP 400 Bad Request in backend-microservices/auth-service/src/test/java/com/easylabour/authservice/web/controller/AuthControllerTest.java
- [x] T010 [P] [US1] Add test for missing required registration fields returning HTTP 400 Bad Request in backend-microservices/auth-service/src/test/java/com/easylabour/authservice/web/controller/AuthControllerTest.java
- [x] T011 [P] [US1] Add test verifying password is securely hashed via PasswordEncoder and that neither plaintext password nor password hash is exposed in the registration response in backend-microservices/auth-service/src/test/java/com/easylabour/authservice/web/controller/AuthControllerTest.java
- [x] T012 [P] [US1] Add test for database/infrastructure outage returning HTTP 503 Service Unavailable in backend-microservices/auth-service/src/test/java/com/easylabour/authservice/web/controller/AuthControllerTest.java

### Implementation for User Story 1
- [x] T013 [US1] Refine registration endpoint in backend-microservices/auth-service/src/main/java/com/easylabour/authservice/web/controller/AuthController.java to enforce syntactic mobile validation, return HTTP 409 on duplicate mobile (relying on repository check and database uniqueness constraint), assign `CUSTOMER` role and `ACTIVE` status, hash password securely via `PasswordEncoder`, ensure no password or hash is returned in the response, and map infrastructure/service exceptions to HTTP 500 or 503

**Checkpoint**: User Story 1 is fully functional and independently testable

---

## Phase 4: Polish & Cross-Cutting Concerns

**Purpose**: Security hardening, documentation, and validation execution

- [x] T014 [P] Update quickstart instructions in backend-microservices/auth-service/specs/002-customer-registration/quickstart.md
- [x] T015 Verify no plaintext passwords or password hashes appear in logs (via code review)
- [x] T016 Execute quickstart validation scenarios and run `./gradlew :auth-service:test`

---

## Dependencies & Execution Order

### Phase Dependencies
- **Setup (Phase 1)**: No dependencies
- **Foundational (Phase 2)**: Depends on Phase 1 - Blocks user stories
- **User Story 1 (Phase 3)**: Depends on Phase 2 (MVP)
- **Polish (Phase 4)**: Depends on Phase 3

### Parallel Opportunities
- T001, T002 can run in parallel.
- T004, T005 can run in parallel.
- T007 through T012 (test addition tasks in AuthControllerTest.java) can run in parallel.
- T014 can run in parallel.

---

## Implementation Strategy

### MVP First (User Story 1 Only)
1. Complete Phase 1 and Phase 2.
2. Complete Phase 3 (User Story 1 tests added to `AuthControllerTest.java` and controller refinements).
3. Validate registration endpoint using `quickstart.md` and tests.
