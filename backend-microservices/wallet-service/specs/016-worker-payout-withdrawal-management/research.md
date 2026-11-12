# Research: Worker Payout & Bank Withdrawal Management

## 1. Pessimistic Fund Reservation & Balance Split
- **Decision**: Add `reserved_balance` column to `wallets` table alongside `balance` (available balance). Payout requests lock the wallet row using `@Lock(LockModeType.PESSIMISTIC_WRITE)`, subtract from available `balance`, and add to `reserved_balance`.
- **Rationale**: Guarantees atomic reservation without race conditions. Reserved funds are explicitly tracked and cannot be spent on bookings or other withdrawals.

## 2. Gateway Outcome Handling & Uncertain States (`UNKNOWN`)
- **Decision**: When calling the mock gateway (and future real gateways), if the response indicates timeout or uncertainty (`UNKNOWN`), retain the fund reservation (`RESERVED` status) and do NOT automatically retry or release funds. Log a high-priority alert for manual administrative reconciliation.
- **Rationale**: Prevents accidental fund loss or double releases caused by transient network failures during payout execution.

## 3. Idempotency & Unique References
- **Decision**: Assign a unique payout reference ID (`PAYOUT_{id}`) and enforce a unique constraint or duplicate check on active payout requests per worker/reference. Ensure state transitions (PENDING -> APPROVED -> COMPLETED or REJECTED/FAILED) are guarded by optimistic versioning (`@Version`) or strict state validation.
- **Rationale**: Prevents double payouts or repeated fund releases.

## 4. Cross-Service Integration (`user-service` & `admin-service`)
- **Decision**: `wallet-service` verifies destination validity by calling `user-service` (or storing destination snapshot/ID). `admin-service` exposes administrative payout endpoints (`ROLE_ADMIN`) and delegates calls to `wallet-service` using secure internal REST (`X-Internal-Service-Key`).
- **Rationale**: Maintains clean microservice boundaries where user-service owns bank/UPI profiles and wallet-service owns financial ledger and payouts.
