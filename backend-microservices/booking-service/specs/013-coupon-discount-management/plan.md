# Implementation Plan: Coupon / Discount Management & Admin APIs

**Feature**: `013-coupon-discount-management`

## Technical Context
- **Language/Framework**: Java 19, Spring Boot 3.2, Spring Security, Spring Data JPA, PostgreSQL.
- **Service Ownership**: `booking-service` owns coupon validation, checkout application, usage recording, and admin coupon CRUD APIs.
- **Authentication**: JWT-based authentication with `SecurityContext` extraction and method security (`@PreAuthorize("hasRole('ADMIN')")`).

## Architecture & Design Decisions
1. **Admin Management**: Hosted in `booking-service` protected by `ROLE_ADMIN`.
2. **Checkout Integration**: Applied during booking creation (`POST /api/v1/bookings`) with authoritative server-side discount calculation.
3. **Usage Tracking**: Persisted via `CouponUsage` entity respecting global and per-customer usage limits.

## Database Impact
- Reuses existing `coupons` and `coupon_usage` tables/models from `DESIGN.md`. No destructive migrations required.

## Security Requirements
- ADMIN-only management APIs (401/403 enforced).
- Server-side validation and discount calculation.
- Customer identity extracted exclusively from `SecurityContext`.
