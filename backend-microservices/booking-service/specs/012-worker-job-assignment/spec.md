# Feature Specification: Worker Booking / Job Assignment & Management

**Feature Branch**: `012-worker-job-assignment`

**Created**: 2026-10-06

**Status**: Draft

**Input**: User description: "Create Feature 012: Worker Booking / Job Assignment & Management for the Easy Labour application..."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Worker Job Visibility & Access Control (Priority: P1)

As an authenticated registered worker, I want to view bookings assigned to me and inspect their details securely so that I can manage my assigned work without accessing other workers' jobs or exposing customer data to unauthorized parties.

**Why this priority**: Core MVP requirement ensuring workers can view and interact only with their own assigned jobs while strictly maintaining data isolation.

**Independent Test**: Test worker job listing (`GET /api/v1/workers/bookings`) and booking details retrieval (`GET /api/v1/workers/bookings/{bookingId}`) with valid worker JWT, ensuring 403 Forbidden is returned for bookings assigned to other workers or non-worker accounts.

**Acceptance Scenarios**:

1. **Given** an authenticated worker with valid JWT, **When** they request their assigned bookings (`GET /api/v1/workers/bookings`), **Then** the system returns HTTP 200 OK with the list of bookings assigned exclusively to that worker.
2. **Given** an authenticated worker, **When** they request details of a specific booking assigned to them (`GET /api/v1/workers/bookings/{bookingId}`), **Then** the system returns HTTP 200 OK with the booking details.
3. **Given** an authenticated worker, **When** they attempt to access a booking assigned to another worker, **Then** the system denies access with HTTP 403 Forbidden.
4. **Given** a customer or unauthenticated user, **When** they attempt to access worker-only booking endpoints, **Then** the system denies access with HTTP 401 Unauthorized or HTTP 403 Forbidden.

---

### User Story 2 - Worker Job Assignment & Eligibility (Priority: P2)

As an administrator or automated dispatcher, I want to assign eligible workers to customer bookings using server-side authorization and validation so that bookings are assigned only to qualified, verified, and available workers without conflicts or duplicates.

**Why this priority**: Essential for platform operations, ensuring secure, constraint-validated job allocation.

**Independent Test**: Test worker assignment (`POST /api/v1/bookings/{bookingId}/assign`) with admin credentials and valid worker eligibility (approved verification, matching skills, available status, no schedule conflict), and verify rejection (HTTP 400/403) for ineligible, unverified, or conflicting assignments.

**Acceptance Scenarios**:

1. **Given** an authenticated admin user and an unassigned customer booking, **When** an admin assigns an eligible approved worker matching the booking service skill and availability, **Then** the system successfully assigns the worker and returns HTTP 200 OK.
2. **Given** an admin attempting to assign an unverified, offline, or unskilled worker, **When** the assignment request is submitted, **Then** the system rejects the request with HTTP 400 Bad Request.
3. **Given** a worker already assigned or busy during the requested booking slot, **When** an assignment is attempted, **Then** the system prevents conflicting or duplicate assignments with HTTP 400 Bad Request.
4. **Given** a customer or unauthorized user, **When** they attempt to assign a worker to a booking, **Then** the system denies authorization with HTTP 403 Forbidden.

---

### User Story 3 - Worker Job Status Management (Priority: P3)

As an authenticated assigned worker, I want to manage my assigned booking using existing platform booking statuses so that job progress can be updated cleanly without violating Feature 006's booking lifecycle.

**Why this priority**: Enables workers to perform job lifecycle actions (e.g., completing or updating status) using the established booking status model.

**Independent Test**: Test worker job status update (`PUT /api/v1/workers/bookings/{bookingId}/status`) with valid assigned worker JWT and existing booking status transitions, verifying successful updates and rejection of unauthorized/invalid transitions.

**Acceptance Scenarios**:

1. **Given** an authenticated worker assigned to a booking, **When** they update the booking status to an allowed lifecycle state using existing booking statuses (without introducing unmapped custom statuses), **Then** the system updates the status successfully and returns HTTP 200 OK.
2. **Given** a worker attempting to update a booking not assigned to them, **When** the status update request is submitted, **Then** the system rejects the request with HTTP 403 Forbidden.

---

