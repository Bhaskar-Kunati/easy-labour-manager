# Component Map & Routing Guidelines

## Purpose
This document defines the architectural components of the Easy Labour Manager application, their paths, specification directories, and routing keywords used by the centralized Spec Kit routing system (`resolve-component.ps1`).

---

## Supported Components

1. **Android Application (`android-app`)**
   - **Path**: `android-app`
   - **Specs Path**: `android-app/specs`
   - **Keywords**: Android, Jetpack Compose, Compose, screen, UI, ViewModel, navigation, Android navigation, Retrofit, Hilt, Android storage, mobile UI, customer screen, worker screen, labour screen.

2. **API Gateway (`api-gateway`)**
   - **Path**: `backend-microservices/api-gateway`
   - **Specs Path**: `backend-microservices/api-gateway/specs`
   - **Keywords**: API gateway, gateway, route, routing, gateway filter, gateway configuration, request forwarding.

3. **Auth Service (`auth-service`)**
   - **Path**: `backend-microservices/auth-service`
   - **Specs Path**: `backend-microservices/auth-service/specs`
   - **Keywords**: login, authentication, authorization, JWT, OAuth2, credentials, password, OTP, token, session, access token, refresh token, authentication flow.

4. **User Service (`user-service`)**
   - **Path**: `backend-microservices/user-service`
   - **Specs Path**: `backend-microservices/user-service/specs`
   - **Keywords**: user profile, customer profile, worker profile, labour profile, user status, customer details, worker details, user role, user management.

5. **Booking Service (`booking-service`)**
   - **Path**: `backend-microservices/booking-service`
   - **Specs Path**: `backend-microservices/booking-service/specs`
   - **Keywords**: booking, appointment, scheduling, rescheduling, cancellation, booking lifecycle, labour assignment, worker assignment, dispatch.

6. **Catalog Service (`catalog-service`)**
   - **Path**: `backend-microservices/catalog-service`
   - **Specs Path**: `backend-microservices/catalog-service/specs`
   - **Keywords**: service catalog, service category, service listing, labour offering, service discovery, service search, category.

7. **Payment Service (`payment-service`)**
   - **Path**: `backend-microservices/payment-service`
   - **Specs Path**: `backend-microservices/payment-service/specs`
   - **Keywords**: payment, transaction, billing, invoice, payment status, payment processing, pricing, refund.

8. **Review Service (`review-service`)**
   - **Path**: `backend-microservices/review-service`
   - **Specs Path**: `backend-microservices/review-service/specs`
   - **Keywords**: review, rating, feedback, customer review, worker rating.

9. **Tracking Service (`tracking-service`)**
   - **Path**: `backend-microservices/tracking-service`
   - **Specs Path**: `backend-microservices/tracking-service/specs`
   - **Keywords**: tracking, live location, GPS, coordinates, location tracking, map, worker location, real-time location.

10. **Notification Service (`notification-service`)**
    - **Path**: `backend-microservices/notification-service`
    - **Specs Path**: `backend-microservices/notification-service/specs`
    - **Keywords**: notification, push notification, SMS, alert, in-app notification, messaging notification, Kafka notification event.

11. **Common Library (`common-lib`)**
    - **Path**: `backend-microservices/common-lib`
    - **Specs Path**: `backend-microservices/common-lib/specs`
    - **Keywords**: shared DTO, shared model, common DTO, common exception, shared exception, Kafka event contract, event contract, shared enum, shared utility, common library.

12. **Admin Service (`admin-service`)**
    - **Path**: `backend-microservices/admin-service`
    - **Specs Path**: `backend-microservices/admin-service/specs`
    - **Keywords**: admin, dashboard, analytics, administration, reporting, platform metrics, financial reconciliation, operational dashboard, admin dashboard, platform analytics.

13. **Wallet Service (`wallet-service`)**
    - **Path**: `backend-microservices/wallet-service`
    - **Specs Path**: `backend-microservices/wallet-service/specs`
    - **Keywords**: wallet, wallet balance, wallet transaction, wallet history, transaction history, credit, debit, withdrawal, wallet statement, payout, payout destination, withdrawal request, digital wallet, wallet funds.

---

## Routing Principles

1. **Explicit Component Precedence**: Explicit mentions of a component name or path (e.g., "in auth-service", "Android screen", "update common-lib") take absolute priority.
2. **Single-Component Routing**: If a requirement clearly matches keywords for exactly one component, route automatically with status `RESOLVED`.
3. **Ambiguity Handling**: If no component matches or confidence is insufficient, return status `AMBIGUOUS` and prompt the user for clarification. Do not guess.
4. **Cross-Component Handling**: If a requirement affects multiple components, return status `CROSS_COMPONENT` listing all affected components and require the user to select the primary owning component.
5. **Reference-Only Baseline (`current-state.md`)**: `current-state.md` files in component specs directories serve as reference and baseline documentation only. Routing workflows must never modify them automatically.
