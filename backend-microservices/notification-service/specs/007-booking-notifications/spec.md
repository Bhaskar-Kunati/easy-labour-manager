# Feature Specification: Booking Notifications & Event-Driven Alerts in Notification Service

**Feature Branch**: `007-booking-notifications`

**Created**: 2026-10-04

**Status**: Draft

**Input**: User description: "Feature 007: Booking Notifications & Event-Driven Alerts. Build MVP notification capability in notification-service using existing architecture. Business goal: Customers automatically receive in-app notifications when their service booking is created or cancelled, and securely view and manage their notification history. Scope: US1 (Booking Created Notification), US2 (Booking Cancelled Notification), US3 (View Notification History via GET /api/v1/notifications ordered newest first, using authenticated security context exclusively with no caller-supplied userId parameter), US4 (Mark Notification as Read via PUT /api/v1/notifications/{id}/read returning HTTP 204 No Content with ownership checks). Kafka event idempotency using bookingId + status (BOOKING_CREATED and BOOKING_CANCELLED remain distinct and each generate one notification). Out of scope: SMS, Push, Email, Twilio/AWS SNS/FCM/SendGrid, notification preferences, payment notifications, worker assignment notifications, reviews/tracking notifications."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Booking Created Notification (Priority: P1)

When `notification-service` receives a successful `BOOKING_CREATED` event from the Kafka `booking-events` topic, the system automatically creates an in-app notification for the affected customer containing booking confirmation details.

**Why this priority**: Immediate confirmation when a booking is created reassures the customer that their request was received.

**Independent Test**: Can be fully tested by publishing a `BOOKING_CREATED` event to Kafka and verifying that an unread in-app notification is persisted in PostgreSQL for the corresponding customer ID.

**Acceptance Scenarios**:

1. **Given** a valid `BOOKING_CREATED` event on the `booking-events` topic, **When** `notification-service` consumes the event, **Then** an in-app notification with unread state (`isRead = false`) is created for the customer.
2. **Given** a duplicate delivery of the same `BOOKING_CREATED` event (Kafka redelivery sharing the same booking ID and status `BOOKING_CREATED`), **When** consumed again, **Then** the system prevents duplicate notification creation via idempotency deduplication (checking existing notifications by reference ID / booking ID and status/type).
3. **Given** subsequent `BOOKING_CANCELLED` and `BOOKING_CREATED` events for the same booking ID, **When** consumed, **Then** both events remain distinct and each generate their respective notification.

---

### User Story 2 - Booking Cancelled Notification (Priority: P1)

When `notification-service` receives a successful `BOOKING_CANCELLED` event from the Kafka `booking-events` topic, the system automatically creates an in-app notification for the affected customer informing them of the booking cancellation.

**Why this priority**: Timely notification of cancellation ensures customers are fully aware of changes to their service schedule.

**Independent Test**: Can be fully tested by publishing a `BOOKING_CANCELLED` event to Kafka and verifying that an unread in-app notification is persisted in PostgreSQL for the customer.

**Acceptance Scenarios**:

1. **Given** a valid `BOOKING_CANCELLED` event on the `booking-events` topic, **When** `notification-service` consumes the event, **Then** an in-app notification with unread state (`isRead = false`) is created for the customer.
2. **Given** a duplicate delivery of the same `BOOKING_CANCELLED` event, **When** consumed again, **Then** the system prevents duplicate notification creation.

---

### User Story 3 - View Notification History (Priority: P1)

Authenticated customers can securely retrieve their own notification history via `GET /api/v1/notifications`, with customer identity extracted exclusively from the authenticated security context (no caller-supplied `userId` query parameter or trust). Notifications are returned ordered newest first.

**Why this priority**: Customers need visibility into past and current notifications within the application.

**Independent Test**: Can be fully tested by sending an authenticated `GET` request to `/api/v1/notifications` and verifying that only notifications belonging to the authenticated customer are returned, ordered newest first.

**Acceptance Scenarios**:

