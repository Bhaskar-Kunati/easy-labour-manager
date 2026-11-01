# Quickstart: User Wallet & Transaction Management

## Overview
This guide outlines how to validate the `wallet-service` implementation locally.

## Prerequisites
- Java 19 and Gradle installed.
- PostgreSQL running (via Docker Compose).

## Running the Service
```powershell
cd backend-microservices
./gradlew :wallet-service:bootRun --jvm-args="-Xms128m -Xmx256m"
```

## Validation Scenarios
1. **Get Wallet Balance**:
   - `GET /api/wallet/balance` with Authorization Bearer token.
   - Expected: `200 OK` with balance and currency.
2. **Top-up Wallet**:
   - `POST /api/wallet/topup` with `amount` and `referenceId`.
   - Expected: `200 OK`, balance incremented, CREDIT transaction recorded.
3. **Wallet Debit (Booking Payment)**:
   - `POST /api/wallet/debit` (Internal/Service API) with `userId`, `amount`, `referenceId`.
   - Expected: `200 OK`, balance decremented atomically, DEBIT transaction recorded.
