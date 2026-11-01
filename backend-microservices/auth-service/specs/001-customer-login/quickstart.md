# Quickstart Validation Guide: Customer Login (`auth-service`)

## Prerequisites
- Java 19 and Gradle installed.
- PostgreSQL database (`el_db`) running (configured on port 5433).

## Setup & Execution
1. Start infrastructure and auth service (Port 8081):
   ```powershell
   cd backend-microservices
   ./gradlew :auth-service:bootRun --jvm-args="-Xms128m -Xmx256m"
   ```

2. Test Login API endpoint (`POST /api/v1/auth/login`):
   ```bash
   curl -X POST http://localhost:8081/api/v1/auth/login \
     -H "Content-Type: application/json" \
     -d '{"mobileNumber": "+1234567890", "password": "SecurePassword123"}'
   ```

3. Expected Outcome:
   - **Success**: HTTP 200 OK with `{ "token": "...", "user": { ... } }`.
   - **Invalid Credentials**: HTTP 401 Unauthorized.
   - **Suspended/Blocked Account**: HTTP 401 Unauthorized (without status exposure).
   - **Service Unavailable**: HTTP 503 if database is down.
