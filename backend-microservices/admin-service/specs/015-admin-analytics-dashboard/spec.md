# Specification: Comprehensive Admin Analytics, Financial Reconciliation & Operational Dashboard

**Feature Branch**: `015-admin-analytics-dashboard`  
**Component**: `admin-service`  

## Overview

The Easy Labour Manager platform requires comprehensive operational analytics, platform overview metrics, and cross-domain financial reconciliation. Feature 015 introduces a dedicated backend `admin-service` to securely aggregate data from `booking-service`, `payment-service`, and `wallet-service` via internal reporting APIs without violating service-to-service data boundaries or direct database access rules.

## Architecture Decisions

- **Dedicated Service**: Introduce a new Spring Boot microservice named `admin-service`.
- **Data Ownership**: `booking-service`, `payment-service`, and `wallet-service` remain the exclusive owners of their respective business data.
- **Secure Internal Communication**: `admin-service` obtains reporting data through secure internal reporting APIs protected by `X-Internal-Service-Key`.
- **No Direct Database Access**: Microservices never access another service's database directly.
- **External Security**: External admin APIs require an authenticated JWT bearing `ROLE_ADMIN`.
- **Backend-Only**: This feature is backend-only; any dashboard UI is explicitly out of scope.

## Functional Scope

### 1. Platform Overview Analytics
- Gross Merchandise Value (GMV).
- Successfully captured payments.
- Active bookings.
- Completed bookings.
- Platform commission.
- Worker earnings.
- Customer wallet liabilities.

### 2. Financial Analytics & Reconciliation
- Wallet top-ups.
- Worker earnings credits.
- Wallet refund credits.
- Original payment method refunds.
- Financial reconciliation summaries matching bookings, payments, refunds, and wallet ledger credits.
- Separate reporting of pending, processing, retry-pending, and unknown refund/outbox states.

### 3. Booking Analytics
- Booking counts grouped by status.
- Completed bookings count.
- Booking trends over a requested date range.

### 4. Reconciliation Matching Rules
- Booking-to-Payment matching.
- Payment-to-Refund matching.
- Wallet Refund-to-Wallet Transaction matching.
- Booking Worker Earnings-to-Wallet Credit matching.
- Identification of missing, duplicate, and mismatched transactions.

## Financial Rules

- **GMV**: Sum of completed booking `total_amount`.
- **Platform Commission**: Sum of completed booking `platform_fee`.
- **Captured Payments**: Sum of verified successful captured payments (`SUCCESS`).
- **Worker Earnings**: Derived from booking worker assignment earnings and reconciled against wallet ledger credits (`BOOKING_EARNINGS_{bookingId}`).
- **Wallet Top-Ups**: CREDIT transactions with `PAYMENT_` references matched to payment records.
- **Wallet Refunds**: CREDIT transactions with `REFUND_` references matched to completed wallet-destination refunds.
- **Original Payment Refunds**: Completed `ORIGINAL_PAYMENT_METHOD` refunds.
- **Wallet Liabilities**: Sum of active INR wallet balances.
- Handling of partial payments, multiple payments, partial refunds, and multiple worker assignments as supported by underlying data models.

## External API Contracts

- `GET /api/v1/admin/analytics/overview` (Supports optional date range query parameters `startDate`, `endDate`)
- `GET /api/v1/admin/analytics/financial-reconciliation` (Supports optional date range and pagination parameters)
- `GET /api/v1/admin/analytics/bookings-summary` (Supports date range filtering)

## Non-Functional Requirements

- **ADMIN-Only Access**: Enforced via Spring Security JWT RBAC (`ROLE_ADMIN`).
- **Secure Internal Communication**: Authenticated via `X-Internal-Service-Key`.
- **Read-Only Reporting**: No alterations to financial transactions or business entities.
- **Precision Calculation**: Use `BigDecimal` for all monetary computations.
- **Explicit Currency Handling**: Default to INR with explicit currency serialization.
- **Performance**: Efficient indexed aggregation queries and pagination for detailed reconciliation records.
- **Resilience**: Clear error handling when underlying services are unavailable (never silently interpret unavailable data as zero; return explicit service degradation or error status).
- **Transparency**: Clearly identify reporting timestamps and data freshness indicators in responses.

## Out of Scope

- Dashboard frontend user interfaces.
- External bank payout transfers.
- Customer support and dispute ticketing workflows.
- Payment gateway integration modifications.
- Manual wallet balance adjustments.
- Changes to existing booking, payment, or wallet financial transaction workflows.

## Testable Scenarios

1. **Partial Payments & Multiple Workers**: Verify analytics correctly aggregates metrics when a booking has multiple payments or multiple assigned workers.
2. **Partial Refunds & Duplicate Transactions**: Verify financial reconciliation correctly identifies partial refunds and guards against duplicate transaction ledger entries.
3. **Unavailable Downstream Services**: Verify `admin-service` returns appropriate error status when `booking-service`, `payment-service`, or `wallet-service` is unreachable, without reporting zero falsely.
