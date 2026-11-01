# Research: Booking Notifications & Event-Driven Alerts

**Feature Branch**: `007-booking-notifications`

## Decision 1: Kafka Event Deduplication & Idempotency Strategy

- **Decision**: To prevent duplicate notifications on Kafka event redelivery while allowing distinct lifecycle events (`BOOKING_CREATED` vs `BOOKING_CANCELLED`) for the same booking to generate separate notifications, we check existing notifications in PostgreSQL by `referenceId` (booking ID) and `notificationType` or message content/status. Alternatively, add a unique constraint or check in `NotificationRepository` (`findByReferenceIdAndNotificationType`) before saving.
- **Rationale**: Reuses existing PostgreSQL `notifications` table and fields (`referenceId`, `notificationType`) without introducing auxiliary idempotency infrastructure or new database tables.
- **Alternatives Considered**: In-memory deduplication cache (rejected because service restarts could cause redeliveries).

## Decision 2: Security Context Identity Resolution

- **Decision**: Remove caller-supplied `userId` query parameters from `GET /api/v1/notifications`. Extract the authenticated customer's identifier strictly from `SecurityContextHolder.getContext().getAuthentication().getName()`. For `PUT /api/v1/notifications/{id}/read`, fetch the notification by ID, verify that `notification.getUserId().equals(securityContextUsername)`, and throw HTTP 403 / 404 if ownership check fails.
- **Rationale**: Aligns with project security conventions (Feature 004 and Feature 006) and prevents unauthorized cross-user data access.
- **Alternatives Considered**: Trusting request parameter `userId` (rejected for security vulnerabilities).

## Decision 3: Notification Ordering & Read Status

- **Decision**: Return customer notifications ordered newest first (`findByUserIdOrderByCreatedAtDesc`). Initialize new notifications with `isRead = false`. Mark-as-read updates `isRead = true` and returns HTTP 204 No Content idempotently.
- **Rationale**: Matches specification requirements and provides a clean user experience.
