# Quickstart Validation Guide: Booking Notifications & Event-Driven Alerts

**Feature Branch**: `007-booking-notifications`

## Prerequisites
- PostgreSQL and Kafka running.
- `booking-service` and `notification-service` running.
- Valid JWT token for an authenticated customer.

## Validation Scenarios

### 1. Booking Created Event Consumption & Notification Generation
- **Action**: Publish a `BOOKING_CREATED` event to Kafka topic `booking-events` for customer ID matching test JWT.
- **Expected Outcome**: `notification-service` consumes event and persists an unread in-app notification in PostgreSQL.

### 2. View Notifications (`GET /api/v1/notifications`)
- **Request**: `GET /api/v1/notifications` with `Authorization: Bearer <JWT_TOKEN>`.
- **Expected Outcome**: HTTP 200 OK returning notifications ordered newest first.

### 3. Mark Notification as Read (`PUT /api/v1/notifications/{id}/read`)
- **Request**: `PUT /api/v1/notifications/1/read` with `Authorization: Bearer <JWT_TOKEN>`.
- **Expected Outcome**: HTTP 204 No Content, updating `isRead` to `true`.
