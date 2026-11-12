# Research: Comprehensive Admin Analytics, Financial Reconciliation & Operational Dashboard

**Feature**: 015-admin-analytics-dashboard

## Decisions & Rationale

1. **Service Aggregation Pattern**:
   - *Decision*: Use synchronous internal REST clients (`RestTemplate`) with `X-Internal-Service-Key` header authentication to query reporting aggregations from booking, payment, and wallet services.
   - *Rationale*: Avoids complex event-sourcing / CQRS infrastructure synchronization while preserving strict microservice data boundaries and preventing direct database sharing.

2. **Monetary Precision**:
   - *Decision*: Utilize `BigDecimal` for all financial metrics, sums, and reconciliation calculations.
   - *Rationale*: Prevents floating-point rounding errors in GMV, commission, and wallet liability sums.

3. **Security Model**:
   - *Decision*: Enforce Spring Security JWT authentication requiring `ROLE_ADMIN` on external admin endpoints, and require `X-Internal-Service-Key` on internal aggregation endpoints.
   - *Rationale*: Aligns perfectly with project security standards established in Features 001 and 014.
