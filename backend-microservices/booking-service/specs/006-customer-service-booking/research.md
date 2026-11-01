# Research: Customer Service Booking and Scheduling

**Feature Branch**: `006-customer-service-booking`

## Decision 1: Idempotency Key Handling for `POST /api/v1/bookings`

- **Decision**: Store a unique `idempotencyKey` field in the `bookings` table with a unique constraint (or check repository for existing key per customer/request). When a request arrives with an idempotency key, query the database; if a booking with that key exists with identical payload parameters, return HTTP 200 OK with the existing booking; if the payload differs, return HTTP 409 Conflict.
- **Rationale**: Ensures robust retry protection without duplicate bookings being created.
- **Alternatives Considered**: In-memory cache (rejected because server restarts or multi-instance deployments require persistent database-backed idempotency).

## Decision 2: Pricing Snapshot & Catalog Verification

- **Decision**: When creating a booking (`POST /api/v1/bookings`), `booking-service` calls `catalog-service` (or queries shared catalog tables/DTOs) to verify that `serviceId` exists and is active, and captures `serviceName` and `basePrice` as snapshot attributes (`bookedPrice`, `serviceName`) directly inside the `Booking` entity.
- **Rationale**: Preserves historical pricing at booking time so subsequent catalog changes do not affect existing bookings, adhering strictly to specification rules without building a separate pricing system.
- **Alternatives Considered**: Dynamic price recalculation (rejected per pricing snapshot requirements).

## Decision 3: Kafka Event Dispatching

- **Decision**: Publish `BOOKING_CREATED` and `BOOKING_CANCELLED` events via Spring Kafka `KafkaTemplate` upon successful database transactions (using `@TransactionalEventListener(TransactionPhase.AFTER_COMMIT)` or direct transactional publishing).
- **Rationale**: Reuses existing event-driven architecture and Kafka infrastructure configured across backend microservices.
- **Alternatives Considered**: Outbox pattern (can be added later, but direct post-commit publishing fits current MVP architecture).
