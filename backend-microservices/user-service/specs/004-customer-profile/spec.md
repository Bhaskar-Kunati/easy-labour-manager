# Feature Specification: Customer Profile Management in User Service

**Feature Branch**: `004-customer-profile`

**Created**: 2026-10-04

**Status**: Draft

**Input**: User description: "Implement customer profile management in user-service. Allow an authenticated customer to view and manage their own customer profile (`GET /api/v1/customers/me` and `PUT /api/v1/customers/me`). Follow existing Easy Labour project architecture, coding conventions, security model, and auth/user-service boundaries. Reuse existing models (`CustomerProfile`, `User`), repositories (`CustomerProfileRepository`, `JpaUserRepository`), services (`CustomerService`), and controllers (`CustomerController`). Do not introduce duplicate functionality or unnecessary database structures."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - View Customer Profile (Priority: P1)

Authenticated customers can retrieve their own profile information (including full name, profile image, email, gender, and associated account details) by accessing the `/api/v1/customers/me` endpoint using a valid JWT access token.

**Why this priority**: Viewing profile information is a foundational requirement for customers to review their account details and status within the application.

**Independent Test**: Can be fully tested by authenticating as a customer with a valid JWT token, sending a `GET` request to `/api/v1/customers/me`, and verifying that the correct customer profile is returned with an HTTP 200 OK status.

**Acceptance Scenarios**:

1. **Given** an authenticated customer with an existing profile, **When** the customer sends a `GET` request to `/api/v1/customers/me`, **Then** the system returns an HTTP 200 OK response containing the customer profile details (`fullName`, `email`, `gender`, `profileImage`).
2. **Given** an unauthenticated request (missing or invalid JWT token), **When** access to `/api/v1/customers/me` is attempted, **Then** the system rejects the request with an HTTP 401 Unauthorized response.
3. **Given** an authenticated customer whose profile record does not exist in the database, **When** `GET /api/v1/customers/me` is invoked, **Then** the system returns an appropriate error response (e.g., HTTP 404 Not Found).

---

### User Story 2 - Update Customer Profile (Priority: P1)

Authenticated customers can update their profile information (such as full name, email, gender, and profile image) by submitting a `PUT` request to `/api/v1/customers/me` with updated profile payloads.

**Why this priority**: Allowing customers to keep their personal details up to date ensures accurate communication and service delivery.

**Independent Test**: Can be fully tested by authenticating as a customer, sending a `PUT` request to `/api/v1/customers/me` with modified profile fields, and verifying that the database record is updated and returned with HTTP 200 OK.

**Acceptance Scenarios**:

1. **Given** an authenticated customer and valid profile update payload (`fullName`, `email`, `gender`, `profileImage`), **When** a `PUT` request is sent to `/api/v1/customers/me`, **Then** the system updates the customer profile, updates the timestamp, and returns the updated profile with an HTTP 200 OK response.
2. **Given** an unauthenticated request, **When** a profile update is attempted, **Then** the system rejects the request with an HTTP 401 Unauthorized response.
3. **Given** a profile update request with invalid or malformed data, **When** submitted, **Then** the system rejects the request with an HTTP 400 Bad Request validation error.

---

### Edge Cases

- What happens if concurrent profile updates are submitted for the same customer? (Database transaction locks and update timestamps ensure data consistency).
- What happens if optional fields (like `profileImage` or `gender`) are omitted during an update? (System preserves or safely handles nullable attributes according to the domain model).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST provide a `GET /api/v1/customers/me` endpoint in `user-service` to retrieve the profile of the currently authenticated customer.
- **FR-002**: System MUST provide a `PUT /api/v1/customers/me` endpoint in `user-service` to update the profile details (`fullName`, `email`, `gender`, `profileImage`) of the currently authenticated customer.
- **FR-003**: System MUST resolve the authenticated customer by extracting the user identity/mobile number from the Spring Security context (`SecurityContextHolder`).
- **FR-004**: System MUST reject any unauthenticated profile access or update requests with an HTTP 401 Unauthorized response.
- **FR-005**: System MUST reuse existing domain models (`CustomerProfile`, `User`), repositories (`CustomerProfileRepository`, `JpaUserRepository`), and services (`CustomerService`) without introducing redundant database tables or conflicting entities.

### Security & Non-Functional Requirements

- **NFR-001**: Profile endpoints MUST be secured and accessible only via valid cryptographically signed JWT tokens.
- **NFR-002**: Customers MUST only be able to view and modify their own profile data (authorization boundary enforcement).

### Success Criteria

- **SC-001**: 100% of valid authenticated profile retrieval requests successfully return the correct customer profile within established performance expectations.
- **SC-002**: 100% of valid authenticated profile update requests successfully modify and persist customer profile data.
- **SC-003**: 100% of unauthenticated or unauthorized profile requests are correctly rejected with HTTP 401 Unauthorized.

### Key Entities

- **CustomerProfile**: Entity containing `id`, `user` (OneToOne relationship), `fullName`, `profileImage`, `email`, `gender`, `createdAt`, and `updatedAt`.
- **User**: Base user entity containing credentials, mobile number, role (`CUSTOMER`), and status.

### Explicit Out-of-Scope Items

- Worker profile management (handled by separate worker controllers/services).
- Admin management of customer profiles.
