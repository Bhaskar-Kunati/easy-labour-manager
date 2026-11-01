---
description: "Task list for feature 011: Worker Profile & Skill Management"
---

# Tasks: Worker Profile & Skill Management

**Input**: Design documents from `backend-microservices/user-service/specs/011-worker-profile-skills/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Tests are included (unit and controller tests as mandated by Constitution).

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure for `user-service` worker features

- [ ] T001 Verify project structure and `build.gradle` configuration in `backend-microservices/user-service/build.gradle`
- [ ] T002 [P] Verify `application.yml` configuration and database connection settings in `backend-microservices/user-service/src/main/resources/application.yml`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure and repository interfaces that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T003 Verify existing `WorkerProfileRepository` in `backend-microservices/user-service/src/main/java/com/easylabour/userservice/domain/repository/WorkerProfileRepository.java`
- [ ] T004 [P] Verify existing `WorkerSkillRepository` in `backend-microservices/user-service/src/main/java/com/easylabour/userservice/domain/repository/WorkerSkillRepository.java`

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Worker Profile Retrieval & Update (Priority: P1) 🎯 MVP

**Goal**: Enable authenticated registered workers to view their own profile and update editable fields (`fullName`, `profileImage`, `experienceYears`, `workingRadius`) while protecting system/admin-controlled fields (`verificationStatus`, `rating`, `totalJobs`, `availabilityStatus`).

**Independent Test**: Test profile retrieval via `GET /api/v1/workers/profile` and profile update via `PUT /api/v1/workers/profile`.

### Tests for User Story 1
> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [ ] T005 [P] [US1] Create unit and controller tests for worker profile retrieval and update in `backend-microservices/user-service/src/test/java/com/easylabour/userservice/web/controller/WorkerProfileControllerTest.java`

### Implementation for User Story 1

- [ ] T006 [P] [US1] Verify `WorkerProfile` domain model entity in `backend-microservices/user-service/src/main/java/com/easylabour/userservice/domain/model/WorkerProfile.java` with editable and protected fields
- [ ] T007 [US1] Implement worker profile service methods in `backend-microservices/user-service/src/main/java/com/easylabour/userservice/application/service/WorkerService.java` handling profile lookup by security context principal and safe profile updates
- [ ] T008 [US1] Implement worker profile endpoints in `backend-microservices/user-service/src/main/java/com/easylabour/userservice/web/controller/WorkerController.java` exposing `GET /api/v1/workers/profile` and `PUT /api/v1/workers/profile` enforcing JWT identity extraction and blocking customer accounts

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - Worker Skill Management (Priority: P2)

**Goal**: Enable authenticated registered workers to add valid catalog service skills (preventing duplicates and invalid categories) and remove their own skills (preventing cross-worker removal).

**Independent Test**: Test skill addition via `POST /api/v1/workers/skills` and skill removal via `DELETE /api/v1/workers/skills/{skillId}`.

### Tests for User Story 2
> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [ ] T009 [P] [US2] Create unit and controller tests for worker skill management in `backend-microservices/user-service/src/test/java/com/easylabour/userservice/web/controller/WorkerSkillControllerTest.java`

### Implementation for User Story 2

- [ ] T010 [P] [US2] Verify `WorkerSkill` domain model entity in `backend-microservices/user-service/src/main/java/com/easylabour/userservice/domain/model/WorkerSkill.java` with unique constraint on `(worker_id, service_category_id)`
- [ ] T011 [US2] Implement worker skill service methods in `backend-microservices/user-service/src/main/java/com/easylabour/userservice/application/service/WorkerService.java` handling skill addition with duplicate/catalog validation and ownership-verified skill deletion
- [ ] T012 [US2] Implement worker skill endpoints in `backend-microservices/user-service/src/main/java/com/easylabour/userservice/web/controller/WorkerController.java` exposing `POST /api/v1/workers/skills` and `DELETE /api/v1/workers/skills/{skillId}`

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories and validation

- [ ] T013 [P] Add global exception handling and error response mapping for worker-service validation errors
- [ ] T014 Code cleanup, formatting, and validation against project guidelines
- [ ] T015 Run quickstart.md validation and execute unit/integration test suite via `./gradlew :user-service:test`

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
