# Feature Specification: Customer Service Booking and Scheduling in Booking Service

**Feature Branch**: `006-customer-service-booking`

**Created**: 2026-10-04

**Status**: Draft

**Input**: User description: "Feature 006: Customer Service Booking and Scheduling. Allow an authenticated customer to create a service booking from the existing service catalog, specify the required service date/time and service location, view their active and past bookings, view booking details, and cancel an eligible booking. Scheduled bookings only (instant bookings out of scope). PENDING initial status. Idempotency behavior for POST /api/v1/bookings using client-supplied idempotency key. Pricing snapshot preserving service name and price at booking creation. Location supplied specifically per booking. Kafka events BOOKING_CREATED and BOOKING_CANCELLED. Out of scope: worker matching, worker assignment/dispatch, payment processing, refunds, reviews, tracking, advanced scheduling, instant booking. APIs: POST /api/v1/bookings, GET /api/v1/bookings, GET /api/v1/bookings/{id}, PUT /api/v1/bookings/{id}/cancel."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Create Scheduled Service Booking with Idempotency (Priority: P1)

Authenticated customers can create a new scheduled service booking by selecting an available service from the catalog, specifying a future service date/time, providing a service location address, and supplying an idempotency key. The system validates the service reference, captures a pricing snapshot, initializes the booking with `PENDING` status, prevents duplicate bookings on retry with matching keys, and dispatches a creation event.

**Why this priority**: Booking creation is the core transaction that allows customers to secure scheduled labour services.

**Independent Test**: Can be fully tested by submitting a valid scheduled booking request with an authenticated JWT token and idempotency key to `POST /api/v1/bookings` and verifying that a record is successfully created and returned with HTTP 201 Created (or HTTP 200 OK on idempotency retry).

**Acceptance Scenarios**:

1. **Given** an authenticated customer with a valid JWT token, an active catalog service ID, a future service date/time, a service location, and an idempotency key, **When** the customer submits a booking request to `POST /api/v1/bookings`, **Then** the system validates the service, stores a pricing snapshot of the service name and price at booking time, creates the booking with status `PENDING`, publishes a `BOOKING_CREATED` event, and returns HTTP 201 Created with booking details.
2. **Given** a repeated booking request with the exact same idempotency key and identical booking payload, **When** submitted, **Then** the system prevents duplicate booking creation and returns the existing booking with HTTP 200 OK.
3. **Given** a booking request reusing an existing idempotency key with materially different booking payload data, **When** submitted, **Then** the system rejects the request with an HTTP 409 Conflict error.
4. **Given** a booking request referencing an invalid or non-existent service ID, **When** submitted, **Then** the system rejects the request with an HTTP 400 Bad Request or HTTP 404 Not Found error.
5. **Given** a booking request with a service date/time in the past or instant booking request, **When** submitted, **Then** the system rejects the request with an HTTP 400 Bad Request validation error.
6. **Given** an unauthenticated request (missing or invalid JWT token), **When** booking creation is attempted, **Then** the system rejects the request with an HTTP 401 Unauthorized response.

---

### User Story 2 - View Customer Bookings & Details (Priority: P1)

Authenticated customers can view their active and past service bookings via `GET /api/v1/bookings` and inspect detailed information for a specific booking via `GET /api/v1/bookings/{id}`, ensuring strict data ownership security.

**Why this priority**: Customers need visibility into their scheduled and past service requests to manage appointments.

**Independent Test**: Can be fully tested by making authenticated `GET` requests to retrieve all bookings or a specific booking ID, verifying that only bookings belonging to the authenticated customer are returned.

**Acceptance Scenarios**:

1. **Given** an authenticated customer with existing bookings, **When** the customer requests their bookings via `GET /api/v1/bookings`, **Then** the system returns an HTTP 200 OK with a list of bookings owned exclusively by that customer.
2. **Given** an authenticated customer and a valid booking ID owned by them, **When** the customer requests booking details via `GET /api/v1/bookings/{id}`, **Then** the system returns the booking details with an HTTP 200 OK response.
3. **Given** an authenticated customer attempting to access a booking ID owned by another customer, **When** access is requested, **Then** the system rejects the request with an HTTP 403 Forbidden or HTTP 404 Not Found response.
4. **Given** an invalid or non-existent booking ID, **When** details are requested, **Then** the system returns an HTTP 404 Not Found response.

---

### User Story 3 - Cancel Eligible Booking (Priority: P2)

