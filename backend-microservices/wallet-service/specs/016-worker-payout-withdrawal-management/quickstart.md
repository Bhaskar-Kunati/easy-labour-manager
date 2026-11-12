# Quickstart: Worker Payout & Bank Withdrawal Management

## Overview
This guide outlines how to validate the worker payout and withdrawal workflow in `wallet-service`.

## Prerequisites
- Java 19 and Gradle installed.
- PostgreSQL running (via Docker Compose).

## Running the Service
```powershell
cd backend-microservices
./gradlew :wallet-service:bootRun --jvm-args="-Xms128m -Xmx256m"
```

## Validation Scenarios
1. **Request Payout (Withdrawal)**:
   - `POST /api/wallet/payouts` with valid amount and destination.
   - Expected: `201 Created`, funds moved from available balance to reserved balance.
2. **List Payouts**:
   - `GET /api/wallet/payouts` with worker token.
   - Expected: `200 OK` with list of requests.
3. **Admin Payout Review & Approval**:
   - `POST /api/v1/admin/payouts/{id}/approve` with admin token.
   - Expected: `200 OK`, mock gateway called, reservation converted to completed debit ledger entry `PAYOUT_{id}`.
4. **Admin Payout Rejection**:
   - `POST /api/v1/admin/payouts/{id}/reject` with admin token.
   - Expected: `200 OK`, reserved funds released back to available balance exactly once.
