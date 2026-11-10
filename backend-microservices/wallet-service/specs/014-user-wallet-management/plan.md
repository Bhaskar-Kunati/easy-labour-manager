# Implementation Plan: User Wallet & Transaction Management (Dual-Destination Refunds & E2E Integration)

**Branch**: `014-user-wallet-management` | **Date**: 2026-10-07 (Updated for Dual-Destination Refunds & E2E Integration) | **Spec**: [specs/014-user-wallet-management/spec.md](spec.md)

**Input**: Feature specification from `backend-microservices/wallet-service/specs/014-user-wallet-management/spec.md`

## Summary

Implement and integrate a dedicated `wallet-service` alongside updates to `booking-service` and `payment-service` to support digital wallets, authoritative top-ups, booking debits, worker earnings payouts, and **dual-destination customer refunds** (`WALLET` and `ORIGINAL_PAYMENT_METHOD`). Concurrency safety is enforced using pessimistic row locking (`PESSIMISTIC_WRITE`) and unique constraints for transaction idempotency, secured via externalized internal service keys (`X-Internal-Service-Key`).

## Technical Context

**Language/Version**: Java 19 / Spring Boot 3.2

**Primary Dependencies**: Spring Web, Spring Data JPA, Spring Security, PostgreSQL Driver, Spring Kafka, Lombok, Validation

**Storage**: PostgreSQL (`wallets`, `wallet_transactions`, `refunds` tables)

**Testing**: JUnit 5, Mockito, Spring Boot Test, Spring Security Test

**Target Platform**: Linux container / local JVM (Docker Compose)

**Project Type**: Spring Boot Microservices (`wallet-service`, `payment-service`, `booking-service`)

**Performance Goals**: <200ms p95 response time for wallet balance and financial operations

**Constraints**: Strict ACID consistency, zero negative balances, race-condition free concurrency via pessimistic locking, idempotent ledger updates, secure internal service authentication

## Refund Architecture & Workflow

### 1. Refund Destinations
- **`WALLET`**: Credited to the customer's digital wallet. `payment-service` verifies approval and invokes `wallet-service` `POST /api/wallet/credit` with reference `REFUND_{refundId}`. `wallet-service` validates `X-Internal-Service-Key`, credits the wallet atomically, records a `CREDIT` ledger transaction, and updates the refund record to `COMPLETED`.
- **`ORIGINAL_PAYMENT_METHOD`**: Processed through the original payment gateway. `payment-service` owns gateway execution and confirmation. Does NOT credit the customer's wallet. Records gateway refund reference and updates status to `COMPLETED`.

### 2. Refund State Machine
- **`PENDING`** (Initial state upon refund request submission and destination selection)
- **`PROCESSING`** (Locked during gateway execution or wallet credit invocation)
- **`COMPLETED`** (Successfully processed via gateway or wallet credit)
- **`FAILED`** (Gateway rejection or wallet service failure; eligible for controlled retry)

### 3. Idempotency & Retry Strategy
- Unique reference ID: `REFUND_{refundId}` for wallet credit; gateway idempotency keys for payment gateway refunds.
- Database uniqueness and state machine checks (`PENDING` -> `PROCESSING` -> `COMPLETED`) prevent duplicate execution.
- Exponential backoff retry for transient gateway or network failures.

### 4. Failure Recovery & Reconciliation
- **Gateway Success, App DB Failure**: Reconciled via scheduled reconciliation jobs querying gateway refund status against application refund records.
- **Mutual Exclusivity**: Strict state checks ensure a refund order can never execute both destinations simultaneously.

## Project Structure

### Documentation (this feature)

```text
backend-microservices/wallet-service/specs/014-user-wallet-management/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output
│   └── rest-api.yaml
└── tasks.md             # Phase 2 output
```