Authenticated customers can cancel an eligible booking (where current status is `PENDING` or `CONFIRMED`) via `PUT /api/v1/bookings/{id}/cancel`, resulting in a transition to `CANCELLED` status and a cancellation event dispatch.

**Why this priority**: Customers must be able to cancel bookings if their plans change before the scheduled service occurs.

**Independent Test**: Can be fully tested by cancelling an active pending/confirmed booking and verifying that its status updates to `CANCELLED` and a cancellation event is emitted.

**Acceptance Scenarios**:

1. **Given** an authenticated customer and an active booking in `PENDING` or `CONFIRMED` status, **When** the customer requests cancellation via `PUT /api/v1/bookings/{id}/cancel`, **Then** the system updates the booking status to `CANCELLED`, publishes a `BOOKING_CANCELLED` event, and returns HTTP 200 OK.
2. **Given** an attempt to cancel a booking in `CANCELLED` or `COMPLETED` status, **When** cancellation is requested, **Then** the system rejects the request with an HTTP 400 Bad Request error.
3. **Given** an unauthenticated request or an attempt to cancel another customer's booking, **When** cancellation is requested, **Then** the system rejects the request with HTTP 401 Unauthorized or HTTP 403 Forbidden / 404 Not Found.

---

### Edge Cases

- What happens if the catalog service pricing changes after a booking is created? (The booking preserves the pricing snapshot captured at booking time; catalog changes do not affect existing bookings).
- What happens if a customer supplies a location string without coordinates or complex map structures? (The MVP accepts a standard text/string location address supplied per request).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST provide a `POST /api/v1/bookings` endpoint allowing authenticated customers to create a scheduled service booking.
- **FR-002**: System MUST validate that the supplied `serviceId` references an active, available service in the catalog service.
- **FR-003**: System MUST capture and preserve the applicable service name and price at booking creation time as a pricing snapshot within the booking record, ensuring subsequent catalog updates do not alter existing booking pricing.
- **FR-004**: System MUST require a valid future service date/time for scheduled bookings, rejecting past timestamps with HTTP 400. Instant bookings are explicitly out of scope.
- **FR-005**: System MUST accept a service location address supplied specifically for each booking request (not automatically copied from the customer profile).
- **FR-006**: System MUST enforce strict customer ownership security so that customers can only view, inspect, or cancel their own bookings.
- **FR-007**: System MUST provide a `GET /api/v1/bookings` endpoint to list active and past bookings for the authenticated customer.
- **FR-008**: System MUST provide a `GET /api/v1/bookings/{id}` endpoint to view detailed information for a specific booking.
- **FR-009**: System MUST provide a `PUT /api/v1/bookings/{id}/cancel` endpoint to cancel eligible bookings (`PENDING` or `CONFIRMED`), rejecting non-cancellable states (`CANCELLED`, `COMPLETED`) with HTTP 400.
- **FR-010**: System MUST initialize newly created bookings with `PENDING` status.
- **FR-011**: System MUST support idempotency for `POST /api/v1/bookings` via a client-supplied idempotency key, returning the existing booking on retry with identical data, or returning HTTP 409 Conflict if the key is reused with different booking data.
- **FR-012**: System MUST publish Kafka events (`BOOKING_CREATED`, `BOOKING_CANCELLED`) upon successful booking creation and cancellation, utilizing the project's existing event-driven architecture without introducing new Kafka infrastructure.

### Security & Non-Functional Requirements

- **NFR-001**: Booking endpoints MUST be secured and accessible only via valid JWT access tokens routed through the API Gateway, extracting customer identity securely.
- **NFR-002**: Strict authorization checks MUST prevent cross-customer data access.

### Success Criteria

- **SC-001**: 100% of valid scheduled booking creation requests successfully persist bookings, preserve pricing snapshot, and emit Kafka events within established performance expectations (< 300ms).
- **SC-002**: 100% of unauthorized or cross-customer access attempts are correctly rejected with HTTP 401, 403, or 404.
- **SC-003**: Zero duplicate bookings created via retry/idempotency mechanisms.

### Key Entities

- **Booking**: Entity containing `id`, `customerId`, `serviceId`, `serviceName`, `bookedPrice`, `pricingType`, `serviceDateTime`, `location`, `status` (`PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED`), `idempotencyKey`, `createdAt`, and `updatedAt`.

### Explicit Out-of-Scope Items

- Instant bookings / real-time requests.
- Worker matching, assignment, or dispatch.
- Advanced scheduling calendar rules beyond future date/time validation.
- Payment processing, invoicing, or refunds.
- Ratings, reviews, or live GPS tracking / maps.
