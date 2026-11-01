# Feature Specification: User Wallet & Transaction Management

**Feature Branch**: `014-user-wallet-management`

**Created**: 2026-10-07

**Status**: Draft

**Input**: User description: "Feature 014 — User Wallet & Transaction Management. The feature enables customers and workers to maintain an in-app digital wallet, view balances and transaction history, add funds, pay for bookings using wallet balance, receive worker earnings/refunds, and allows admins to manage wallets and audit wallet transactions. Use a dedicated `wallet-service`..."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Customer & Worker Wallet Management (Priority: P1)

As an authenticated customer or worker, I want to view my wallet balance and transaction history (including transaction type, amount, reference ID, description, and timestamp) so that I can track my digital funds and earnings associated with my authenticated user identity.

**Why this priority**: Core self-service visibility required for users to inspect their wallet status and transaction ledger.

**Independent Test**: Can be tested by authenticating as a customer/worker, querying `/api/wallet/balance` and `/api/wallet/transactions`, and verifying that only the user's own wallet and transactions are returned.

**Acceptance Scenarios**:

1. **Given** an authenticated customer or worker with an active wallet, **When** they request their wallet balance, **Then** the system returns the correct balance and currency from `wallets`.
2. **Given** an authenticated user with transaction history, **When** they request their wallet transactions, **Then** the system returns an immutable list of CREDIT/DEBIT transactions sorted by creation timestamp.
3. **Given** an unauthenticated request, **When** accessing wallet balance or transactions, **Then** the system rejects the request with 401 Unauthorized.

---

### User Story 2 - Wallet Top-up (Priority: P1)

As an authenticated user, I want to add funds to my wallet so that I have available balance for booking services.

**Why this priority**: Essential mechanism to fund digital wallets for cashless service payments.

**Independent Test**: Can be tested by submitting a top-up request with a positive amount, verifying balance increment and creation of a corresponding CREDIT transaction ledger entry.

**Acceptance Scenarios**:

1. **Given** an authenticated user and a positive top-up amount, **When** the top-up request is processed upon payment gateway verification, **Then** the wallet balance increases by the top-up amount and a `CREDIT` transaction is recorded.
2. **Given** a top-up request with a negative or zero amount or invalid currency, **When** submitted, **Then** the system rejects the request with 400 Bad Request.
3. **Given** a user attempting to top-up another user's wallet, **When** submitted, **Then** the system rejects it with 403 Forbidden.

---

### User Story 3 - Wallet Payment / Debit for Bookings (Priority: P1)

As an authenticated customer, I want to pay for a booking using my wallet balance so that funds are debited securely and atomically.

**Why this priority**: Enables seamless wallet-based checkout for services.

**Independent Test**: Can be tested by initiating a booking payment via wallet, verifying authoritative amount debiting, balance check, and creation of a `DEBIT` transaction record.

**Acceptance Scenarios**:

1. **Given** an authenticated customer with sufficient wallet balance and a valid booking, **When** wallet payment is processed, **Then** the system verifies booking ownership, debits the authoritative booking/payment amount atomically, creates a `DEBIT` ledger transaction, and prevents duplicate debiting for the same reference ID.
2. **Given** a customer with insufficient wallet balance, **When** wallet payment is attempted, **Then** the system rejects the debit with an insufficient balance error.
3. **Given** concurrent debit requests for the same wallet, **When** processed simultaneously, **Then** pessimistic row locking serializes updates and prevents the balance from becoming negative.

---

### User Story 4 - Worker Earnings / Credits (Priority: P2)

As a worker, I want my job earnings to be credited to my wallet upon job completion so that I receive payouts reliably.

**Why this priority**: Supports worker monetization and earnings tracking within the platform ecosystem.

**Independent Test**: Can be tested via internal/system service calls crediting worker wallets upon completion events, verifying ledger updates and idempotency.

**Acceptance Scenarios**:

1. **Given** a successfully completed job/booking, **When** the internal earnings credit service is invoked with an authoritative payout amount and reference ID, **Then** the worker's wallet is credited atomically and a `CREDIT` transaction is recorded.
2. **Given** a duplicate credit request for the same reference ID, **When** processed, **Then** the system rejects or ignores it to prevent duplicate payouts.

---

### User Story 5 - Refund Credits (Priority: P2)

