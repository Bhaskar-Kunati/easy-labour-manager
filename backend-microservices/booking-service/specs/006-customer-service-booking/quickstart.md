# Quickstart Validation Guide: Customer Service Booking and Scheduling

**Feature Branch**: `006-customer-service-booking`

## Prerequisites
- PostgreSQL and Kafka running.
- `catalog-service` and `booking-service` running.
- Valid JWT token for an authenticated customer.

## Validation Scenarios

### 1. Create Booking (`POST /api/v1/bookings`)
- **Request**:
  ```http
  POST /api/v1/bookings
  Authorization: Bearer <JWT_TOKEN>
  X-Idempotency-Key: test-key-001
  Content-Type: application/json

  {
    "serviceId": 1,
    "serviceDateTime": "2026-12-31T10:00:00",
    "location": "123 Main Street, City"
  }
  ```
- **Expected Outcome**: HTTP 201 Created with booking details (status `PENDING`, pricing snapshot captured, `BOOKING_CREATED` event published).

### 2. Idempotency Retry (`POST /api/v1/bookings` with same key)
- **Request**: Same request as above with `X-Idempotency-Key: test-key-001`.
- **Expected Outcome**: HTTP 200 OK returning the existing booking without creating a duplicate.

### 3. List Bookings (`GET /api/v1/bookings`)
- **Request**: `GET /api/v1/bookings` with `Authorization: Bearer <JWT_TOKEN>`.
- **Expected Outcome**: HTTP 200 OK with list of bookings owned by the customer.

### 4. Cancel Booking (`PUT /api/v1/bookings/{id}/cancel`)
- **Request**: `PUT /api/v1/bookings/1/cancel` with `Authorization: Bearer <JWT_TOKEN>`.
- **Expected Outcome**: HTTP 200 OK with status updated to `CANCELLED` and `BOOKING_CANCELLED` event published.
