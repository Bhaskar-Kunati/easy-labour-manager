# Tasks: Customer Profile Management

**Input**: Design documents from `backend-microservices/user-service/specs/004-customer-profile/`

**Prerequisites**: plan.md, spec.md, data-model.md, contracts/, research.md, quickstart.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure verification for user-service

- [X] T001 Verify `user-service` project structure and existing build configuration in `backend-microservices/user-service/build.gradle`
- [X] T002 [P] Verify existing database schema and migrations for `customer_profiles` table

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core security and repository infrastructure that MUST be verified before user story verification

- [X] T003 Verify existing `CustomerProfileRepository` in `backend-microservices/user-service/src/main/java/com/easylabour/userservice/domain/repository/CustomerProfileRepository.java`
- [X] T004 [P] Verify existing `JpaUserRepository` in `backend-microservices/user-service/src/main/java/com/easylabour/userservice/infrastructure/persistence/JpaUserRepository.java`
- [X] T005 Verify Spring Security JWT authentication integration with `SecurityContextHolder` in `user-service`

---

## Phase 3: User Story 1 - View Customer Profile (Priority: P1) 🎯 MVP

**Goal**: Authenticated customers can view their own profile via `GET /api/v1/customers/me`

**Independent Test**: Send authenticated `GET /api/v1/customers/me` request with a valid JWT token and verify profile data is returned with HTTP 200 OK.

### Implementation for User Story 1

- [X] T006 [P] [US1] Inspect `CustomerProfile` model in `backend-microservices/user-service/src/main/java/com/easylabour/userservice/domain/model/CustomerProfile.java` to ensure attributes (`fullName`, `email`, `gender`, `profileImage`) match requirements
- [X] T007 [US1] Review `CustomerService.getCustomerProfile(String userId)` method in `backend-microservices/user-service/src/main/java/com/easylabour/userservice/application/service/CustomerService.java`
- [X] T008 [US1] Verify `CustomerController.getMe()` endpoint implementation in `backend-microservices/user-service/src/main/java/com/easylabour/userservice/web/controller/CustomerController.java` adhering to OpenAPI contract in `contracts/customer-profile-api.yaml`

**Checkpoint**: User Story 1 is functional and testable independently.

---

## Phase 4: User Story 2 - Update Customer Profile (Priority: P1)

**Goal**: Authenticated customers can update their profile via `PUT /api/v1/customers/me`

**Independent Test**: Send authenticated `PUT /api/v1/customers/me` request with valid update payload (`fullName`, `email`, `gender`, `profileImage`) and verify updates are persisted and returned with HTTP 200 OK.

### Implementation for User Story 2

- [X] T009 [US2] Review `CustomerService.updateCustomerProfile(String userId, CustomerProfile profileUpdate)` method in `backend-microservices/user-service/src/main/java/com/easylabour/userservice/application/service/CustomerService.java`
- [X] T010 [US2] Verify `CustomerController.updateMe(@RequestBody CustomerProfile profile)` endpoint implementation in `backend-microservices/user-service/src/main/java/com/easylabour/userservice/web/controller/CustomerController.java` adhering to OpenAPI contract in `contracts/customer-profile-api.yaml`
- [X] T011 [US2] Ensure input validation and error handling (HTTP 400 for bad request, HTTP 401 for unauthorized) are correctly enforced

**Checkpoint**: User Stories 1 and 2 both work independently and collectively.

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Validation, testing, and quickstart execution

- [X] T012 [P] Run unit and integration tests for `user-service` using `./gradlew :user-service:test`
- [X] T013 Execute end-to-end validation scenarios per `quickstart.md`
- [X] T014 Code cleanup and verification against project coding conventions
