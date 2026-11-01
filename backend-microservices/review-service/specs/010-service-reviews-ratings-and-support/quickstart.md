# Quickstart: Service Reviews, Ratings & Customer Support

**Branch**: `010-service-reviews-ratings-and-support` | **Date**: October 5, 2026

## Overview
This guide provides validation steps for testing the Service Reviews, Ratings & Customer Support feature in `review-service`.

## Prerequisites
- Java 19 installed
- PostgreSQL running (via Docker Compose)
- Gradle wrapper in `backend-microservices`

## Setup & Running
1. Start infrastructure:
   ```powershell
   cd devops/docker
   docker-compose up -d
   ```
2. Run `review-service`:
   ```powershell
   cd backend-microservices
   ./gradlew :review-service:bootRun --jvm-args="-Xms128m -Xmx256m"
   ```

## Validation Scenarios

### 1. Submit Review for Completed Booking
- **Endpoint**: `POST /api/v1/reviews`
- **Headers**: `Authorization: Bearer <jwt_token>`
- **Body**:
  ```json
  {
    "bookingId": "booking-123",
    "workerId": "worker-456",
    "serviceId": "service-789",
    "rating": 5,
    "comment": "Excellent service and prompt work!"
  }
  ```
- **Expected Outcome**: HTTP 200 OK with persisted review object.

### 2. Retrieve Worker Reviews & Ratings
- **Endpoint**: `GET /api/v1/reviews/worker/worker-456`
- **Expected Outcome**: HTTP 200 OK with list of reviews for the worker.

### 3. Create Support Ticket
- **Endpoint**: `POST /api/v1/support`
- **Headers**: `Authorization: Bearer <jwt_token>`
- **Body**:
  ```json
  {
    "bookingId": "booking-123",
    "subject": "Delay in service delivery",
    "message": "The worker arrived 30 minutes late."
  }
  ```
- **Expected Outcome**: HTTP 200 OK with created support ticket.

### 4. Retrieve Customer Support Tickets
- **Endpoint**: `GET /api/v1/support`
- **Headers**: `Authorization: Bearer <jwt_token>`
- **Expected Outcome**: HTTP 200 OK with list of customer's tickets.
