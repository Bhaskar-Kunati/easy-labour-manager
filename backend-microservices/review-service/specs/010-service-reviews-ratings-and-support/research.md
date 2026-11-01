# Research: Service Reviews, Ratings & Customer Support

**Branch**: `010-service-reviews-ratings-and-support` | **Date**: October 5, 2026

## 1. Review Eligibility & Booking Status Validation
- **Decision**: Validate booking status via Feign client or shared event store / booking service integration, ensuring reviews are only accepted for `COMPLETED` bookings.
- **Rationale**: Prevents premature or invalid reviews on `PENDING`, `CONFIRMED`, or `CANCELLED` bookings.
- **Alternatives Considered**: Direct database query across microservice boundaries (rejected to preserve microservice autonomy and loose coupling).

## 2. Duplicate Review Prevention
- **Decision**: Unique database constraint on `booking_id` in the `reviews` table combined with service-layer validation.
- **Rationale**: Guarantees that a booking can only receive one review, rejecting duplicate submissions with HTTP 400/409.
- **Alternatives Considered**: In-memory tracking (rejected due to persistence across restarts).

## 3. Rating Aggregation
- **Decision**: JPA repository query methods using aggregate functions (e.g., `AVG(rating)`, `COUNT(*)`) grouped by worker ID or service ID.
- **Rationale**: Efficient, native SQL calculation of average ratings and total review counts without application-level memory overhead.
- **Alternatives Considered**: Materialized summary table (overkill for MVP scale).

## 4. Support Ticketing Privacy & Security
- **Decision**: Extract authenticated customer ID from `SecurityContextHolder.getContext().getAuthentication().getName()` and enforce repository queries filtered by `customer_id`.
- **Rationale**: Ensures complete data isolation between customers.
- **Alternatives Considered**: Trusting client-supplied parameters (rejected due to security risks).
