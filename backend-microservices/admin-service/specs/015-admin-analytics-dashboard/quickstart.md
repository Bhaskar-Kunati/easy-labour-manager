# Quickstart: Comprehensive Admin Analytics, Financial Reconciliation & Operational Dashboard

**Feature**: 015-admin-analytics-dashboard

## Prerequisites

- Java 19
- Gradle
- PostgreSQL running locally or via Docker Compose

## Verification

1. Authenticate as an administrator with `ROLE_ADMIN` to obtain a JWT bearer token.
2. Call `GET /api/v1/admin/analytics/overview` with `Authorization: Bearer <JWT>` header.
3. Verify response contains `gmv`, `totalCapturedPayments`, `platformCommission`, and `customerWalletLiabilities` computed via `BigDecimal`.
4. Call `GET /api/v1/admin/analytics/financial-reconciliation` to verify transaction reconciliation sums.
