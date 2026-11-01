# Feature Specification: Customer Registration in Auth Service

**Feature Branch**: `002-customer-registration`

**Created**: 2026-10-03

**Status**: Draft

**Input**: User description: "Implement customer registration in auth-service. A new customer should be able to register using mobile number and password. The service must securely hash the password, create the customer with the appropriate CUSTOMER role and initial account status, reject duplicate mobile numbers, validate required input, and return appropriate validation/conflict/server-error responses. Reuse the existing User entity, repository, password encoder, security configuration, and existing auth-service architecture. Do not redesign existing authentication or JWT functionality. Define the API contract and automated acceptance criteria."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - New Customer Registration (Priority: P1)

New customers can register for an account by submitting a valid mobile number and secure password, resulting in the creation of a profile with the `CUSTOMER` role, initial account status (`ACTIVE`), and securely hashed password storage.

**Why this priority**: Customer registration is the primary entry point for acquiring new users on the platform before they can perform bookings or manage services.

**Independent Test**: Can be fully tested by submitting a new unique mobile number and password to the registration endpoint and verifying that the user is successfully created in the database with a hashed password and `CUSTOMER` role.

**Acceptance Scenarios**:

1. **Given** a new unique mobile number and secure password, **When** the customer submits a registration request, **Then** the system creates the customer profile with the `CUSTOMER` role, an initial account status of `ACTIVE`, a securely hashed password using the application's password-encoding mechanism, and returns a success response without exposing password or password hash data.
2. **Given** a registration request with a mobile number that is already registered in the system, **When** registration is attempted, **Then** the system rejects the request with an HTTP 409 Conflict response indicating that the mobile number already exists.
3. **Given** a registration request containing a syntactically invalid mobile number format or missing required fields, **When** registration is submitted, **Then** the system returns an HTTP 400 Bad Request validation response.
4. **Given** an unexpected application or service failure during registration, **When** registration is attempted, **Then** the system returns an HTTP 500 Internal Server Error response without exposing internal infrastructure details.
5. **Given** that the database or dependent infrastructure is unavailable during registration, **When** registration is attempted, **Then** the system returns an HTTP 503 Service Unavailable response.

---

### Edge Cases

- What happens if credentials contain leading/trailing whitespace? (System sanitizes inputs according to standard formatting before validation).
- What happens if concurrent registration requests are made with the same mobile number? (Database unique constraints ensure only one registration succeeds while the duplicate is rejected with an HTTP 409 Conflict response).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST accept customer registration requests containing a mobile number and password.
- **FR-002**: System MUST enforce syntactic validation of the mobile number according to the application's supported mobile number format.
- **FR-003**: System MUST check for existing registrations and reject duplicate mobile numbers with an HTTP 409 Conflict response.
- **FR-004**: System MUST securely hash the password using the application's existing password-encoding mechanism prior to persistence, ensuring plaintext passwords are never stored or logged, and that neither plaintext passwords nor password hashes are ever returned in the registration response.
- **FR-005**: System MUST assign the `CUSTOMER` role and an initial account status of `ACTIVE` upon successful registration.
- **FR-006**: System MUST return deterministic status codes: HTTP 400 for missing or malformed registration input, HTTP 409 for duplicate mobile numbers, HTTP 500 for unexpected application or service failure, and HTTP 503 when the database or dependent infrastructure is unavailable.

### Success Criteria

- **SC-001**: 100% of valid new customer registrations successfully create an account with a hashed password and `CUSTOMER` role within established performance expectations.
- **SC-002**: 100% of duplicate mobile number registrations are correctly rejected with an HTTP 409 Conflict response.
- **SC-003**: Zero plaintext passwords are ever stored or exposed in logs or responses, and password hashes are never returned in responses.

### Key Entities

- **Customer / User**: Represents the registered account containing mobile number, securely hashed password, initial account status (`ACTIVE`), and assigned role (`CUSTOMER`).
