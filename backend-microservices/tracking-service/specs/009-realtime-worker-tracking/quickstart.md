# Quickstart Validation Guide: Real-Time Service & Worker Tracking

**Feature**: `009-realtime-worker-tracking`

## Prerequisites
1. Running MongoDB instance (`trackingdb`).
2. Running Kafka broker and Zookeeper (or Docker infrastructure via `devops/docker/docker-compose.yml`).
3. Running `tracking-service` (`./gradlew :tracking-service:bootRun --jvm-args="-Xms128m -Xmx256m"`).

## Validation Scenarios

### 1. Test Location Update Submission (REST)
Send a POST request to `POST /api/tracking/update` with valid coordinates and token:
```bash
curl -X POST http://localhost:8080/api/tracking/update \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <valid_system_or_worker_token>" \
  -d '{
    "bookingId": "booking-123",
    "latitude": 12.9716,
    "longitude": 77.5946
  }'
```
*Expected Outcome*: HTTP 200 OK. Location persisted in MongoDB `LocationHistory` and broadcasted via STOMP.

### 2. Test Historical Location Retrieval (REST)
Send a GET request to `GET /api/tracking/history/booking-123`:
```bash
curl -X GET http://localhost:8080/api/tracking/history/booking-123 \
  -H "Authorization: Bearer <valid_customer_owner_token>"
```
*Expected Outcome*: HTTP 200 OK with JSON array containing location history records.

### 3. Test Real-Time WebSocket STOMP Subscription
Connect via a STOMP client to `ws://localhost:8080/ws-tracking`, authenticate with Bearer token, and subscribe to `/topic/tracking/booking-123`.
*Expected Outcome*: Receiving broadcasted `TrackingEvent` messages whenever `POST /api/tracking/update` is called for `booking-123`.
