# Feature Specification: Push Notification & In-App Messaging Center (Feature 017)

**Feature Branch**: `017-push-notification-messaging`

**Created**: November 17, 2026

**Status**: Draft

**Input**: User description: "Feature 017 – Push Notification & In-App Messaging Center Build a centralized notification platform for EasyTechnician. Scope: 1. Backend: - Integrate Firebase Cloud Messaging (FCM) in notification-service. - Store notification history in MongoDB. - Support device token registration and removal. - Provide APIs to list notifications, mark as read and delete notifications. - Consume Kafka events from booking, payment and tracking services. - Support notification delivery status, retries and failure handling. 2. Android: - Integrate Firebase Messaging SDK. - Handle foreground and background push notifications. - Implement a Jetpack Compose notification inbox. - Support unread counts and notification navigation. 3. Admin Web: - Allow administrators to create and send broadcast notifications. - Support targeted customer and worker announcements. 4. Integration: - Reuse Feature 007 booking notification events and existing Kafka infrastructure. - Integrate with Features 003, 006, 008 and 009. - Avoid duplicating existing notification functionality. - Maintain existing authentication and authorization. 5. Quality: - Follow existing microservice architecture and coding conventions. - Include unit and integration tests. - Support local development without requiring production Firebase credentials."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Automated Event Push & In-App Notification Delivery (Priority: P1)
As a customer or worker, I want to receive real-time push notifications and in-app updates when my booking status changes, payment is processed, or tracking milestone is reached, so that I stay informed throughout the service lifecycle.

**Why this priority**: Core operational value of the platform relies on timely communication of booking states and payments.

**Independent Test**: Trigger a booking event via Kafka and verify that `notification-service` processes it, dispatches an FCM push, and persists the notification in MongoDB for retrieval.

**Acceptance Scenarios**:
1. **Given** a customer has registered an FCM device token, **When** a booking status changes to confirmed or dispatched, **Then** an FCM push notification is sent and an in-app notification record is stored in MongoDB.
2. **Given** a user opens the mobile app, **When** they navigate to the notification inbox, **Then** they see all received notifications with unread indicators and can mark them as read.

---

### User Story 2 - Device Token Management & Preferences (Priority: P2)
As a mobile app user, I want my device FCM token to be securely registered upon login and removed upon logout, ensuring push notifications reach the correct device without leakage.

**Why this priority**: Ensures accurate message delivery and compliance with user session lifecycles.

**Independent Test**: Send a device token registration request with a valid JWT and verify storage; test token removal on logout.

**Acceptance Scenarios**:
1. **Given** an authenticated user with a valid JWT, **When** they register their device FCM token, **Then** the token is associated with their user profile in `notification-service`.
2. **Given** a user logs out, **When** their device token is deregistered, **Then subsequent push notifications are not delivered to that device.**

---

### User Story 3 - Admin Broadcast & Targeted Announcements (Priority: P3)
As an administrator, I want to send broadcast announcements or targeted notifications to specific user segments (customers vs. workers), so that I can communicate promotions or operational updates effectively.

**Why this priority**: Enables platform operators to engage users and manage mass announcements.

**Independent Test**: Use the admin console to publish a broadcast announcement and verify receipt by targeted user groups.

**Acceptance Scenarios**:
1. **Given** an authenticated admin user (`ROLE_ADMIN`), **When** they create and send a broadcast notification, **Then** all active users receive the in-app notification and push alert.
2. **Given** an admin targets customer or worker segments specifically, **When** the announcement is dispatched, **Then** only users matching the target segment receive the notification.

---

### Edge Cases
- What happens when FCM service is unreachable or unresponsive? (System logs error, marks notification delivery status as FAILED, and attempts scheduled retries with exponential backoff).
- What happens during local development without Firebase credentials? (FCM calls are mocked/bypassed gracefully, allowing offline testing).
- How are expired or invalid FCM tokens handled? (Tokens returning FCM NotRegistered error are automatically removed from database).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST integrate Firebase Cloud Messaging (FCM) within `notification-service` for push message dispatch.
- **FR-002**: The system MUST store notification history and device tokens in MongoDB.
- **FR-003**: The system MUST provide secure REST APIs for device token registration, listing notifications, marking notifications as read, and deleting notifications.
- **FR-004**: The system MUST consume Kafka event topics from booking, payment, and tracking services (extending Feature 007).
- **FR-005**: The Android application MUST integrate Firebase Messaging SDK, handle foreground and background push notifications, and feature a Jetpack Compose notification inbox with unread counts and deep-link navigation.
- **FR-006**: The Admin web dashboard MUST allow administrators to create and send broadcast or segmented announcements to customers and workers.
- **FR-007**: The system MUST support local development mode where Firebase credentials are optional and mocked.
- **FR-008**: The system MUST maintain existing JWT authentication and RBAC authorization across all endpoints.

### Reused vs. New Functionality Analysis
- **Reused Functionality**:
  - Existing Kafka messaging infrastructure and Feature 007 booking notification event contracts.
  - Common security filters, JWT validation (`common-lib`), and service client authentication (`X-Internal-Service-Key`).
  - MongoDB database integration patterns from `tracking-service` and `notification-service`.
- **New Functionality**:
  - FCM SDK integration and message dispatch client in `notification-service`.
  - Device token lifecycle management (register, unregister, cleanup invalid tokens).
  - Notification history persistence, unread status tracking, and management APIs (list, mark read, delete).
  - Android FCM background/foreground handlers and Jetpack Compose notification inbox UI.
  - Admin announcement broadcast API and web UI controls.

### Success Criteria

- **Measurable Outcomes**:
  - 99% of valid notification events are dispatched to FCM within 2 seconds of Kafka event consumption.
  - Notification inbox loads list items in under 500ms.
  - 100% of expired or invalid FCM tokens are automatically cleaned up upon delivery failure.
  - Zero disruption to existing Features 001–016 functionality.
