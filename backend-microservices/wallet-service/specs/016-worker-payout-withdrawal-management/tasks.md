# Implementation Tasks: Worker Payout & Bank Withdrawal Management

**Feature Branch**: `016-worker-payout-withdrawal-management`  
**Spec**: [specs/016-worker-payout-withdrawal-management/spec.md](spec.md)  
**Plan**: [specs/016-worker-payout-withdrawal-management/plan.md](plan.md)  

## Phase 1: Setup & Configuration
- [x] T001 Verify `wallet-service` module structure and dependencies in `backend-microservices/wallet-service/build.gradle`. [wallet-service]
- [x] T002 [P] Configure database connection and Kafka properties in `backend-microservices/wallet-service/src/main/resources/application.yml`. [wallet-service]
- [x] T003 [P] Configure internal service API key properties (`internal.service.key`) in `backend-microservices/wallet-service/src/main/resources/application.yml`. [wallet-service]

## Phase 2: Foundational Database Schema & Entities
- [x] T004 Create Flyway migration `backend-microservices/wallet-service/src/main/resources/db/migration/V2__add_reserved_balance_and_payouts.sql` adding `reserved_balance DECIMAL(19,2) NOT NULL DEFAULT 0.00` to `wallets` table. [wallet-service]
- [x] T005 Create `payout_requests` table migration in `backend-microservices/wallet-service/src/main/resources/db/migration/V2__add_reserved_balance_and_payouts.sql` with columns: `id` (PK), `wallet_id` (FK), `user_id` (VARCHAR 100 NOT NULL), `amount` (DECIMAL 19,2 NOT NULL), `currency` (VARCHAR 3 NOT NULL DEFAULT 'INR'), `destination_id` (VARCHAR 100 NOT NULL), `destination_type` (VARCHAR 20 NOT NULL), `status` (VARCHAR 30 NOT NULL), `reference_id` (VARCHAR 100 NOT NULL UNIQUE), `gateway_reference` (VARCHAR 100), `failure_reason` (VARCHAR 255), `created_at` (TIMESTAMP), `updated_at` (TIMESTAMP). [wallet-service]
- [x] T006 [P] Update `Wallet` entity in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/domain/model/Wallet.java` to add `reservedBalance` field (`DECIMAL(19,2)`). [wallet-service]
- [x] T007 [P] Implement `PayoutRequest` JPA entity and `PayoutStatus` / `DestinationType` enums in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/domain/model/PayoutRequest.java`. [wallet-service]
- [x] T008 Implement `PayoutRequestRepository` in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/domain/repository/PayoutRequestRepository.java` with finders for user and status. [wallet-service]
- [x] T009 Update `WalletRepository` in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/domain/repository/WalletRepository.java` to support pessimistic locking for balance reservation. [wallet-service]

## Phase 3: Payout Destination Management & Integration [US1]
- [x] T010 [US1] Implement bank and UPI destination DTOs and client integration interface for `user-service` in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/infrastructure/client/UserServiceClient.java`. [wallet-service]
- [x] T011 [US1] Implement destination validation logic verifying destination ownership and active status via `user-service` in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/application/service/PayoutService.java`. [wallet-service]

