# Feature Specification: Production-Grade Customer Mobile OTP Verification in Auth Service

**Feature Branch**: `003-mobile-otp-verification`

**Created**: 2026-10-03

**Status**: Draft

**Input**: User description: "Implement production-grade customer mobile OTP verification in auth-service. Context: * `001-customer-login` is complete. * `002-customer-registration` is complete. * auth-service already contains mock OTP endpoints: * `POST /api/v1/auth/send-otp` * `POST /api/v1/auth/verify-otp` * Existing `OtpProvider` and `MockOtpProvider` must be inspected and reused where appropriate. * `User` already contains an `isVerified` field and the database schema already supports it. * Existing `AuthController`, `UserRepository`, `User`, security infrastructure, and exception handling must be reused. * Do not create duplicate authentication infrastructure."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Send Mobile OTP (Priority: P1)

Registered customers can request a One-Time Password (OTP) to be dispatched to their mobile number. The system generates a secure OTP, manages its lifecycle (including tracking a defined 5-minute expiration duration), and dispatches it via the `OtpProvider` abstraction without exposing the plaintext OTP in API responses or logs.

**Why this priority**: Mobile OTP generation and dispatch is the first required step to establish verifiable communication channels with the customer.

**Independent Test**: Can be fully tested by submitting a registered mobile number to `/api/v1/auth/send-otp` and verifying that an OTP is successfully generated, dispatched via `OtpProvider`, and that the HTTP 200 response contains a success message without exposing the OTP value.

**Acceptance Scenarios**:

1. **Given** a valid registered mobile number, **When** the customer requests an OTP via `POST /api/v1/auth/send-otp`, **Then** the system generates an OTP, dispatches it through the `OtpProvider`, and returns an HTTP 200 success response without disclosing the OTP value.
2. **Given** a mobile number that does not exist in the system or is unknown, **When** an OTP request is submitted, **Then** the system returns a generic HTTP 400 Bad Request response to prevent user-enumeration.
3. **Given** a request with a malformed or missing mobile number, **When** `send-otp` is submitted, **Then** the system returns an HTTP 400 Bad Request validation response.
4. **Given** that the OTP provider or infrastructure is unavailable, **When** `send-otp` is invoked, **Then** the system handles the failure gracefully and returns an HTTP 503 Service Unavailable response.

---

### User Story 2 - Verify Mobile OTP & Update Verification State (Priority: P1)

Customers can verify the received OTP by submitting their mobile number and the OTP code. Upon successful verification, the system updates the user's `isVerified` state to `true`. If the OTP is invalid, expired, or submitted against an unknown mobile number, verification fails with an HTTP 400 Bad Request response with safeguards against brute-force attempts.

**Why this priority**: Completing OTP verification proves ownership of the mobile number and marks the account as verified (`isVerified = true`).

**Independent Test**: Can be fully tested by submitting a valid OTP for a mobile number to `/api/v1/auth/verify-otp` and verifying that the response is HTTP 200 and the user's `isVerified` flag in the database becomes `true`.

**Acceptance Scenarios**:

1. **Given** a valid mobile number and a correct, unexpired OTP, **When** the customer submits verification via `POST /api/v1/auth/verify-otp`, **Then** the system validates the code, updates the user's `isVerified` field to `true`, invalidates/clears the OTP, and returns an HTTP 200 success response.
2. **Given** an incorrect or invalid OTP code, **When** verification is submitted, **Then** the system rejects the request with an HTTP 400 Bad Request response.
3. **Given** an expired OTP code (exceeding the defined expiration duration), **When** verification is submitted, **Then** the system rejects the request with an HTTP 400 Bad Request response indicating invalid/expired status without exposing sensitive OTP information.
4. **Given** a mobile number that does not exist or is unknown during verification, **When** verification is attempted, **Then** the system rejects the request with an HTTP 400 Bad Request response to prevent user-enumeration.
5. **Given** excessive invalid verification attempts (rate limiting / brute-force prevention), **When** further verification is attempted, **Then** the system blocks or temporarily limits attempts.

---

### Edge Cases

- What happens if an OTP is verified twice? (The second verification attempt fails because the OTP is single-use and invalidated upon successful verification).
- What happens if a new OTP is requested while a previous OTP is still valid? (The system generates a new OTP, overwriting or superseding the previous active OTP).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST provide a `POST /api/v1/auth/send-otp` endpoint accepting a mobile number to generate and dispatch an OTP via the `OtpProvider` abstraction.
- **FR-002**: System MUST provide a `POST /api/v1/auth/verify-otp` endpoint accepting a mobile number and OTP code to verify authenticity.
- **FR-003**: System MUST update the existing user's `isVerified` field to `true` upon successful OTP verification.
- **FR-004**: System MUST NOT store plaintext OTPs directly in the `User` entity or expose OTP values in production API responses or application logs.
- **FR-005**: System MUST enforce an explicit OTP expiration duration of 5 minutes from generation, rejecting expired OTP codes.
- **FR-006**: System MUST return deterministic status codes: HTTP 400 for invalid/malformed input, invalid OTP, expired OTP, unknown mobile numbers (preventing user-enumeration), and HTTP 503 when the OTP provider or dependent infrastructure is unavailable.
- **FR-007**: System MUST reuse existing `AuthController`, `UserRepository`, `User`, `OtpProvider`, `MockOtpProvider`, and exception handling architecture without creating duplicate authentication infrastructure or new database tables/columns.

### Security Requirements

- **NFR-001**: OTP codes MUST be securely generated and transmitted over secure transport channels.
- **NFR-002**: Plaintext OTP codes MUST never appear in API response bodies or application logs in production environments.
- **NFR-003**: OTP verification attempts MUST be protected against brute-force attacks through rate-limiting or temporary lockout mechanisms.

### Success Criteria

- **SC-001**: 100% of valid OTP verification requests successfully update the target user's `isVerified` status to `true`.
- **SC-002**: 100% of invalid or expired OTP codes are correctly rejected without altering user verification state.
- **SC-003**: Zero sensitive OTP values are exposed in logs or API responses.

### Key Entities

- **User / Customer**: Existing entity containing `mobileNumber`, `passwordHash`, `role`, `status`, and `isVerified`.
- **OTP Session / Provider State**: Transient or managed storage for active OTP codes, timestamps, and attempt counts linked to mobile numbers (via `OtpProvider` or internal service caching).

### Explicit Out-of-Scope Items

- Integrating specific paid external SMS gateways (e.g., Twilio, AWS SNS) beyond the `OtpProvider` abstraction (real external SMS provider remains out of scope).
- Modifying customer login or registration API contracts.
- Adding new database tables or columns outside the existing `User` schema.
