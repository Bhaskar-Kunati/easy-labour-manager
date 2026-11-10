# Feature Specification: User Wallet & Transaction Management (Dual-Destination Refunds & E2E Integration)

**Feature Branch**: `014-user-wallet-management`

**Created**: 2026-10-07 (Updated for Dual-Destination Refund Architecture & E2E Integration Audit Compliance)

**Status**: Approved / Ready for Planning

**Input**: Feature description: "Feature 014 — User Wallet & Transaction Management. The feature enables customers and workers to maintain an in-app digital wallet, view balances and transaction history, add funds, pay for bookings using wallet balance, receive worker earnings/refunds (supporting both WALLET and ORIGINAL_PAYMENT_METHOD destinations), and allows admins to manage wallets and audit wallet transactions. Use a dedicated `wallet-service` with complete end-to-end integration across booking-service, payment-service, and wallet-service."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Customer & Worker Wallet Management (Priority: P1)

As an authenticated customer or worker, I want to view my wallet balance and transaction history (including transaction type, amount, reference ID, description, and timestamp) so that I can track my digital funds and earnings associated with my authenticated user identity.

**Acceptance Scenarios**:
1. **Given** an authenticated customer or worker with an active wallet, **When** they request their wallet balance, **Then** the system returns the correct balance and currency from `wallets`.
2. **Given** an authenticated user with transaction history, **When** they request their wallet transactions, **Then** the system returns an immutable list of CREDIT/DEBIT transactions sorted by creation timestamp.
3. **Given** an unauthenticated request, **When** accessing wallet balance or transactions, **Then** the system rejects the request with 401 Unauthorized.

---

### User Story 2 - Wallet Top-up via Authoritative Payment Verification (Priority: P1)

As an authenticated user, I want to add funds to my wallet securely using verified payment gateway transactions so that my wallet balance is funded reliably.

**Acceptance Scenarios**:
1. **Given** an authenticated user and a successful payment in `payment-service`, **When** the top-up confirmation request is processed with `paymentId`, **Then** the system verifies payment ownership and success, credits the authoritative payment amount, records a `CREDIT` transaction, and publishes a `TOPUP_SUCCESS` event.
2. **Given** an unknown, failed, or customer-mismatched payment ID, **When** top-up confirmation is attempted, **Then** the system rejects the request with 400 Bad Request or 403 Forbidden.
3. **Given** duplicate confirmation attempts for the same payment ID, **When** processed concurrently or sequentially, **Then** database idempotency checks via reference ID (`PAYMENT_{paymentId}`) reject the duplicate request with 409 Conflict.

---

### User Story 3 - Wallet Payment / Debit for Bookings (Priority: P1)

As an authenticated customer, I want to pay for a booking using my wallet balance so that funds are debited securely and atomically.

**Acceptance Scenarios**:
1. **Given** an authenticated customer with sufficient wallet balance and a valid booking, **When** wallet payment is processed via internal debit API, **Then** the system verifies booking ownership, debits the authoritative booking/payment amount atomically, creates a `DEBIT` ledger transaction, and prevents duplicate debiting for the same reference ID.
2. **Given** a customer with insufficient wallet balance, **When** wallet payment is attempted, **Then** the system rejects the debit with an insufficient balance error (400 Bad Request).
3. **Given** concurrent debit requests for the same wallet, **When** processed simultaneously, **Then** pessimistic row locking (`PESSIMISTIC_WRITE`) serializes updates and prevents the balance from becoming negative.

---

### User Story 4 - Worker Earnings Integration (Priority: P1)

As a worker, I want my job earnings to be credited to my wallet automatically upon successful job completion so that I receive reliable payouts without manual intervention.

**Acceptance Scenarios**:
1. **Given** a successfully completed job/booking in `booking-service`, **When** the job status transitions to completed, **Then** `booking-service` securely invokes `wallet-service` (`POST /api/wallet/credit`) with trusted worker ID, authoritative payout amount, and idempotency reference ID (`BOOKING_EARNINGS_{bookingId}`).
2. **Given** duplicate completion notifications or retries for the same booking, **When** processed, **Then** database idempotency constraints prevent duplicate credits.
3. **Given** an untrusted client attempting to invoke earnings credit or supply arbitrary worker IDs/amounts, **When** submitted, **Then** the request is rejected due to internal service key authentication and trusted backend derivation.

---

### User Story 5 - Dual-Destination Customer Refund Integration (Priority: P1)

As an authenticated customer receiving an approved refund for a cancelled booking, I want my refund processed securely according to my explicitly approved refund destination (`WALLET` or `ORIGINAL_PAYMENT_METHOD`).

**Acceptance Scenarios**:
1. **Given** an approved refund with destination `WALLET`, **When** processed, **Then** `payment-service` invokes `wallet-service` (`POST /api/wallet/credit`) with the trusted customer ID, authoritative refund amount, and unique idempotency reference ID (`REFUND_{refundId}`), crediting the wallet atomically and updating the refund status to `COMPLETED`.
2. **Given** an approved refund with destination `ORIGINAL_PAYMENT_METHOD`, **When** processed, **Then** `payment-service` executes the refund through the original payment gateway, records the gateway refund reference, updates the status to `COMPLETED`, and does NOT credit the customer's wallet.
3. **Given** an attempt to alter an approved refund destination or amount via untrusted client input, **When** submitted, **Then** the system rejects the modification and enforces backend-derived refund records.
4. **Given** concurrent or duplicate refund execution attempts for the same refund ID, **When** processed, **Then** state machine validations and unique reference constraints prevent duplicate wallet crediting or gateway refund execution.

