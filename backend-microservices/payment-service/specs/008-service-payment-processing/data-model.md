# Data Model: Service Payment Processing (Feature 008)

## Entities

### 1. Payment (`payments` table)
Represents a payment order and status associated with a booking.
- **id**: `BIGSERIAL` (Primary Key)
- **booking_id**: `BIGINT` (Not Null) - References booking service booking ID.
- **customer_id**: `VARCHAR(255)` (Not Null) - Derived from authenticated security context.
- **payment_gateway**: `VARCHAR(50)` (Not Null) - e.g., `MOCK`.
- **gateway_order_id**: `VARCHAR(255)` - Gateway-specific order identifier.
- **gateway_payment_id**: `VARCHAR(255)` - Gateway-specific payment/transaction identifier.
- **amount**: `DECIMAL(19, 2)` (Not Null) - Payment amount matching booking price.
- **currency**: `VARCHAR(3)` (Not Null) - e.g., `INR` or `USD`.
- **status**: `VARCHAR(50)` (Not Null) - `PENDING`, `SUCCESS`, `FAILED`.
- **payment_method**: `VARCHAR(50)` - e.g., `CARD`, `UPI`, `WALLET`.
- **gateway_response**: `TEXT` - Raw or summarized response JSON from gateway.
- **created_at**: `TIMESTAMP` (Default CURRENT_TIMESTAMP)
- **updated_at**: `TIMESTAMP`

### 2. PaymentTransaction (`payment_transactions` table)
Represents individual transaction attempts or verification records for a payment.
- **id**: `BIGSERIAL` (Primary Key)
- **payment_id**: `BIGINT` (Foreign Key -> `payments.id`)
- **transaction_type**: `VARCHAR(50)` (Not Null) - e.g., `ORDER_CREATION`, `VERIFICATION`.
- **amount**: `DECIMAL(19, 2)` (Not Null)
- **status**: `VARCHAR(50)` (Not Null) - `SUCCESS`, `FAILED`.
- **gateway_response**: `TEXT`
- **created_at**: `TIMESTAMP`

## State Transitions
- `PENDING` (Initial state upon order creation) -> `SUCCESS` (Upon successful gateway verification/processing)
- `PENDING` (Initial state) -> `FAILED` (Upon gateway processing error or verification failure)
