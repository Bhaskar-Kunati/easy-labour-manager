# Data Model: Customer Service Booking and Scheduling

**Feature Branch**: `006-customer-service-booking`

## Entities

### 1. Booking
- **Description**: Represents a customer service booking.
- **Key Attributes**:
  - `id` (Long): Primary key.
  - `customerId` (String): ID of the customer who created the booking (extracted from JWT / security context).
  - `serviceId` (Long): ID of the service from the catalog.
  - `serviceName` (String): Snapshot of service name at booking time.
  - `bookedPrice` (BigDecimal): Snapshot of price at booking time.
  - `pricingType` (String / PricingType): e.g., `FIXED`, `HOURLY`.
  - `serviceDateTime` (LocalDateTime): Scheduled future date and time for service execution.
  - `location` (String): Service location address supplied per booking request.
  - `status` (BookingStatus): `PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED`.
  - `idempotencyKey` (String): Unique key for idempotency protection on creation.
  - `createdAt` (LocalDateTime): Timestamp when booking was created.
  - `updatedAt` (LocalDateTime): Timestamp when booking was last updated.
