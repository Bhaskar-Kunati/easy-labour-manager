# Quickstart Validation Guide: Customer Registration (`auth-service`)

## Prerequisites
- Java 19 and Gradle installed.
- PostgreSQL database (`el_db`) running (configured on port 5433).

## Setup & Execution
1. Start infrastructure and auth service (Port 8081):
   ```powershell
   cd backend-microservices
   ./gradlew :auth-service:bootRun --jvm-args="-Xms128m -Xmx256m"
   ```

2. Test Registration API endpoint (`POST /api/v1/auth/register`):
   ```bash
   curl -X POST http://localhost:8081/api/v1/auth/register \
     -H "Content-Type: application/json" \
     -d '{"mobileNumber": "+1234567890", "password": "SecurePassword123"}'
   ```

3. Expected Outcome:
   - **Success**: HTTP 200 OK with success message (password/hash never exposed).
   - **Invalid Input / Mobile Format**: HTTP 400 Bad Request.
   - **Duplicate Mobile Number**: HTTP 409 Conflict.
   - **Database Outage**: HTTP 503 Service Unavailable.