## Phase 4: Worker Withdrawal Request & Pessimistic Fund Reservation [US2]
- [x] T012 [US2] Implement withdrawal request DTO with validation (`amount > 0`, `destinationId`, `destinationType`) in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/web/dto/PayoutRequestDto.java`. [wallet-service]
- [x] T013 [US2] Implement atomic fund reservation service method in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/application/service/PayoutService.java` using `@Lock(LockModeType.PESSIMISTIC_WRITE)`, checking available balance, decrementing `balance`, incrementing `reserved_balance`, and creating `PayoutRequest` with `PENDING` status. [wallet-service]
- [x] T014 [US2] Implement worker payout controller endpoints `POST /api/wallet/payouts` and `GET /api/wallet/payouts` in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/web/controller/PayoutController.java`. [wallet-service]

## Phase 5: Admin Payout Review, Approval & Mock Gateway Execution [US3]
- [ ] T015 [US3] Implement `MockPayoutGateway` component simulating external bank/UPI payouts with configurable success, failure, and unknown outcomes in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/infrastructure/gateway/MockPayoutGateway.java`. [wallet-service]
- [ ] T016 [US3] Implement admin approval service orchestration in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/application/service/PayoutService.java`: updating status to `PROCESSING`, invoking mock gateway, converting reserved funds into completed debit ledger entry `PAYOUT_{id}`, and updating status to `COMPLETED`. [wallet-service]
- [ ] T017 [US3] Implement internal wallet payout admin endpoints in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/web/controller/WalletInternalController.java` secured with `X-Internal-Service-Key`. [wallet-service]
- [ ] T018 [US3] Implement admin payout review endpoints (`GET /api/v1/admin/payouts`, `POST /api/v1/admin/payouts/{id}/approve`) in `admin-service` delegating to `wallet-service` via internal REST client. [admin-service]

## Phase 6: Rejection, Cancellation & Confirmed Failure Fund Releases [US4]
- [ ] T019 [US4] Implement admin rejection and worker cancellation service methods in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/application/service/PayoutService.java` ensuring reserved funds are released back to available balance exactly once. [wallet-service]
- [ ] T020 [US4] Implement confirmed gateway failure handling in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/application/service/PayoutService.java` releasing reserved funds back to available balance and setting status to `FAILED`. [wallet-service]
- [ ] T021 [US4] Implement admin rejection endpoint `POST /api/v1/admin/payouts/{id}/reject` in `admin-service` and `wallet-service`. [admin-service, wallet-service]

## Phase 7: Gateway Uncertainty Handling, Idempotency & Kafka Events [US5]
- [ ] T022 [US5] Implement gateway outcome handler in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/application/service/PayoutService.java` that detects `UNKNOWN` responses, retains fund reservations (`UNKNOWN` status), and prevents automatic retries or releases. [wallet-service]
- [ ] T023 [US5] Implement idempotent reference check (`PAYOUT_{id}`) preventing duplicate reservations or duplicate releases. [wallet-service]
- [ ] T024 [US5] Implement Kafka event producer for payout lifecycle events (`PAYOUT_REQUESTED`, `PAYOUT_COMPLETED`, `PAYOUT_REJECTED`, `PAYOUT_FAILED`) in `backend-microservices/wallet-service/src/main/java/com/easylabour/walletservice/infrastructure/kafka/PayoutEventProducer.java`. [wallet-service]
- [ ] T025 [US5] Integrate `notification-service` consumer to listen to payout Kafka events and trigger worker notifications. [notification-service]

## Phase 8: Testing & Verification
- [ ] T026 [P] Implement unit tests for `PayoutService` and `MockPayoutGateway` in `backend-microservices/wallet-service/src/test/java/com/easylabour/walletservice/application/service/PayoutServiceTest.java`. [wallet-service]
- [ ] T027 Implement concurrency test verifying pessimistic locking prevents race conditions and negative balances during concurrent withdrawal requests. [wallet-service]
- [ ] T028 [P] Implement idempotency test verifying duplicate reference IDs (`PAYOUT_{id}`) do not result in double debits or double reservations. [wallet-service]
- [ ] T029 [P] Implement gateway uncertainty test verifying `UNKNOWN` gateway outcomes retain reservations without auto-retry or fund release. [wallet-service]
- [ ] T030 [P] Implement security and authorization tests verifying `ROLE_WORKER` ownership isolation and `ROLE_ADMIN` protection. [wallet-service, admin-service]

## Phase 9: Polish, Documentation & Build Verification
- [ ] T031 [P] Verify compilation and test suite execution using Gradle JVM limits (`./gradlew :wallet-service:test --jvm-args="-Xms128m -Xmx256m"`). [wallet-service]
- [ ] T032 [P] Update API documentation and quickstart instructions in `backend-microservices/wallet-service/specs/016-worker-payout-withdrawal-management/quickstart.md`. [wallet-service]
