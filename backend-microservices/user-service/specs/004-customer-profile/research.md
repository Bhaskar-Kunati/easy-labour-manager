# Research: Customer Profile Management

**Feature Branch**: `004-customer-profile`

## Decision 1: Authentication & User Identification via Spring Security Context

- **Decision**: Extract the authenticated customer's mobile number/identity using `SecurityContextHolder.getContext().getAuthentication().getName()` and query `JpaUserRepository` to find the associated `User` entity, then fetch or update the `CustomerProfile` via `CustomerProfileRepository`.
- **Rationale**: Aligns precisely with existing implementation in `CustomerController.java` and `CustomerService.java`, ensuring seamless integration with the API Gateway JWT authentication filter.
- **Alternatives Considered**: Direct user ID passing in path or headers (rejected for security reasons as customers must only access their own profile).

## Decision 2: Endpoint Route Definitions

- **Decision**: Use `GET /api/v1/customers/me` and `PUT /api/v1/customers/me`.
- **Rationale**: Standard RESTful convention for accessing and modifying the currently authenticated user's own resource, already implemented and validated in `user-service`.
- **Alternatives Considered**: `/api/v1/customers/{id}` (rejected because it would require ID enumeration or admin privileges; `/me` strictly enforces self-service access).

## Decision 3: Data Persistence and Schema

- **Decision**: Reuse the existing `customer_profiles` table and `CustomerProfile` entity mapped to `users` table via OneToOne relationship.
- **Rationale**: Schema and entities are already established in `user-service` (`CustomerProfile.java`, `V2__create_customer_profiles.sql`). No new database migrations or table modifications are required.
- **Alternatives Considered**: Creating new profile attributes or tables (rejected as unnecessary and violating reuse constraints).
