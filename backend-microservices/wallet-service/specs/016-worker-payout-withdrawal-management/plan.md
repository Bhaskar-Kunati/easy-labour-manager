# Implementation Plan: Worker Payout & Bank Withdrawal Management

**Branch**: `016-worker-payout-withdrawal-management` | **Date**: 2026-11-11 | **Spec**: [specs/016-worker-payout-withdrawal-management/spec.md](spec.md)

**Input**: Feature specification from `backend-microservices/wallet-service/specs/016-worker-payout-withdrawal-management/spec.md`

## Summary

Implement worker payout request handling, destination management integration, pessimistic wallet fund reservation, admin review and approval orchestration, and mock payout execution in `wallet-service`. The feature ensures separate available and reserved wallet balances, transactional idempotency via unique payout reference IDs (`PAYOUT_{id}`), Kafka event publishing for notifications, and robust handling of gateway states including uncertain (`UNKNOWN`) outcomes.

## Technical Context

**Language/Version**: Java 19 / Spring Boot 3.2

**Primary Dependencies**: Spring Web, Spring Data JPA, Spring Security, PostgreSQL Driver, Spring Kafka, Lombok, Validation

**Storage**: PostgreSQL (`wallets`, `wallet_transactions`, `payout_requests` tables)

**Testing**: JUnit 5, Mockito, Spring Boot Test

**Target Platform**: Linux container / local JVM (Docker Compose)

**Project Type**: Spring Boot Microservice (`wallet-service`)

**Performance Goals**: <200ms p95 response time for payout request creation and status updates

**Constraints**: Strict financial consistency, zero negative balances, pessimistic locking for fund reservation, idempotent payout processing, mock gateway integration for MVP.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- Spring Boot & Java 19 conventions followed: **PASS**
- Dedicated microservice isolation (`wallet-service` owning payout & reservation lifecycle): **PASS**
- Transaction safety & pessimistic locking for fund reservation: **PASS**
- Idempotency, transaction outbox pattern, and Kafka event emission post-commit: **PASS**

## Project Structure

### Documentation (this feature)

```text
backend-microservices/wallet-service/specs/016-worker-payout-withdrawal-management/
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
│   ├── domain/model/PayoutRequest.java, Wallet.java
│   ├── application/service/PayoutService.java, WalletService.java
│   ├── web/controller/PayoutController.java, AdminPayoutController.java
│   └── infrastructure/gateway/MockPayoutGateway.java
```

**Structure Decision**: Extends existing `wallet-service` architecture with domain entities for payout requests, service orchestration, controller endpoints, and mock gateway integration.
