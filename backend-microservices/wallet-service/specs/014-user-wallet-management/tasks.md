# Implementation Tasks: User Wallet & Transaction Management

**Feature Branch**: `014-user-wallet-management`
**Spec**: [specs/014-user-wallet-management/spec.md](spec.md)
**Plan**: [specs/014-user-wallet-management/plan.md](plan.md)

## Phase 1: Setup / Existing Infrastructure Verification
- [x] T001 Verify existing project structure and parent build configuration in `backend-microservices/build.gradle`.
- [x] T002 Initialize `wallet-service` module structure and `build.gradle` with Spring Boot 3.2, Spring Data JPA, Spring Security, PostgreSQL, and Kafka dependencies.
- [x] T003 [P] Create application configuration `backend-microservices/wallet-service/src/main/resources/application.yml` with database, JPA, security, and Kafka properties.
- [x] T004 [P] Create main application entry point `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/WalletServiceApplication.java`.

## Phase 2: Foundational Database & Domain Model
- [x] T005 Create Flyway migration or JPA DDL schema configuration for `wallets` table in `backend-microservices/wallet-service/src/main/resources/db/migration/V1__init_wallet_schema.sql` with unique index on `user_id`.
- [x] T006 Create Flyway migration or JPA DDL schema configuration for `wallet_transactions` table in `backend-microservices/wallet-service/src/main/resources/db/migration/V1__init_wallet_schema.sql` with unique index on `reference_id` and foreign key to `wallets`.
- [x] T007 [P] Implement `Wallet` JPA entity in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/domain/model/Wallet.java` with decimal precision for balance and status.
- [x] T008 [P] Implement `WalletTransaction` JPA entity and `TransactionType` enum in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/domain/model/WalletTransaction.java`.
- [x] T009 Implement `WalletRepository` in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/domain/repository/WalletRepository.java` including pessimistic write lock query `findByUserIdWithLock`.
- [x] T010 [P] Implement `WalletTransactionRepository` in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/domain/repository/WalletTransactionRepository.java`.

## Phase 3: Customer & Worker Wallet Management [US1]
- [x] T011 [US1] Implement security context helper and DTOs for wallet balance and transaction responses in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/web/dto/`.
- [x] T012 [US1] Implement core `WalletService` methods for retrieving or auto-creating user wallets and fetching transaction history in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/application/service/WalletService.java`.
- [x] T013 [US1] Implement `WalletController` exposing `GET /api/wallet/balance` and `GET /api/wallet/transactions` in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/web/controller/WalletController.java`.

## Phase 4: Wallet Top-up Integration [US2]
- [x] T014 [US2] Implement top-up request DTO with validation (positive amount, valid currency) in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/web/dto/TopupRequest.java`.
- [x] T015 [US2] Implement transactional top-up service method with pessimistic locking, balance increment, ledger credit recording, and reference-based idempotency check in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/application/service/WalletService.java`.
- [x] T016 [US2] Add `POST /api/wallet/topup` endpoint to `WalletController` in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/web/controller/WalletController.java`.

## Phase 5: Wallet Payment / Debit Integration [US3] & Payment Compatibility (Feature 008)
- [x] T017 [US3] Implement internal debit request DTO and validation in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/web/dto/WalletDebitRequest.java`.
- [x] T018 [US3] Implement atomic wallet debit service method utilizing `findByUserIdWithLock`, insufficient balance check, balance decrement, `DEBIT` transaction recording, and duplicate debit prevention in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/application/service/WalletService.java`.
- [x] T019 [US3] Implement internal service endpoint `POST /api/wallet/debit` (verifying authoritative booking/payment amounts without client-supplied balance overrides) in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/web/controller/WalletInternalController.java`.

## Phase 6: Worker Earnings & Refund Credits [US4, US5]
- [x] T020 [US4, US5] Implement internal credit request DTO for worker earnings and refunds in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/web/dto/WalletCreditRequest.java`.
- [x] T021 [US4, US5] Implement atomic wallet credit service method with pessimistic locking, ledger credit recording, and reference-based idempotency to prevent duplicate payouts or refund credits in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/application/service/WalletService.java`.
- [x] T022 [US4, US5] Implement internal service endpoint `POST /api/wallet/credit` in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/web/controller/WalletInternalController.java`.

## Phase 7: Admin Wallet Management [US6]
- [x] T023 [US6] Implement admin wallet audit and management service methods in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/application/service/WalletService.java`.
- [x] T024 [US6] Implement `AdminWalletController` exposing admin wallet audit and status management endpoints secured with `ROLE_ADMIN` in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/web/controller/AdminWalletController.java`.

## Phase 8: Kafka Event Publishing [US1 - US5]
- [x] T025 Implement Kafka producer configuration in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/infrastructure/kafka/WalletEventProducer.java`.
- [x] T026 Integrate post-commit Kafka event publishing for `WALLET_DEBITED`, `WALLET_CREDITED`, and `TOPUP_SUCCESS` events in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/application/service/WalletService.java`.

## Phase 9: Security and Authorization [US7]
- [x] T027 Configure Spring Security JWT authentication and authorization rules for self-service vs internal vs admin endpoints in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/infrastructure/security/SecurityConfig.java`.
- [x] T028 [P] Implement user isolation checks to ensure customers/workers can only access their own wallets and reject cross-user access with 403 Forbidden.

## Phase 10: Testing & Concurrency Validation
- [x] T029 [P] Implement unit and integration tests for wallet balance retrieval and transaction history in `backend-microservices/wallet-service/src/test/java/com/easylabour/walletservice/application/service/WalletServiceTest.java`.
- [x] T030 Implement concurrent debit/top-up concurrency test (`WalletConcurrencyTest`) verifying pessimistic locking prevents race conditions, lost updates, and negative balances.
- [x] T031 [P] Implement idempotency tests verifying duplicate reference IDs do not create duplicate ledger entries.
- [x] T032 [P] Implement security and authorization tests verifying 401/403 responses for unauthenticated requests, cross-user access attempts, and non-admin access to admin endpoints.
- [x] T033 Run Gradle test suite for `wallet-service` and verify regression compatibility across backend microservices.
