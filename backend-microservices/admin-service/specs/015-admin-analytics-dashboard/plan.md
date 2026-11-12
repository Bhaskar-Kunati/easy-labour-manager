# Implementation Plan: Comprehensive Admin Analytics, Financial Reconciliation & Operational Dashboard

**Branch**: `015-admin-analytics-dashboard` | **Component**: `admin-service` | **Spec**: [specs/015-admin-analytics-dashboard/spec.md](spec.md)

## Technical Context

- **Language & Framework**: Java 19, Spring Boot 3.2.
- **Architecture**: Dedicated microservice (`admin-service`) communicating securely with `booking-service`, `payment-service`, and `wallet-service` via internal REST APIs secured with `X-Internal-Service-Key`.
- **Database**: PostgreSQL (for potential admin audit logs/cache; primary data owned by respective microservices).
- **Security**: Spring Security JWT RBAC (`ROLE_ADMIN`) for external access; `X-Internal-Service-Key` for internal inter-service requests.
- **Monetary Calculations**: `BigDecimal` with explicit currency (`INR`).

## Constitution Check

- **Modularity & Boundaries**: Compliant. `admin-service` aggregates data via internal REST contracts without direct database access to other microservices.
- **Security Mandates**: Compliant. Strict external JWT `ROLE_ADMIN` check and internal service authentication (`X-Internal-Service-Key`).
- **Data Integrity**: Compliant. Uses `BigDecimal` for all financial metrics.

## Proposed Structure

```text
backend-microservices/admin-service/
├── build.gradle
├── Dockerfile
└── src/main/java/com/easylabour/adminservice/
    ├── AdminServiceApplication.java
    ├── application/service/AdminAnalyticsService.java
    ├── infrastructure/client/
    │   ├── BookingClient.java
    │   ├── PaymentClient.java
    │   └── WalletClient.java
    ├── infrastructure/security/SecurityConfig.java
    └── web/controller/AdminAnalyticsController.java
```

## Phases

- **Phase 0**: Research & Technology Selection (`research.md`).
- **Phase 1**: Design & Contracts (`data-model.md`, `contracts/rest-api.yaml`, `quickstart.md`).
- **Phase 2**: Implementation Tasks (`tasks.md`).
