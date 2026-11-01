# Quickstart Validation Guide: Mobile OTP Verification (`auth-service`)

## Prerequisites
- Java 19 and Gradle installed.
- PostgreSQL database (`el_db`) running on port 5433.

## Setup & Execution
1. Start auth-service (Port 8081):
   ```powershell
   cd backend-microservices
   ./gradlew :auth-service:bootRun --jvm-args="-Xms128m -Xmx256m"
   ```

2. Test Send OTP (`POST /api/v1/auth/send-otp`):
   ```bash
   curl -X POST http://localhost:8081/api/v1/auth/send-otp \
     -H "Content-Type: application/json" \
     -d '{"mobileNumber": "+1234567890"}'
   ```

3. Test Verify OTP (`POST /api/v1/auth/verify-otp`):
   ```bash
   curl -X POST http://localhost:8081/api/v1/auth/verify-otp \
     -H "Content-Type: application/json" \
     -d '{"mobileNumber": "+1234567890", "otp": "123456"}'
   ```

4. Expected Outcome:
   - **Send OTP Success**: HTTP 200 OK.
   - **Verify OTP Success**: HTTP 200 OK, updating `User.isVerified` to `true`.
   - **Invalid/Expired OTP**: HTTP 400 Bad Request.
   - **Unknown Mobile**: HTTP 400 Bad Request (preventing user-enumeration).
