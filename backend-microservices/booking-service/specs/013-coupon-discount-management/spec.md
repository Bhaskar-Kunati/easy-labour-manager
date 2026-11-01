# Feature Specification: Coupon / Discount Management & Admin APIs

**Feature Branch**: `013-coupon-discount-management`

**Created**: 2026-10-07

**Status**: Draft

**Input**: User description: "Create Feature 013 for the Easy Labour project. Feature: 013 — Coupon / Discount Management & Admin APIs Primary components: * catalog-service for coupon/discount management where appropriate * booking-service for applying validated coupons during booking checkout * existing admin/security infrastructure for administrative APIs..."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Admin Coupon Management (Priority: P1)

As an administrator, I want to create, update, enable, disable, and list promotional coupons so that I can manage platform discounts and promotional campaigns according to the master design.

**Why this priority**: Essential administrative foundation for promotional discounting and lifecycle management.

**Independent Test**: Can be fully tested by authenticating as an ADMIN, creating/updating/listing coupons via admin endpoints, and verifying status updates without customer booking interaction.

**Acceptance Scenarios**:

1. **Given** an authenticated user with ROLE_ADMIN, **When** they submit a valid coupon creation request with code, discount type, discount value, validity dates, usage limits, and minimum amount, **Then** the coupon is created successfully with active status.
2. **Given** an authenticated user with ROLE_ADMIN, **When** they update coupon status or details, **Then** the updates are persisted correctly.
3. **Given** an unauthenticated or non-admin user, **When** they attempt to access admin coupon management endpoints, **Then** the system rejects the request with 401 Unauthorized or 403 Forbidden.

---

### User Story 2 - Customer Coupon Application & Server-Side Validation (Priority: P1)

As a customer creating a booking, I want to apply a coupon code during checkout so that eligible discounts are calculated and applied securely on the server side based on authoritative pricing.

**Why this priority**: Core user-facing feature enabling promotional pricing during booking checkout while enforcing rigorous validation and security.

**Independent Test**: Can be tested by submitting a booking request with a coupon code, verifying server-side validation of active status, validity period, usage limits, minimum order amount, and correct discount calculation.

**Acceptance Scenarios**:

1. **Given** an authenticated customer with a valid booking payload and coupon code, **When** the coupon is applied during checkout, **Then** the system validates the coupon, calculates the discount authoritatively on the server side, and returns the discounted booking summary.
2. **Given** an expired, disabled, exhausted, or invalid coupon code, **When** a customer attempts to apply it, **Then** the system rejects it with an appropriate validation error.
3. **Given** a coupon requiring a minimum order amount or specific service category, **When** the booking does not meet criteria, **Then** the system rejects the coupon application.

---

### User Story 3 - Coupon Usage Tracking & Payment Integration (Priority: P2)

As the system, I want to record successful coupon usage in `coupon_usage` and ensure payment amounts stem strictly from authoritative discounted booking data.

**Why this priority**: Ensures tracking of promotional redemptions, prevents coupon reuse beyond allowed limits, and preserves Feature 008 payment integration rules.

**Independent Test**: Can be tested by completing a booking with a coupon and verifying that a corresponding `coupon_usage` record is created and per-user/global usage counts are incremented.

**Acceptance Scenarios**:

1. **Given** a successfully created booking with an applied coupon, **When** the booking is finalized, **Then** a usage record is saved in `coupon_usage` and usage limits are enforced.
2. **Given** payment processing, **When** payment is initiated for a discounted booking, **Then** the payment amount strictly uses the server-calculated authoritative discounted total from booking data.

---

### User Story 4 - Security & Access Control (Priority: P1)

As a security system, I want to enforce strict access control so that admin management APIs require ROLE_ADMIN, customers cannot modify coupons or access admin endpoints, and customer coupon operations are restricted exclusively to their own authenticated context.

**Why this priority**: Critical for data integrity, preventing price tampering, and unauthorized administrative actions.

**Independent Test**: Can be tested via security test suites attempting cross-role and cross-user coupon access.

**Acceptance Scenarios**:

1. **Given** a customer attempting to access admin coupon CRUD endpoints, **Then** the system returns 403 Forbidden.
2. **Given** an unauthenticated request to coupon application or management, **Then** the system returns 401 Unauthorized.
3. **Given** a customer attempting to apply a coupon using a manipulated price or external discount amount, **Then** the system ignores client-supplied pricing and calculates discounts authoritatively on the server.

### Edge Cases

- What happens when a coupon reaches its global usage limit exactly during concurrent booking checkouts? (Handled with transactional locks / validation checks to prevent over-redemption).
- What happens when a coupon validity period expires mid-checkout? (Rejected based on current server timestamp).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST allow administrators with ROLE_ADMIN to create, update, enable/disable, and list coupons.
- **FR-002**: System MUST validate coupon codes during booking checkout against active state, validity dates, global usage limits, per-customer usage limits, minimum booking amount, and service/category restrictions.
- **FR-003**: System MUST calculate discounts authoritatively on the server side using base service pricing; client-supplied discount amounts or final prices MUST never be trusted.
- **FR-004**: System MUST record successful coupon redemptions in `coupon_usage` and enforce redemption limits without creating duplicate tracking mechanisms.
- **FR-005**: System MUST enforce that administrative coupon management endpoints reject non-admin requests with 403 Forbidden and unauthenticated requests with 401 Unauthorized.
- **FR-006**: System MUST ensure customers can only apply coupons within their own authenticated booking context extracted from `SecurityContext`.

### Key Entities *(include if feature involves data)*

- **Coupons**: Represents promotional discount definitions (code, discount type [PERCENTAGE/FIXED], discount value, min order amount, max discount, start date, end date, usage limit, per-customer limit, status).
- **CouponUsage**: Tracks redemptions per coupon per user per booking (`coupon_id`, `user_id`, `booking_id`, `used_at`).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Administrators can manage coupons via secure admin APIs with 100% authorization accuracy.
- **SC-002**: Server-side coupon validation and discount calculation execute within 200ms during booking checkout.
- **SC-003**: Zero instances of client-supplied discount price tampering accepted by the booking or payment flow.
- **SC-004**: 100% of successful coupon redemptions accurately recorded in `coupon_usage` with strict enforcement of usage limits.

## Assumptions

- Reuses existing database models and tables (`coupons`, `coupon_usage`) defined in `DESIGN.md`.
- Relies on existing Spring Security JWT and `SecurityContext` authentication infrastructure.
- Payment gateway logic remains out of scope for Feature 013, relying on Feature 008 payment amount rules.
