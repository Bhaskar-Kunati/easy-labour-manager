# Quickstart Validation Guide: Service Payment Processing (Feature 008)

## Prerequisites
1. Infrastructure running (PostgreSQL, MongoDB, Kafka) via Docker Compose (`devops/docker`).
2. `auth-service` and `booking-service` running or available.
3. `payment-service` built and running on port 8085.

## Validation Scenarios

### 1. Initiate Payment Order
- **Endpoint**: `POST /api/v1/payments/create-order`
- **Headers**: `Authorization: Bearer <JWT_TOKEN>`, `Content-Type: application/json`
- **Payload**:
  ```json
  {
    "bookingId": 1,
    "amount": 500.00,
    "currency": "INR",
    "paymentMethod": "CARD"
  }
  ```
- **Expected Outcome**: HTTP 200 OK, returns payment order details with status `PENDING` and a generated gateway order ID.

### 2. Verify Payment
- **Endpoint**: `POST /api/v1/payments/verify`
- **Headers**: `Authorization: Bearer <JWT_TOKEN>`, `Content-Type: application/json`
- **Payload**:
  ```json
  {
    "paymentId": "1",
    "gatewayPaymentId": "mock_pay_123"
  }
  ```
- **Expected Outcome**: HTTP 200 OK returning `true`, updates payment status to `SUCCESS`, publishes `PAYMENT_COMPLETED` event to Kafka `payment-events`.

### 3. View Payment Status
- **Endpoint**: `GET /api/v1/payments/status/{bookingId}`
- **Headers**: `Authorization: Bearer <JWT_TOKEN>`
- **Expected Outcome**: HTTP 200 OK returning payment status (`SUCCESS` or `FAILED`).