1. **Given** an authenticated customer with existing notifications, **When** a `GET` request is sent to `/api/v1/notifications` without parameters, **Then** the system returns an HTTP 200 OK with notifications belonging exclusively to that customer, ordered newest first.
2. **Given** an authenticated customer with zero notifications, **When** `GET /api/v1/notifications` is requested, **Then** the system returns an HTTP 200 OK with an empty list `[]`.
3. **Given** an unauthenticated request (missing or invalid JWT), **When** access is attempted, **Then** the system rejects the request with an HTTP 401 Unauthorized response.

---

### User Story 4 - Mark Notification as Read (Priority: P2)

Authenticated customers can mark one of their own notifications as read via `PUT /api/v1/notifications/{id}/read`, returning HTTP 204 No Content upon success, with strict verification of notification ownership.

**Why this priority**: Allowing users to mark notifications as read helps them manage unread alerts.

**Independent Test**: Can be fully tested by sending an authenticated `PUT` request to `/api/v1/notifications/{id}/read` and verifying that the notification status updates to read (`isRead = true`) with an HTTP 204 No Content response.

**Acceptance Scenarios**:

1. **Given** an authenticated customer and an unread notification owned by them, **When** a `PUT` request is sent to `/api/v1/notifications/{id}/read`, **Then** the system marks the notification as read (`isRead = true`) and returns HTTP 204 No Content.
2. **Given** a request to mark a notification owned by another customer, **When** attempted, **Then** the system rejects the request with an HTTP 403 Forbidden or HTTP 404 Not Found response.
3. **Given** a request with a non-existent notification ID, **When** attempted, **Then** the system returns an HTTP 404 Not Found response.
4. **Given** marking an already read notification as read, **When** requested, **Then** the operation succeeds idempotently, returning HTTP 204 No Content without error.

---

### Edge Cases

- What happens if Kafka redelivers the same event message? (Idempotency deduplication checks matching reference ID and status/type prevent duplicate notification rows).
- What happens if a caller attempts to pass a `userId` query parameter to `GET /api/v1/notifications`? (The parameter is ignored or unsupported, and identity is derived exclusively from the security context).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST consume `BOOKING_CREATED` and `BOOKING_CANCELLED` events from the existing `booking-events` Kafka topic using the existing `BookingEvent` schema.
- **FR-002**: System MUST automatically generate an in-app notification when a valid booking event is consumed.
- **FR-003**: System MUST provide a `GET /api/v1/notifications` endpoint allowing authenticated customers to retrieve their own notifications ordered newest first, deriving customer identity strictly from the security context (disallowing caller-supplied `userId`).
- **FR-004**: System MUST provide a `PUT /api/v1/notifications/{id}/read` endpoint allowing authenticated customers to mark their notifications as read, returning HTTP 204 No Content on success and validating notification ownership.
- **FR-005**: System MUST initialize newly created notifications with an unread state (`isRead = false`).
- **FR-006**: System MUST enforce idempotency on Kafka event consumption using booking reference ID and status/type (`BOOKING_CREATED` vs `BOOKING_CANCELLED`) to prevent duplicate notifications for the same event while allowing distinct lifecycle events to generate separate notifications.
- **FR-007**: System MUST reuse the existing PostgreSQL `notifications` table and entity model without introducing MongoDB or new database schemas.

### Security & Non-Functional Requirements

- **NFR-001**: Notification endpoints MUST be secured via valid JWT access tokens routed through the API Gateway.
- **NFR-002**: Strict authorization checks MUST prevent cross-customer notification access or modification.

### Success Criteria

- **SC-001**: 100% of valid Kafka booking events successfully generate in-app notifications.
- **SC-002**: 100% of unauthorized or cross-customer notification requests are rejected with HTTP 401, 403, or 404.
- **SC-003**: Zero duplicate notifications created on Kafka message redelivery.

### Key Entities

- **Notification**: Entity containing `id`, `userId` (customer mobile/ID), `title`, `message`, `notificationType`, `referenceType` (e.g. `BOOKING`), `referenceId` (booking ID), `isRead` (boolean), and `createdAt`.

### Explicit Out-of-Scope Items

- SMS, Push notifications, and Email.
- External notification providers (Twilio, AWS SNS, FCM, APNs, SendGrid).
- Notification preference management.
- Payment, worker assignment, review, or tracking notifications.
