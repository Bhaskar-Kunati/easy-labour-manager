# Data Model: Real-Time Service & Worker Tracking

**Feature**: `009-realtime-worker-tracking`

## Entities

### 1. LocationHistory (MongoDB Document)
Represents a recorded geographic coordinate point associated with a specific booking.

- **Collection**: `location_history` (or `trackingdb.locationHistory`)
- **Fields**:
  - `id`: String (MongoDB ObjectId, primary key)
  - `bookingId`: String (Indexed, required, references booking)
  - `latitude`: Double (Required, range: -90.0 to 90.0)
  - `longitude`: Double (Required, range: -180.0 to 180.0)
  - `timestamp`: LocalDateTime (Required, time of location recording)

### 2. TrackingEvent (DTO / Message Payload)
Payload structure used for location update submissions and WebSocket broadcasts.

- **Fields**:
  - `bookingId`: String (Required)
  - `latitude`: Double (Required, range: -90.0 to 90.0)
  - `longitude`: Double (Required, range: -180.0 to 180.0)
  - `timestamp`: LocalDateTime (Optional/Auto-populated if null)

## Validation Rules
- `bookingId` must not be null or blank.
- `latitude` must be between `-90.0` and `90.0` inclusive.
- `longitude` must be between `-180.0` and `180.0` inclusive.
- `timestamp` defaults to `LocalDateTime.now()` if not provided in the incoming payload.
