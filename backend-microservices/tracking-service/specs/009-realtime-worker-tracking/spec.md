# Feature Specification: Real-Time Service & Worker Tracking

**Feature Branch**: `009-realtime-worker-tracking`

**Created**: October 5, 2026

**Status**: Draft

**Input**: User description: "Feature 009 — Real-Time Service & Worker Tracking Build on the existing tracking-service architecture discovered in the repository. Feature goal: Enable authenticated customers to view the tracking status and real-time location of the worker/service execution for their booking, while providing a secure mechanism for an authorized worker/system actor to submit location updates. Existing reusable architecture: * tracking-service * MongoDB `LocationHistory` * `LocationRepository` * `TrackingService` * `TrackingController` * Spring WebSocket/STOMP * `/ws-tracking` * `/topic/tracking/{bookingId}` * existing `BookingEventConsumer` * existing booking-events Kafka infrastructure..."

## Clarifications

### Session 2026-10-05

- Q: What authenticated actor is allowed to call `POST /api/tracking/update` given that Feature 006 did not implement worker matching/assignment? → A: Authenticated system service accounts or authorized workers possessing valid security credentials/tokens. Actual worker-to-booking assignment validation is recorded as a future dependency/out-of-scope item for Feature 009.
- Q: How is an authenticated customer subscribing to `/topic/tracking/{bookingId}` verified as the owner of that booking? → A: Customer identity is derived from the authenticated security context (JWT/session) and verified against the booking owner. Client-supplied customer IDs are never trusted.
- Q: Which booking statuses (`PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED`) allow tracking history access, WebSocket subscription, and location updates? → A: `CONFIRMED` status (active execution) allows live WebSocket subscription and location updates. `PENDING`, `CANCELLED`, and `COMPLETED` disallow active tracking updates/subscriptions (though historical logs remain queryable for audit/review where authorized).
- Q: How are existing `booking-events` Kafka topics used by tracking-service to handle lifecycle handling? → A: Tracking-service consumes `booking-events` to track booking status transitions (`CONFIRMED`, `CANCELLED`, `COMPLETED`) and prevent live tracking/location submissions for cancelled or completed bookings, without introducing new topics or duplicate event infrastructure.
- Q: Does MVP need a separate tracking status field, or is booking status + location history sufficient? → A: A separate tracking status model is NOT needed. Booking status combined with MongoDB `LocationHistory` collection is fully sufficient for MVP tracking.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Secure Historical & Real-Time Tracking for Customers (Priority: P1)

Authenticated customers can view historical location logs and subscribe to real-time location updates via WebSocket/STOMP for their active bookings.

**Why this priority**: Delivers core value to the end user (customer) to monitor service execution progress and worker location securely.

**Independent Test**: Can be fully tested by authenticating as a valid booking owner, requesting `GET /api/tracking/history/{bookingId}`, and establishing a STOMP connection to `/ws-tracking` subscribing to `/topic/tracking/{bookingId}`.

**Acceptance Scenarios**:

1. **Given** an authenticated customer who owns a booking in `CONFIRMED` status, **When** they request historical location via `GET /api/tracking/history/{bookingId}`, **Then** the system returns the list of historical location records for that booking.
2. **Given** an authenticated customer who owns a booking in `CONFIRMED` status, **When** they subscribe to `/topic/tracking/{bookingId}` over `/ws-tracking`, **Then** they successfully receive real-time location updates broadcasted for that booking.
3. **Given** an unauthenticated user or a customer attempting to access tracking for another customer's booking or an inactive booking (`PENDING`, `CANCELLED`, `COMPLETED`), **When** they request history or subscribe to the tracking topic, **Then** the system denies access with an appropriate authentication/authorization error.

---

### User Story 2 - Authorized Worker/System Location Submission (Priority: P2)

Authorized workers or system actors can submit real-time location updates for a booking via the REST endpoint `POST /api/tracking/update`, which are validated, persisted to MongoDB, and broadcasted to connected customers.

**Why this priority**: Enables workers or system components to feed location telemetry into the tracking pipeline.

**Independent Test**: Can be tested by sending a valid `POST /api/tracking/update` request with booking ID, latitude, longitude, and timestamp from an authorized actor, verifying persistence in MongoDB `LocationHistory` and broadcast to WebSocket subscribers.

**Acceptance Scenarios**:

