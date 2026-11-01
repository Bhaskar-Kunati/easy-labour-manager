# Feature Specification: Service Reviews, Ratings & Customer Support

**Feature Branch**: `010-service-reviews-ratings-and-support`

**Created**: October 5, 2026

**Status**: Draft

**Input**: User description: "010-service-reviews-ratings-and-support Create the specification for Feature 010: Service Reviews, Ratings & Customer Support. Important existing signed-off features: 001 Customer Login, 002 Customer Registration, 003 Mobile OTP Verification, 004 Customer Profile Management, 005 Service Catalog & Search, 006 Customer Service Booking & Scheduling, 007 Booking Notifications & Event-Driven Alerts, 008 Service Payment Processing, 009 Real-Time Service & Worker Tracking. Feature 010 is owned by review-service."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Submit and Retrieve Service/Worker Reviews (Priority: P1)

As an authenticated customer, I want to submit a star rating (1–5) and optional feedback comment for my completed booking, and view reviews for workers and services so that I can evaluate service quality and share feedback.

**Why this priority**: Core value proposition enabling customer feedback and rating aggregation for completed services.

**Independent Test**: Can be fully tested by submitting a review for a `COMPLETED` booking via `POST /api/v1/reviews` and retrieving reviews via `GET /api/v1/reviews/worker/{workerId}`.

**Acceptance Scenarios**:

1. **Given** a customer has a `COMPLETED` booking, **When** they submit a 1–5 star rating and comment, **Then** the review is successfully saved and linked to the booking and worker/service.
2. **Given** a customer attempts to review a booking that is `PENDING`, `CONFIRMED`, or `CANCELLED`, **When** they submit a review, **Then** the request is rejected with a 403/400 status.
3. **Given** a customer attempts to review another customer's booking or submits with a spoofed customer ID, **When** the request is processed, **Then** ownership validation fails and access is denied (403 Forbidden).
4. **Given** a customer attempts to submit a duplicate review for the same booking, **When** submitted, **Then** the duplicate submission is rejected.

---

### User Story 2 - Customer Support Ticketing (Priority: P2)

As an authenticated customer, I want to create support tickets or complaints related to my booking and retrieve my tickets so that I can resolve issues.

**Why this priority**: Essential customer care and complaint management workflow following service delivery.

**Independent Test**: Can be tested by creating a support ticket via `POST /api/v1/support` and retrieving tickets via `GET /api/v1/support`.

**Acceptance Scenarios**:

1. **Given** an authenticated customer with a booking, **When** they submit a support ticket/complaint regarding their booking, **Then** the ticket is saved securely and linked to their customer ID.
2. **Given** a customer requests their support tickets, **When** authenticated, **Then** only their own tickets are returned, without exposing any information from other customers.

---

### Edge Cases

- What happens when a review is submitted for a booking ID that does not exist? System rejects with 404 Not Found.
- What happens when an unauthenticated user attempts to submit a review or support ticket? System rejects with 401 Unauthorized.
- What happens when a customer submits a rating outside the 1–5 range? System rejects with 400 Bad Request.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST allow authenticated customers to submit a 1–5 star rating and optional feedback comment for their own `COMPLETED` booking.
- **FR-002**: System MUST link reviews to the booking and relevant worker/service information.
- **FR-003**: System MUST prevent customers from reviewing bookings belonging to other customers or reviewing non-`COMPLETED` bookings.
- **FR-004**: System MUST prevent duplicate reviews for the same booking.
- **FR-005**: System MUST provide worker and service rating aggregation and retrieval APIs.
- **FR-006**: System MUST allow authenticated customers to create and retrieve support tickets/complaints related to their own bookings.
- **FR-007**: System MUST derive customer identity strictly from the authenticated security context/JWT, never trusting client-supplied customer IDs.
- **FR-008**: System MUST enforce booking lifecycle status rules (`COMPLETED` for reviews; existing statuses `PENDING`, `CONFIRMED`, `CANCELLED` disallowed for reviews).

### Key Entities

- **Review**: Represents a customer's rating (1–5) and feedback comment for a completed booking, linked to customer ID, worker ID, and booking ID.
- **SupportTicket**: Represents a customer complaint or support request linked to a customer ID and booking ID, with status and message details.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of reviews submitted for valid `COMPLETED` bookings are successfully persisted and retrievable within 1 second.
- **SC-002**: 100% of unauthorized review or support ticket requests (unauthenticated, wrong ownership, invalid booking state) are rejected with appropriate security error codes (401/403).
- **SC-003**: Worker rating aggregation accurately computes average ratings from all associated reviews without performance degradation.

## Assumptions

- Uses the existing `review-service` PostgreSQL database schema (`V1__init_review_support_schema.sql`).
- Authentication and security contexts are provided via shared security patterns and JWT integration in `common-lib`.
- Booking state and ownership verification integrate with existing booking service events or models as established in Feature 006.
- Support ticketing is restricted to complaint management and ticketing only (no live chat).