As a user receiving a refund for a cancelled booking, I want the refund amount credited securely to my wallet.

**Why this priority**: Ensures fair and automated refund processing for cancelled or disputed services.

**Independent Test**: Can be tested by initiating an authorized refund workflow, verifying wallet credit and `CREDIT` transaction creation.

**Acceptance Scenarios**:

1. **Given** an authoritative refund approval referencing a booking/payment ID, **When** processed, **Then** the user's wallet is credited with the refund amount and a `CREDIT` transaction is recorded idempotently.
2. **Given** a customer attempting to create arbitrary refund credits directly, **When** submitted, **Then** the request is rejected with 403 Forbidden.

---

### User Story 6 - Admin Wallet Management & Audit (Priority: P2)

As an administrator, I want to view, audit, and manage user wallets and transactions so that I can resolve support queries and maintain financial oversight.

**Why this priority**: Provides necessary operational visibility and governance for platform admins.

**Independent Test**: Can be tested by authenticating as an ADMIN, querying user wallets and audit trails, and verifying restricted access for non-admins.

**Acceptance Scenarios**:

1. **Given** an authenticated admin with ROLE_ADMIN, **When** they query a user's wallet or transaction ledger, **Then** the system returns the requested details and audit trail.
2. **Given** a non-admin user attempting to access admin wallet audit endpoints, **When** requested, **Then** the system returns 403 Forbidden.

---

### User Story 7 - Security, Isolation & Concurrency (Priority: P1)

As a system engineer, I want robust security isolation, transactional atomicity, and pessimistic locking so that financial transactions are race-condition free and users can only access their own wallets.

**Why this priority**: Crucial for financial integrity, preventing double spending, and security compliance.

**Independent Test**: Can be tested via concurrent thread execution tests and cross-user access attempts.

**Acceptance Scenarios**:

1. **Given** multiple concurrent financial operations on the same wallet, **When** executed simultaneously, **Then** pessimistic row locking (`PESSIMISTIC_WRITE`) serializes operations and preserves ledger consistency.
2. **Given** a user attempting to access or modify another user's wallet data, **When** requested, **Then** the system denies access with 403 Forbidden.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST maintain a dedicated `wallet-service` as the authoritative owner of wallet balances, statuses, and transaction ledgers.
- **FR-002**: The system MUST provide self-service REST APIs for authenticated customers and workers to retrieve their wallet balance and transaction history.
- **FR-003**: The system MUST provide top-up and debit operations that validate positive amounts, validate currencies, and enforce user isolation based on `SecurityContext`.
- **FR-004**: The system MUST enforce pessimistic row locking (`PESSIMISTIC_WRITE`) on wallet balance updates to guarantee concurrency safety and prevent negative balances.
- **FR-005**: The system MUST enforce idempotency on all financial operations (top-ups, debits, credits, refunds) using unique reference IDs to prevent duplicate ledger entries.
- **FR-006**: The system MUST publish Kafka events (`WALLET_DEBITED`, `WALLET_CREDITED`, `TOPUP_SUCCESS`) exclusively after successful database transaction commits.
- **FR-007**: The system MUST restrict administrative wallet management and audit endpoints strictly to users with `ROLE_ADMIN`.

### Key Entities *(include if feature involves data)*

- **Wallets**: Represents user digital wallets (`id`, `user_id`, `balance`, `currency`, `status`, `created_at`, `updated_at`).
- **WalletTransactions**: Represents immutable ledger entries (`id`, `wallet_id`, `amount`, `type` [`CREDIT`, `DEBIT`], `reference_id`, `description`, `created_at`).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of wallet balance updates are executed within transactional boundaries protected by pessimistic locking.
- **SC-002**: Zero instances of negative wallet balances or duplicate financial ledger entries under high concurrency test loads.
- **SC-003**: 100% authorization enforcement ensuring users can only access their own wallets, and admins have exclusive audit access.
- **SC-004**: Kafka events are reliably emitted post-transaction commit for downstream services (payment, booking, notifications).

## Assumptions

- Relies on existing JWT authentication and Spring Security context infrastructure.
- Payment gateway confirmation for top-up flows depends on webhook/callback verification from `payment-service`.
- Monetary amounts utilize `BigDecimal` with 2 decimal places precision.