1. **Given** an authorized worker or system actor with valid location payload for a `CONFIRMED` booking, **When** they invoke `POST /api/tracking/update`, **Then** the location is stored in MongoDB and broadcasted via STOMP.
2. **Given** a location update request with invalid latitude/longitude ranges or missing booking ID, **When** `POST /api/tracking/update` is called, **Then** the system rejects the request with a validation error.
3. **Given** a location update request for an unknown or ineligible booking (`PENDING`, `CANCELLED`, `COMPLETED`), **When** `POST /api/tracking/update` is called, **Then** the system rejects the request with an appropriate error.

---

### Edge Cases

- What happens when a customer attempts to track a booking that is completed (`COMPLETED`), cancelled (`CANCELLED`), or pending (`PENDING`)? The system rejects active tracking updates and subscriptions based on the booking lifecycle state.
- What happens when latitude/longitude values are out of bounds (e.g., latitude > 90 or longitude > 180)? The system rejects the request with a validation error.
- What happens when a WebSocket subscription is attempted without proper authentication or ownership validation? The connection/subscription is refused.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Customer authentication MUST be required for all tracking REST and WebSocket APIs.
- **FR-002**: A customer MAY access tracking history or real-time updates ONLY for their own booking, derived securely from the authenticated security context (JWT/session) rather than client-supplied parameters.
- **FR-003**: Worker and system location update submissions via `POST /api/tracking/update` MUST be authenticated and authorized using system service accounts or authorized security tokens (pending future worker-assignment module integration).
- **FR-004**: The system MUST support worker/system location submission through the existing REST endpoint `POST /api/tracking/update`.
- **FR-005**: The system MUST support historical tracking retrieval through the existing REST endpoint `GET /api/tracking/history/{bookingId}`.
- **FR-006**: The system MUST support real-time customer updates through the existing STOMP WebSocket infrastructure (`/ws-tracking` and `/topic/tracking/{bookingId}`).
- **FR-007**: Location history MUST be persisted in the existing MongoDB `LocationHistory` collection via `LocationRepository`.
- **FR-008**: Location data MUST include booking ID, latitude, longitude, and timestamp.
- **FR-009**: The system MUST validate basic location inputs, including valid latitude range (-90 to 90), valid longitude range (-180 to 180), and required booking ID.
- **FR-010**: Tracking updates and WebSocket subscriptions MUST be restricted to bookings in `CONFIRMED` status. Bookings in `PENDING`, `CANCELLED`, or `COMPLETED` statuses disallow live tracking updates and subscriptions.
- **FR-011**: The system MUST consume existing `booking-events` Kafka topics in `BookingEventConsumer` to dynamically monitor booking lifecycle state transitions (`CONFIRMED`, `CANCELLED`, `COMPLETED`) without introducing new topics.
- **FR-012**: When a valid location update is accepted and persisted, the system MUST broadcast the update to the booking-specific STOMP destination (`/topic/tracking/{bookingId}`).
- **FR-013**: Only authenticated and authorized booking owners MAY subscribe to a booking's tracking stream, verified via security context.
- **FR-014**: Error responses MUST NOT expose sensitive system information. Unauthenticated requests must return authentication errors, unauthorized booking access must return authorization errors, and invalid data must return validation errors.
- **FR-015**: The MVP implementation MUST NOT introduce a separate tracking database, third-party maps, navigation, fleet management, geofencing, route optimization, distance-threshold notifications, or a separate tracking status model (booking status + `LocationHistory` is sufficient).
- **FR-016**: Worker-assignment validation dependency: Worker-to-booking assignment validation is explicitly documented as a future out-of-scope dependency on a worker-management module.

### Key Entities

- **LocationHistory**: Represents a recorded geographic location point associated with a booking, containing fields for booking ID, latitude, longitude, and timestamp, stored in MongoDB.
- **TrackingEvent**: Represents the payload structure transmitted during location updates and WebSocket broadcasts containing booking ID, latitude, longitude, and timestamp.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Authorized customers successfully retrieve tracking history for their active bookings in under 500ms.
- **SC-002**: Valid worker location updates are processed, persisted to MongoDB, and broadcasted via STOMP WebSocket within 200ms of receipt.
- **SC-003**: 100% of unauthorized or unauthenticated requests to tracking REST endpoints or WebSocket destinations are rejected with appropriate error status codes/messages.
- **SC-004**: Input validation successfully intercepts and rejects 100% of malformed or out-of-range coordinate submissions.

## Assumptions

- Users have stable internet connectivity for WebSocket and REST communications.
- Existing Spring Security infrastructure provides authenticated user context.
- MongoDB `trackingdb` (or default MongoDB instance configured for tracking-service) is available and accessible.
- Booking status validation leverages existing `booking-events` Kafka streams and service interactions from the existing microservices architecture.
- Worker identity and update submission are performed by authenticated service accounts or authorized workers possessing valid security tokens.
