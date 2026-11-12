# Data Model: Comprehensive Admin Analytics, Financial Reconciliation & Operational Dashboard

**Feature**: 015-admin-analytics-dashboard

## Aggregated DTOs & Response Models

- **OverviewResponse**:
  - `gmv` (BigDecimal)
  - `totalCapturedPayments` (BigDecimal)
  - `activeBookings` (long)
  - `completedBookings` (long)
  - `platformCommission` (BigDecimal)
  - `workerEarnings` (BigDecimal)
  - `customerWalletLiabilities` (BigDecimal)
  - `dataFreshnessTimestamp` (LocalDateTime)

- **FinancialReconciliationResponse**:
  - `totalPaymentsCaptured` (BigDecimal)
  - `totalWalletTopups` (BigDecimal)
  - `totalWorkerPayouts` (BigDecimal)
  - `totalRefundsWallet` (BigDecimal)
  - `totalRefundsGateway` (BigDecimal)
  - `discrepancyAmount` (BigDecimal)
  - `pendingTransactionsCount` (long)
  - `unknownTransactionsCount` (long)
  - `dataFreshnessTimestamp` (LocalDateTime)

- **BookingsSummaryResponse**:
  - `statusCounts` (Map<String, Long>)
  - `completedBookings` (long)
  - `totalRevenue` (BigDecimal)
  - `dataFreshnessTimestamp` (LocalDateTime)