### User Story 4 - Customer Booking Worker Visibility (Priority: P4)

As a customer who created a booking, I want to view assigned worker details (such as full name, rating, profile image, and contact info) for my confirmed booking without exposing unnecessary private worker information.

**Why this priority**: Enhances customer transparency and trust once a worker is successfully assigned.

**Independent Test**: Test customer booking retrieval (`GET /api/v1/customers/bookings/{bookingId}`) with valid customer JWT, verifying assigned worker summary details are included without exposing internal system identifiers or sensitive data.

**Acceptance Scenarios**:

1. **Given** an authenticated customer with an assigned booking, **When** they view their booking details, **Then** the system returns booking information along with assigned worker summary details (full name, rating, profile image).
2. **Given** a customer viewing a booking belonging to another customer, **When** they request booking details, **Then** the system denies access with HTTP 403 Forbidden.

---

### Edge Cases

- What happens when a worker's availability changes to offline while a job assignment is pending? System rejects the assignment with HTTP 400.
- What happens when a booking is cancelled by a customer while a worker is assigned? System prevents further worker status updates and releases worker assignment.
- What happens when a client supplies a custom or tampered `workerId` or `customerId` in request payloads? System ignores client-supplied IDs and extracts identity strictly from the authenticated JWT/SecurityContext.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST provide `GET /api/v1/workers/bookings` allowing authenticated workers to view bookings assigned to them.
- **FR-002**: System MUST provide `GET /api/v1/workers/bookings/{bookingId}` allowing workers to view details of their assigned booking while blocking access to bookings assigned to other workers.
- **FR-003**: System MUST provide `POST /api/v1/bookings/{bookingId}/assign` (or equivalent admin/system endpoint) restricted to authorized admin/system roles to assign workers to bookings.
- **FR-004**: System MUST validate worker eligibility prior to assignment, verifying the worker profile is approved (`WorkerVerificationStatus.APPROVED`), possesses the required skill category matching the booking service, and is available (`WorkerAvailability.AVAILABLE`).
- **FR-005**: System MUST prevent duplicate worker assignments and scheduling conflicts for bookings.
- **FR-006**: System MUST provide `PUT /api/v1/workers/bookings/{bookingId}/status` allowing assigned workers to update job status using existing platform booking statuses without introducing unapproved new lifecycle statuses or violating Feature 006 lifecycle rules.
- **FR-007**: System MUST provide customers visibility into assigned worker summary details (full name, rating, profile image) for their own confirmed bookings via existing customer booking retrieval endpoints.
- **FR-008**: System MUST derive all user and worker identities strictly from the authenticated JWT/SecurityContext (`SecurityContextHolder`), ignoring any client-supplied IDs.
- **FR-009**: System MUST return HTTP 401 Unauthorized for unauthenticated requests and HTTP 403 Forbidden for unauthorized access attempts (e.g. customers accessing worker endpoints, workers accessing other workers' jobs).

### Key Entities

- **Booking**: Represents a customer service request, containing booking status, service reference, customer reference, scheduled date/time, and assigned worker reference/relationship (`booking_workers` or worker field).
- **WorkerProfile**: Represents worker details, verification status, rating, and availability status.
- **WorkerSkill**: Represents skills associated with a worker matching catalog service categories.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Authenticated workers can retrieve their assigned job listings and details in under 1 second.
- **SC-002**: 100% of unauthorized access attempts (cross-worker access, customer access to worker endpoints, unauthenticated calls) are successfully blocked with HTTP 401 or HTTP 403.
- **SC-003**: Worker assignment enforces 100% eligibility validation (skill match, approval status, availability check) preventing invalid or conflicting assignments.
- **SC-004**: Customer booking views correctly display assigned worker summary details without exposing restricted or private information.

## Assumptions

- Reuses existing authentication, JWT filtering, and SecurityContext infrastructure from Auth Service and Common Lib.
- Reuses existing `Booking`, `WorkerProfile`, `WorkerSkill`, `WorkerAvailability`, and `WorkerVerificationStatus` models from Booking Service and User Service.
- Reuses existing Feature 006 booking lifecycle and status definitions without modification.
- Communication between microservices (e.g., booking service verifying worker profile/skills from user service) utilizes existing REST clients or event-driven mechanisms.
