# Research: Coupon / Discount Management & Admin APIs

**Feature**: `013-coupon-discount-management`

## Decisions & Rationale

1. **Service Ownership**:
   - **Decision**: `booking-service` manages coupon application and validation during checkout, while administrative CRUD endpoints can be hosted in `catalog-service` or `booking-service` (hosted in `booking-service` alongside booking context for tight consistency).
   - **Rationale**: Keeps booking validation and discount calculation close to the booking creation flow, ensuring transactional consistency.

2. **Server-Side Authoritative Calculation**:
   - **Decision**: Never trust client-supplied discount amounts. Calculate percentage or fixed discounts on the server based on authoritative service base price.
   - **Rationale**: Prevents price tampering and secures payment processing (Feature 008).

3. **Usage Tracking & Concurrency**:
   - **Decision**: Persist usage in `coupon_usage` table and update global/per-customer redemption counters within a transactional scope with pessimistic/optimistic locking.
   - **Rationale**: Prevents coupon reuse beyond allowed limits and race conditions during concurrent bookings.

4. **Security & Authorization**:
   - **Decision**: Enforce `@PreAuthorize("hasRole('ADMIN')")` for management APIs and extract authenticated customer identity from `SecurityContext`.
   - **Rationale**: Aligns with existing Spring Security configuration established in Feature 012.
