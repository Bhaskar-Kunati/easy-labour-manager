# Tasks: Service Catalog and Search

**Input**: Design documents from `backend-microservices/catalog-service/specs/005-service-catalog-search/`

**Prerequisites**: plan.md, spec.md, data-model.md, contracts/, research.md, quickstart.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure verification for catalog-service

- [X] T001 Verify `catalog-service` project structure and build configuration in `backend-microservices/catalog-service/build.gradle`
- [X] T002 [P] Verify database schema and Flyway migration scripts for `service_categories` and `services` tables

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core repository and security infrastructure that MUST be verified before user story implementation/testing

- [X] T003 Verify `CategoryRepository` in `backend-microservices/catalog-service/src/main/java/com/easylabour/catalogservice/domain/repository/CategoryRepository.java`
- [X] T004 [P] Verify `ServiceRepository` in `backend-microservices/catalog-service/src/main/java/com/easylabour/catalogservice/domain/repository/ServiceRepository.java`
- [X] T005 Verify public read endpoint accessibility and security configuration in `catalog-service`

---

## Phase 3: User Story 1 - Browse Service Categories (Priority: P1) 🎯 MVP

**Goal**: Customers can browse and retrieve all active service categories and category details via `/api/v1/categories` and `/api/v1/categories/{id}`.

**Independent Test**: Send `GET /api/v1/categories` and `GET /api/v1/categories/{id}` requests and verify active categories and category details are returned with HTTP 200 OK.

### Tests for User Story 1

- [X] T006 [P] [US1] Create unit/integration test for category endpoints in `backend-microservices/catalog-service/src/test/java/com/easylabour/catalogservice/web/controller/CategoryControllerTest.java`

### Implementation for User Story 1

- [X] T007 [P] [US1] Inspect `ServiceCategory` model in `backend-microservices/catalog-service/src/main/java/com/easylabour/catalogservice/domain/model/ServiceCategory.java`
- [X] T008 [US1] Review `CategoryService` in `backend-microservices/catalog-service/src/main/java/com/easylabour/catalogservice/application/service/CategoryService.java`
- [X] T009 [US1] Verify `CategoryController` endpoints (`GET /api/v1/categories`, `GET /api/v1/categories/{id}`) in `backend-microservices/catalog-service/src/main/java/com/easylabour/catalogservice/web/controller/CategoryController.java` adhering to OpenAPI contract in `contracts/catalog-api.yaml`

**Checkpoint**: User Story 1 is functional and testable independently.

---

## Phase 4: User Story 2 - Browse and Filter Service Offerings (Priority: P1)

**Goal**: Customers can browse active service offerings, filter by category ID, and view service details via `/api/v1/services` and `/api/v1/services/{id}`.

**Independent Test**: Send `GET /api/v1/services`, `GET /api/v1/services?categoryId={id}`, and `GET /api/v1/services/{id}` requests and verify correct matching active services and details are returned with HTTP 200 OK.

### Tests for User Story 2

- [X] T010 [P] [US2] Create unit/integration test for service browsing and filtering endpoints in `backend-microservices/catalog-service/src/test/java/com/easylabour/catalogservice/web/controller/ServiceControllerTest.java`

### Implementation for User Story 2

- [X] T011 [P] [US2] Inspect `Service` model in `backend-microservices/catalog-service/src/main/java/com/easylabour/catalogservice/domain/model/Service.java`
- [X] T012 [US2] Review `ServiceService` in `backend-microservices/catalog-service/src/main/java/com/easylabour/catalogservice/application/service/ServiceService.java`
- [X] T013 [US2] Verify `ServiceController` endpoints (`GET /api/v1/services`, `GET /api/v1/services/{id}`) in `backend-microservices/catalog-service/src/main/java/com/easylabour/catalogservice/web/controller/ServiceController.java` adhering to OpenAPI contract in `contracts/catalog-api.yaml`

**Checkpoint**: User Stories 1 and 2 both work independently and collectively (MVP complete).

---

## Phase 5: User Story 3 - Search Services by Keyword (Priority: P2)

**Goal**: Customers can search for services by keyword via `/api/v1/services/search?query={term}`.

**Independent Test**: Send `GET /api/v1/services/search?query={term}` request and verify matching services are returned with HTTP 200 OK.

### Tests for User Story 3

- [X] T014 [P] [US3] Add unit/integration test for search endpoint in `backend-microservices/catalog-service/src/test/java/com/easylabour/catalogservice/web/controller/ServiceControllerTest.java`

### Implementation for User Story 3

- [X] T015 [US3] Verify search query repository method in `ServiceRepository` in `backend-microservices/catalog-service/src/main/java/com/easylabour/catalogservice/domain/repository/ServiceRepository.java`
- [X] T016 [US3] Verify search service method in `ServiceService` in `backend-microservices/catalog-service/src/main/java/com/easylabour/catalogservice/application/service/ServiceService.java`
- [X] T017 [US3] Verify `ServiceController.searchServices(@RequestParam String query)` endpoint in `backend-microservices/catalog-service/src/main/java/com/easylabour/catalogservice/web/controller/ServiceController.java` adhering to OpenAPI contract in `contracts/catalog-api.yaml`

**Checkpoint**: All user stories should now be independently functional.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Validation, testing, and quickstart execution

- [X] T018 [P] Add test dependencies (`spring-boot-starter-test`) in `backend-microservices/catalog-service/build.gradle` if missing
- [X] T019 Run unit and integration tests for `catalog-service` using `./gradlew :catalog-service:test`
- [X] T020 Execute end-to-end validation scenarios per `quickstart.md`
- [X] T021 Code cleanup and verification against project coding conventions
