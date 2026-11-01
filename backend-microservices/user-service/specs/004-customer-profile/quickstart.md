# Quickstart Validation Guide: Customer Profile Management

**Feature Branch**: `004-customer-profile`

## Prerequisites
- PostgreSQL running and accessible.
- `user-service` running (or test context configured).
- A valid JWT token for a registered customer obtained via login or test setup.

## Validation Scenarios

### 1. View Customer Profile (`GET /api/v1/customers/me`)
- **Command / Request**:
  ```http
  GET /api/v1/customers/me
  Authorization: Bearer <VALID_JWT_TOKEN>
  ```
- **Expected Outcome**:
  - HTTP 200 OK
  - JSON body containing customer profile (`fullName`, `email`, `gender`, `profileImage`, timestamps).

### 2. Update Customer Profile (`PUT /api/v1/customers/me`)
- **Command / Request**:
  ```http
  PUT /api/v1/customers/me
  Authorization: Bearer <VALID_JWT_TOKEN>
  Content-Type: application/json

  {
    "fullName": "Updated Customer Name",
    "email": "customer@example.com",
    "gender": "Male"
  }
  ```
- **Expected Outcome**:
  - HTTP 200 OK
  - JSON body showing updated `fullName`, `email`, and `gender`.

### 3. Unauthorized Access Rejection
- **Command / Request**:
  ```http
  GET /api/v1/customers/me
  ```
- **Expected Outcome**:
  - HTTP 401 Unauthorized.
