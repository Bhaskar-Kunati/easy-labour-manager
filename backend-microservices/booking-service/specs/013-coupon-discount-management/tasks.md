---
description: "Task list for feature 013: Coupon / Discount Management & Admin APIs"
---

# Tasks: Coupon / Discount Management & Admin APIs

**Input**: Design documents from `backend-microservices/booking-service/specs/013-coupon-discount-management/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3, US4)
- Include exact file paths in descriptions

---

## Phase 1: Setup / Schema Verification

**Purpose**: Verify existing schema, entities, and data models for coupons and coupon usage.

- [x] T001 Verify existing Coupon entity and repository in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/domain/model/Coupon.java` and `CouponRepository.java`
- [x] T002 Verify existing CouponUsage entity and repository in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/domain/model/CouponUsage.java` and `CouponUsageRepository.java`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure and security configuration for admin and coupon processing.

- [x] T003 Verify security configuration and `@EnableMethodSecurity` in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/config/SecurityConfig.java`

---

## Phase 3: User Story 1 - Admin Coupon Management (Priority: P1) 🎯 MVP

**Goal**: Enable administrators with ROLE_ADMIN to create, update, enable/disable, and list promotional coupons.

**Independent Test**: Can be fully tested by authenticating as an ADMIN, creating/updating/listing coupons via admin endpoints.

### Implementation for User Story 1

- [x] T004 [US1] Implement Coupon service methods for create, update, list, and delete/deactivate in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/application/service/CouponService.java`
- [x] T005 [US1] Implement AdminCouponController exposing `/api/v1/admin/coupons` endpoints with `@PreAuthorize("hasRole('ADMIN')")` in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/web/controller/AdminCouponController.java`
- [x] T006 [US1] Create controller and security tests for admin coupon CRUD operations in `backend-microservices/booking-service/src/test/java/com/easylabour/bookingservice/web/controller/AdminCouponControllerTest.java`

**Checkpoint**: Admin coupon management is fully functional and testable independently.

---

## Phase 4: User Story 2 - Customer Coupon Application & Server-Side Validation (Priority: P1)

**Goal**: Allow customers to apply coupon codes during booking checkout with rigorous server-side validation and authoritative discount calculation.

**Independent Test**: Can be tested by submitting booking requests with coupon codes and verifying validation checks and discount calculations.

### Implementation for User Story 2

- [x] T007 [US2] Implement server-side coupon validation logic (active state, validity dates, global/per-customer limits, min amount, service restrictions) in `CouponService.java`
- [x] T008 [US2] Implement authoritative server-side discount calculation (percentage/fixed, max discount cap, negative/excessive protection) in `CouponService.java`
- [x] T009 [US2] Integrate coupon validation and discount calculation into booking creation flow in `backend-microservices/booking-service/src/main/java/com/easylabour/bookingservice/application/service/BookingService.java`
- [x] T010 [US2] Create unit tests for coupon validation and discount calculation in `backend-microservices/booking-service/src/test/java/com/easylabour/bookingservice/application/service/CouponValidationTest.java`

**Checkpoint**: Customer coupon application and server-side validation are fully operational.

---

## Phase 5: User Story 3 - Coupon Usage Tracking & Payment Integration (Priority: P2)

**Goal**: Record successful coupon usage in `coupon_usage` and ensure payment amounts stem strictly from authoritative discounted booking data.

**Independent Test**: Can be tested by completing bookings with coupons and verifying usage record persistence and payment total integrity.

### Implementation for User Story 3

- [x] T011 [US3] Implement `CouponUsage` creation and atomic usage counter incrementing in `CouponService.java` / `BookingService.java`
- [x] T012 [US3] Ensure discounted total is persisted authoritatively on the Booking entity for payment integration in `BookingService.java`
- [x] T013 [US3] Create integration tests for coupon usage tracking and payment amount integrity in `backend-microservices/booking-service/src/test/java/com/easylabour/bookingservice/application/service/CouponUsageTest.java`

**Checkpoint**: Coupon usage tracking and payment amount integrity are verified.

---

## Phase 6: User Story 4 - Security & Access Control (Priority: P1)

**Goal**: Enforce strict access control, customer isolation, and protection against price tampering.

**Independent Test**: Can be tested via security test suites attempting unauthorized access, cross-user access, and price tampering.

### Implementation for User Story 4

- [x] T014 [US4] Add comprehensive negative security tests for coupon endpoints (401 unauthenticated, 403 non-admin access, customer isolation) in `backend-microservices/booking-service/src/test/java/com/easylabour/bookingservice/security/CouponSecurityTest.java`
- [x] T015 [US4] Add tests preventing client-supplied price tampering and excessive discounts in `CouponValidationTest.java`

**Checkpoint**: Security boundaries and negative test cases are fully verified.

---

## Phase 7: Polish & Regression

**Purpose**: Final code review, formatting, quickstart validation, and regression testing.

- [x] T016 Perform code cleanup, formatting, and validation across all coupon-related files
- [x] T017 Run quickstart validation steps from `quickstart.md`
- [x] T018 Execute full test suite via `./gradlew :booking-service:test` to ensure zero regressions

---

## Dependencies & Execution Order

### Phase Dependencies
- **Phase 1 (Setup)**: No dependencies
- **Phase 2 (Foundational)**: Depends on Phase 1
- **Phase 3 (US1 - Admin Management)**: Depends on Phase 2
- **Phase 4 (US2 - Customer Application)**: Depends on Phase 2 & 3
- **Phase 5 (US3 - Usage & Payment)**: Depends on Phase 4
- **Phase 6 (US4 - Security)**: Depends on Phase 3 & 4
- **Phase 7 (Polish & Regression)**: Depends on all previous phases

### Parallel Opportunities
- T001 and T002 can run in parallel.
- T006, T010, T013, T014 test tasks can be written alongside implementation.
