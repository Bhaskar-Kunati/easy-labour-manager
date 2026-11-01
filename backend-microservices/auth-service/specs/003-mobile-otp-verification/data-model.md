# Data Model: Mobile OTP Verification (`auth-service`)

## Entities

### User Entity (`com.easylabour.authservice.domain.model.User`)
- **Table**: `users` (PostgreSQL `el_db`)
- **Fields**:
  - `id`: `String` (UUID)
  - `mobileNumber`: `String` (Unique, Not Null)
  - `passwordHash`: `String` (Not Null)
  - `role`: `UserRole` Enum (`CUSTOMER`)
  - `status`: `UserStatus` Enum (`ACTIVE`)
  - `isVerified`: `boolean` (Updated to `true` upon successful OTP verification)
  - `createdAt`: `LocalDateTime`
  - `updatedAt`: `LocalDateTime`

### Transient OTP Session Record (In-Memory / Service State)
- `mobileNumber`: `String` (Key)
- `otp`: `String` (Hashed or temporary secure string)
- `expiresAt`: `Instant` (Generation time + 5 minutes)
- `attempts`: `int` (Brute-force / failure attempt counter)

### Send OTP Request DTO
- `mobileNumber`: `String` (Required)

### Verify OTP Request DTO
- `mobileNumber`: `String` (Required)
- `otp`: `String` (Required)
