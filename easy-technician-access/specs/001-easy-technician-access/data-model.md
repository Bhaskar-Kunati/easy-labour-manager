# Data Model: EasyTechnicianAccess (ETA)

**Feature Branch**: `001-easy-technician-access`

**Created**: 2026-11-11

## Core Domain Entities

### 1. User & Auth Entity
- **Attributes**:
  - `id`: UUID / Long (Unique identifier)
  - `email`: String (User email address)
  - `phoneNumber`: String (Mobile number for OTP verification)
  - `role`: Enum (`CUSTOMER`, `TECHNICIAN`, `ADMIN`)
  - `token`: String (JWT authentication token)

### 2. Customer Profile Entity
- **Attributes**:
  - `customerId`: Long (Linked to User ID)
  - `fullName`: String
  - `addresses`: List<Address>
  - `createdAt`: Timestamp

### 3. Technician Profile Entity
- **Attributes**:
  - `technicianId`: Long (Linked to User ID)
  - `fullName`: String
  - `skills`: List<String>
  - `availabilityStatus`: Enum (`AVAILABLE`, `BUSY`, `OFFLINE`)
  - `rating`: Double

### 4. Service Catalog Entity
- **Attributes**:
  - `serviceId`: Long
  - `title`: String
  - `description`: String
  - `category`: String
  - `price`: BigDecimal
  - `estimatedDuration`: String

### 5. Booking Entity
- **Attributes**:
  - `bookingId`: Long
  - `customerId`: Long
  - `technicianId`: Long (nullable before assignment)
  - `serviceId`: Long
  - `status`: Enum (`PENDING`, `ACCEPTED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`)
  - `scheduledDate`: Timestamp
  - `totalAmount`: BigDecimal

### 6. Wallet & Payment Entity
- **Attributes**:
  - `walletId`: Long
  - `technicianId`: Long
  - `balance`: BigDecimal
  - `transactions`: List<Transaction>
