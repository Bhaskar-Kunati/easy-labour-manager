# Specification: Worker Payout & Bank Withdrawal Management

**Feature Branch**: `016-worker-payout-withdrawal-management`  
**Component**: `wallet-service`  

## Overview

Workers on the Easy Labour Manager platform earn funds upon successfully completing jobs. To complete the economic loop, workers require a reliable mechanism to withdraw their accumulated wallet earnings to their bank accounts or UPI destinations. Feature 016 introduces worker payout destination management, withdrawal request workflows, pessimistic wallet fund reservation, admin review and approval, and mock payout execution with robust idempotency and failure recovery.

## Architecture Decisions

- **Wallet Service Ownership**: `wallet-service` owns payout requests, payout lifecycle management, wallet fund reservation, ledger entries (`PAYOUT_{id}`), approval/rejection business rules, and payout execution orchestration.
- **User Service Ownership**: `user-service` owns worker bank account and UPI destination profiles. Sensitive bank/UPI information is encrypted at rest and masked in API responses.
- **Admin Service Delegation**: `admin-service` exposes `ROLE_ADMIN`-protected payout review endpoints and delegates payout operations to `wallet-service` through authenticated internal REST communication (`X-Internal-Service-Key`).
- **Notification Integration**: `notification-service` receives asynchronous payout lifecycle events via Kafka.
- **Pessimistic Locking**: Leverages existing wallet pessimistic locking (`findByUserIdWithLock`) and immutable ledger mechanisms. No separate payout microservice is introduced.

## Functional Scope

### 1. Payout Destination Management
- Workers can register and manage their payout destinations (Bank Account / UPI ID).
- Sensitive destination data is stored securely and masked when viewed.
- Workers can view their own destination profiles.

### 2. Withdrawal Requests
- Workers can request withdrawal from their available wallet balance.
- Withdrawal requests validate positive amounts, system currency (`INR`), destination ownership, and sufficient available funds.
- Payout requests reserve funds atomically using pessimistic wallet row locking, moving funds from available balance to reserved balance.

### 3. Administrative Review & Approval
- Administrators (`ROLE_ADMIN`) can list and review pending payout requests.
- Administrators can approve or reject payout requests.
- Rejection or cancellation automatically releases reserved funds back to the worker's available wallet balance exactly once.

### 4. Payout Execution & Lifecycle
- Approved payouts are submitted to a mock payout gateway.
- Successful payouts complete the withdrawal and record immutable debit ledger transactions (`PAYOUT_{id}`).
- Confirmed failed payouts release reserved funds exactly once.
- Unknown gateway outcomes retain fund reservations and require reconciliation before retry or final resolution.

## Financial Rules

- **Available Balance vs. Reserved Balance**: Clearly distinguish available funds from reserved funds pending payout. Reserved funds cannot be used for subsequent withdrawals.
- **Ledger Immutability**: All payout reservation, completion, failure, and reversal operations produce immutable, auditable wallet ledger transactions.
- **Monetary Precision**: All monetary calculations and storage use `BigDecimal` and `DECIMAL(19,2)` precision.
- **Concurrency Safety**: Concurrent withdrawal requests are safely serialized and checked against available balance using pessimistic row locking.

## External API Contracts

- **Worker Payout APIs**:
  - `POST /api/wallet/payouts` (Request withdrawal)
  - `GET /api/wallet/payouts` (List worker payout history)
  - `POST /api/wallet/destinations` (Register payout destination)
  - `GET /api/wallet/destinations` (List payout destinations)
- **Admin Payout APIs (exposed via admin-service / internal wallet APIs)**:
  - `GET /api/v1/admin/payouts` (List pending/all payouts)
  - `POST /api/v1/admin/payouts/{id}/approve` (Approve payout)
  - `POST /api/v1/admin/payouts/{id}/reject` (Reject payout)

## Non-Functional Requirements

- **Security**: Strict `ROLE_WORKER` for worker operations and `ROLE_ADMIN` for administrative actions. Ownership checks enforced on all worker-facing endpoints. Internal service calls authenticated via `X-Internal-Service-Key`.
- **Data Protection**: Bank account numbers and UPI details must be masked in API responses (e.g., `******1234`) and encrypted at rest.
- **Idempotency**: Unique payout reference IDs (`PAYOUT_{id}`) and database constraints prevent duplicate reservation, release, debit, or payout execution.
- **Resilience**: Graceful error handling for downstream service failures without corrupting wallet state.

## Out of Scope

- Real bank or UPI payout gateway API integration.
- Automated NEFT, IMPS, or RTGS bank transfer processing.
- Multi-bank provider routing and failover.
- Advanced tax deduction (TDS) and regulatory reporting.
- Automated payout batching and settlement schedules with banking partners. (Mock payout execution does not transfer real money).

## Testable Scenarios

1. **Successful Withdrawal & Payout**: Worker requests withdrawal, admin approves, mock gateway succeeds, wallet balance debited permanently and ledger recorded.
2. **Rejection & Fund Reversal**: Worker requests withdrawal, admin rejects, reserved funds are restored to available balance exactly once.
3. **Insufficient Funds**: Worker requests withdrawal exceeding available balance; request is rejected with HTTP 400.
4. **Concurrent Requests**: Two concurrent withdrawal requests for the total available balance; exactly one succeeds and one fails due to insufficient available funds.
5. **Security Isolation**: Worker A attempting to view or cancel Worker B's payout receives HTTP 403 Forbidden.
