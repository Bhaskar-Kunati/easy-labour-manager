# Feature Specification: Service Payment Processing (Feature 008)

**Feature Branch**: `008-service-payment-processing`

**Created**: 2026-10-05

**Status**: Draft

**Input**: User description: "Feature 008: Service Payment Processing Formalize the existing payment-service into a complete MVP payment flow for customer service bookings..."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Customer Initiates Payment (Priority: P1)

As an authenticated customer, I want to initiate payment for one of my eligible bookings so that I can pay for the booked service.

**Why this priority**: Core transactional prerequisite for completing service bookings.

**Independent Test**: Can be tested by invoking payment initiation with a valid booking ID and JWT token for an owned booking, verifying that a payment record is created and processed via MockPaymentGateway.

**Acceptance Scenarios**:
1. **Given** an authenticated customer who owns an eligible booking, **When** they request payment processing/creation, **Then** the payment is processed and associated with the booking and customer.
2. **Given** an unauthenticated user or customer attempting to pay for another user's booking, **When** they request payment, **Then** the request is rejected with a security/authorization error.

---

### User Story 2 - Successful Payment & Event Notification (Priority: P2)

As a customer, I want a successful payment to be recorded so that my booking reflects the completed payment and triggers downstream updates.

**Why this priority**: Ensures payment confirmation flows correctly to booking-service via Kafka.

**Acceptance Scenarios**:
1. **Given** a payment processed successfully by MockPaymentGateway, **Then** payment status is persisted as successful/paid, a `PAYMENT_COMPLETED` event is published to `payment-events`, and booking-service updates booking payment status to `PAID`.

---

### User Story 3 - Failed Payment & Event Notification (Priority: P3)

As a customer, I want a failed payment to be recorded so that I know the payment did not complete without incorrectly marking the booking as paid.

**Why this priority**: Ensures failure handling and visibility.

**Acceptance Scenarios**:
1. **Given** payment processing fails at the gateway, **Then** payment status is persisted as failed, a `PAYMENT_FAILED` event is published to `payment-events`, and booking-service updates booking payment status to `FAILED`.

---

### User Story 4 - View Payment Status (Priority: P4)

As an authenticated customer, I want to view the payment status for my booking securely.

**Why this priority**: Provides transparency on payment outcomes.

**Acceptance Scenarios**:
1. **Given** an authenticated customer requesting payment status for their booking via `GET /api/v1/payments/status/{bookingId}`, **Then** the current persisted payment status and details are returned.

---

### User Story 5 - Payment Idempotency and Safety (Priority: P5)

Payment processing must be safe against retries and duplicate requests.

**Why this priority**: Prevents double-charging and duplicate successful transactions.

**Acceptance Scenarios**:
1. **Given** a booking that is already paid, **When** a new payment request is submitted, **Then** it is rejected or prevented from creating duplicate successful payment transactions.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST require valid JWT authentication for all customer payment endpoints.
- **FR-002**: System MUST derive customer ID exclusively from the authenticated security context and never trust client-supplied customer IDs.
- **FR-003**: System MUST verify booking existence and customer ownership prior to processing or viewing payment information.
- **FR-004**: System MUST retrieve the payment amount directly from the Feature 006 booking context rather than accepting arbitrary client-supplied amounts.
- **FR-005**: System MUST use `MockPaymentGateway` to simulate payment gateway execution for the MVP.
- **FR-006**: System MUST persist payment records in the existing `payments` table and transactions in `payment_transactions`.
- **FR-007**: System MUST publish `PAYMENT_COMPLETED` and `PAYMENT_FAILED` events to the existing `payment-events` Kafka topic using `PaymentEvent` from `common-lib`.
- **FR-008**: System MUST expose APIs supporting payment creation/processing (`POST /api/v1/payments/create-order` / `POST /api/v1/payments/process`), payment verification (`POST /api/v1/payments/verify`), and payment status retrieval (`GET /api/v1/payments/status/{bookingId}`).
- **FR-009**: System MUST enforce idempotency to prevent duplicate successful payment processing for already-paid bookings.

### Key Entities

- **Payment**: Represents a payment transaction for a booking, tracking booking ID, customer ID, gateway details, amount, currency, and payment status.
- **PaymentTransaction**: Represents individual transaction attempts/logs associated with a payment (`payments` foreign key).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Authenticated customers can successfully initiate and complete mock payment processing for eligible bookings in under 2 seconds.
- **SC-002**: 100% of successful and failed payments correctly publish `PAYMENT_COMPLETED` and `PAYMENT_FAILED` events to Kafka.
- **SC-003**: Zero double-charges or duplicate successful payment records occur under retry or duplicate submission conditions.
- **SC-004**: Unauthorized access attempts to payment endpoints or other customers' booking payments are 100% rejected with 401/403 errors.

## Assumptions

- Feature 001 (Customer Login) and Feature 006 (Customer Service Booking & Scheduling) are fully operational.
- `MockPaymentGateway` simulates deterministic or random success/failure for testing MVP flows.
- Existing database tables (`payments`, `payment_transactions`) and Kafka topic (`payment-events`) require no schema changes.
