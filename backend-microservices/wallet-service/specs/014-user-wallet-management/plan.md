# Implementation Plan: User Wallet & Transaction Management

**Branch**: `014-user-wallet-management` | **Date**: 2026-10-07 | **Spec**: [specs/014-user-wallet-management/spec.md](spec.md)

**Input**: Feature specification from `backend-microservices/wallet-service/specs/014-user-wallet-management/spec.md`

## Summary

Implement a dedicated `wallet-service` using Spring Boot 3.2 (Java 19), Spring Data JPA with PostgreSQL, and Spring Security JWT. The service will be the authoritative owner of digital wallets, balances, transaction ledger (`wallet_transactions`), top-ups, booking debits, worker earnings credits, refunds, and admin audits. Concurrency safety will be enforced using pessimistic row locking (`PESSIMISTIC_WRITE`) and unique constraints for transaction idempotency, integrated via Kafka events.

## Technical Context

**Language/Version**: Java 19 / Spring Boot 3.2

**Primary Dependencies**: Spring Web, Spring Data JPA, Spring Security, PostgreSQL Driver, Spring Kafka, Lombok, Validation

**Storage**: PostgreSQL (`wallets`, `wallet_transactions` tables)

**Testing**: JUnit 5, Mockito, Spring Boot Test, Testcontainers (optional/embedded)

**Target Platform**: Linux container / local JVM (Docker Compose)

**Project Type**: Spring Boot Microservice (`wallet-service`)

**Performance Goals**: <200ms p95 response time for wallet balance and debit operations

**Constraints**: Strict ACID consistency, zero negative balances, race-condition free concurrency via pessimistic locking, idempotent ledger updates

**Scale/Scope**: Platform-wide user wallet management (customers and workers)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- Spring Boot & Java 19 conventions followed: **PASS**
- Dedicated microservice isolation (`wallet-service`): **PASS**
- Transaction safety & pessimistic locking for financial ledger: **PASS**
- Idempotency and Kafka event emission post-commit: **PASS**

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
└── tasks.md             # Phase 2 output (created by /speckit.tasks)
```

### Source Code (repository root)

```text
backend-microservices/wallet-service/
├── src/main/java/com/easylabour/walletservice/
│   ├── WalletServiceApplication.java
│   ├── domain/
│   │   ├── model/
│   │   └── repository/
│   ├── application/
│   │   └── service/
│   ├── web/
│   │   ├── controller/
│   │   └── dto/
│   └── infrastructure/
│       └── kafka/
└── src/test/java/com/easylabour/walletservice/
    ├── application/service/
    └── web/controller/
```

**Structure Decision**: Standard Spring Boot microservice layout matching sibling services (`booking-service`, `payment-service`, `auth-service`).
