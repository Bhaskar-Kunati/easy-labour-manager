# Data Model: Booking Notifications & Event-Driven Alerts

**Feature Branch**: `007-booking-notifications`

## Entities

### 1. Notification
- **Description**: Represents an in-app notification delivered to a customer.
- **Key Attributes**:
  - `id` (Long): Primary key.
  - `userId` (String): Owner user identifier (customer mobile/ID).
  - `title` (String): Notification title.
  - `message` (String): Notification body message.
  - `notificationType` (String): Type of notification (e.g. `BOOKING_CREATED`, `BOOKING_CANCELLED`).
  - `referenceType` (String): Entity type referenced (e.g. `BOOKING`).
  - `referenceId` (String): ID of the referenced entity (booking ID).
  - `isRead` (Boolean): Read status (`false` by default).
  - `createdAt` (LocalDateTime): Timestamp when notification was created.
