# Quickstart: Push Notification & In-App Messaging Center (Feature 017)

## Prerequisites
- Docker & Docker Compose running (Kafka, MongoDB, PostgreSQL).
- Java 19 and Gradle installed.

## Setup & Running
1. Start infrastructure:
   ```powershell
   cd devops/docker
   docker-compose up -d
   ```
2. Run `notification-service`:
   ```powershell
   cd backend-microservices
   ./gradlew :notification-service:bootRun --jvm-args="-Xms128m -Xmx256m"
   ```

## Validation Scenarios
1. **Device Token Registration**: Send `POST /api/v1/notifications/tokens` with Bearer JWT and token payload.
2. **Kafka Event Push**: Produce a test booking event to Kafka topic and verify MongoDB notification insertion and mock FCM dispatch log output.
3. **Notification Retrieval**: Call `GET /api/v1/notifications` with user JWT to list stored notifications.
