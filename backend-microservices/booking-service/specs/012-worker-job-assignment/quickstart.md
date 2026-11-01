# Quickstart Validation: Worker Booking / Job Assignment & Management

**Feature**: `012-worker-job-assignment`

## Prerequisites
- PostgreSQL, MongoDB, and Kafka running via Docker Compose (`devops/docker`).
- Auth Service, User Service, and Booking Service started.

## Validation Scenarios

### 1. Worker Job Visibility
- Authenticate as a worker (`UserType.LABOUR`).
- Call `GET /api/v1/workers/bookings` and verify assigned bookings are returned.
- Call `GET /api/v1/workers/bookings/{bookingId}` and verify booking details.
- Attempt to access a booking assigned to another worker and verify HTTP 403 Forbidden.

### 2. Admin Worker Assignment
- Authenticate as an Admin.
- Call `POST /api/v1/admin/bookings/{id}/assign-worker?workerId=...`.
- Verify successful assignment for verified, available, skilled workers (HTTP 200).
- Verify rejection (HTTP 400) for unverified, offline, or unskilled workers.

### 3. Worker Job Status Update
- Authenticate as the assigned worker.
- Call `PUT /api/v1/workers/bookings/{bookingId}/status` with valid status.
- Verify status is updated successfully.
