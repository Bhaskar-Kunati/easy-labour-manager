# Research: User Wallet & Transaction Management

## 1. Concurrency Control in Financial Ledgers
- **Decision**: Use Spring Data JPA `@Lock(LockModeType.PESSIMISTIC_WRITE)` combined with `@Transactional` service methods.
- **Rationale**: Prevents race conditions where concurrent top-ups, debits, or credits read stale balance values. Pessimistic locking locks the specific user wallet row during the transaction, guaranteeing serial execution and zero negative balances.
- **Alternatives Considered**: Optimistic locking (`@Version`) - rejected because high concurrency on frequently updated wallets (e.g., peak booking hours) would cause frequent `OptimisticLockingFailureException` retries.

## 2. Idempotency & Duplicate Prevention
- **Decision**: Enforce a unique constraint on `wallet_transactions(reference_id)` or check existing transactions for a given `reference_id` within the transactional scope before applying credits/debits.
- **Rationale**: Ensures that retried payment webhooks, booking requests, or payout events do not result in double crediting or double debiting.

## 3. Microservice Ownership & Integration
- **Decision**: Dedicated `wallet-service` owns all wallet data and transactions. It communicates via REST APIs for synchronous queries and Kafka events (`WALLET_DEBITED`, `WALLET_CREDITED`, `TOPUP_SUCCESS`) for asynchronous event notifications.
- **Rationale**: Adheres to the bounded context principle, decoupling wallet accounting from `payment-service` and `booking-service`.