---

### User Story 6 - Internal Wallet API Security (Priority: P1)

As a system architect, I want internal service-to-service endpoints (`/api/wallet/debit`, `/api/wallet/credit`) protected by robust filter-level authentication using externalized internal service secrets (`X-Internal-Service-Key`) so that internal APIs cannot be accessed by external clients.

**Acceptance Scenarios**:
1. **Given** a request to `/api/wallet/debit` or `/api/wallet/credit` with a valid internal service secret header (`X-Internal-Service-Key`), **When** processed, **Then** the request is authenticated and executed successfully.
2. **Given** a request to internal wallet endpoints with missing or invalid internal service keys, **When** submitted, **Then** Spring Security / filter layer rejects the request with 401/403 Unauthorized/Forbidden.
3. **Given** customer JWT tokens sent to internal endpoints, **When** submitted, **Then** they do not bypass internal secret verification.

---

### User Story 7 - Admin Wallet Management & Audit (Priority: P2)

As an administrator, I want to view, audit, and manage user wallets and transactions so that I can resolve support queries and maintain financial oversight.

**Acceptance Scenarios**:
1. **Given** an authenticated admin with ROLE_ADMIN, **When** they query a user's wallet or transaction ledger, **Then** the system returns requested details and audit trail.
2. **Given** a non-admin user attempting access, **When** requested, **Then** returns 403 Forbidden.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST maintain a dedicated `wallet-service` as the authoritative owner of wallet balances, statuses, and transaction ledgers.
- **FR-002**: The system MUST provide self-service REST APIs for authenticated customers and workers to retrieve their wallet balance and transaction history.
- **FR-003**: The system MUST verify payment success, gateway status, and customer ownership via `payment-service` before crediting wallets for top-ups.
- **FR-004**: The system MUST enforce pessimistic row locking (`PESSIMISTIC_WRITE`) on wallet balance updates to guarantee concurrency safety and prevent negative balances.
- **FR-005**: The system MUST enforce idempotency on all financial operations using unique reference IDs (`PAYMENT_{id}`, `BOOKING_EARNINGS_{id}`, `REFUND_{id}`) to prevent duplicate ledger entries.
- **FR-006**: The system MUST publish Kafka events (`WALLET_DEBITED`, `WALLET_CREDITED`, `TOPUP_SUCCESS`) exclusively after successful database transaction commits.
- **FR-007**: The system MUST restrict administrative management endpoints strictly to users with `ROLE_ADMIN`.
- **FR-008**: The `booking-service` MUST automatically invoke `wallet-service` internal credit API upon successful job completion using trusted backend worker IDs and authoritative payout amounts.
- **FR-009**: The system MUST validate and secure internal service endpoints (`/api/wallet/debit`, `/api/wallet/credit`) at the Spring Security filter/handler level using externalized internal service secrets (`X-Internal-Service-Key`).
- **FR-010**: The system MUST support dual refund destinations (`WALLET` and `ORIGINAL_PAYMENT_METHOD`), ensuring explicit destination approval, authoritative amount/customer derivation, strict state machine validation, idempotency, and non-simultaneous mutual exclusivity.

### Key Entities *(include if feature involves data)*

- **Wallets**: Represents user digital wallets (`id`, `user_id`, `balance`, `currency`, `status`, `created_at`, `updated_at`).
- **WalletTransactions**: Represents immutable ledger entries (`id`, `wallet_id`, `amount`, `type` [`CREDIT`, `DEBIT`], `reference_id`, `description`, `created_at`).
- **Refunds**: Represents refund orders (`id`, `booking_id`, `payment_id`, `customer_id`, `amount`, `currency`, `destination` [`WALLET`, `ORIGINAL_PAYMENT_METHOD`], `status` [`PENDING`, `PROCESSING`, `COMPLETED`, `FAILED`], `gateway_refund_id`, `created_at`, `updated_at`).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of wallet balance updates are executed within transactional boundaries protected by pessimistic locking.
- **SC-002**: Zero instances of negative wallet balances or duplicate financial ledger entries under high concurrency test loads.
- **SC-003**: 100% authorization enforcement securing internal service APIs and ensuring user data isolation.
- **SC-004**: Kafka events are reliably emitted post-transaction commit for downstream services.
- **SC-005**: 100% accurate routing and state enforcement for dual refund destinations (`WALLET` vs `ORIGINAL_PAYMENT_METHOD`) without double-crediting or gateway race conditions.

## Assumptions

- Relies on existing JWT authentication and Spring Security context infrastructure.
- Internal service secret is securely configured across microservices via shared environment properties.
- Monetary amounts utilize `BigDecimal` with 2 decimal places precision.
