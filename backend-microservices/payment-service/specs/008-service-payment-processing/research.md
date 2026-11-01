# Research: Service Payment Processing (Feature 008)

## 1. Mock Payment Gateway Integration
- **Decision**: Use `MockPaymentGateway` implementation.
- **Rationale**: Avoids external payment gateway API credentials (Stripe/Razorpay) for local MVP development while validating the complete transaction state machine.
- **Alternatives Considered**: Integrating Razorpay/Stripe (rejected per MVP scope constraints and lack of sandbox credentials).

## 2. Authentication & Security Context
- **Decision**: Extract customer ID from Spring Security's authenticated `Principal` / `Jwt` / `SecurityContextHolder`.
- **Rationale**: Ensures security compliance; client cannot forge or inject arbitrary customer IDs.
- **Alternatives Considered**: Accepting `customerId` in request payload (rejected due to severe security vulnerability).

## 3. Kafka Event Integration
- **Decision**: Publish `PAYMENT_COMPLETED` and `PAYMENT_FAILED` events using `PaymentEvent` schema from `common-lib` to `payment-events` topic.
- **Rationale**: Reuses existing event infrastructure and aligns with `booking-service` consumer expectations.
- **Alternatives Considered**: Creating new event schema (rejected to maintain architectural consistency).

## 4. Database Persistence
- **Decision**: Use existing `payments` and `payment_transactions` PostgreSQL tables managed via Flyway migration `V1__init_payment_schema.sql`.
- **Rationale**: Schema is already fully scaffolded and matches database design requirements.
- **Alternatives Considered**: Creating a new database or altering schema (rejected as unnecessary).
