# Data Model: ETA Web Application UI Redesign

**Feature**: `002-eta-ui-redesign`
**Created**: November 14, 2026

## Core Domain Entities & UI State Models

### 1. ServiceCategory
- **Description**: Represents home service offerings displayed on the homepage and catalog.
- **Attributes**:
  - `id`: string (Unique category identifier)
  - `name`: string (e.g., "AC Repair & Service", "Plumbing", "Electrical")
  - `icon`: string (Lucide icon identifier)
  - `description`: string (Brief summary of service)
  - `basePrice`: number (Optional starting price)

### 2. BookingRequest
- **Description**: Represents a service booking initiated by a customer and routed through the API Gateway.
- **Attributes**:
  - `id`: string (Booking identifier)
  - `customerId`: string
  - `serviceId`: string
  - `location`: string (Default: "Bengaluru")
  - `scheduledTime`: string/datetime
  - `status`: enum (`PENDING`, `ACCEPTED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`)
  - `paymentStatus`: enum (`PENDING`, `PAID`, `REFUNDED`)

### 3. TechnicianProfile
- **Description**: Represents a background-checked professional profile.
- **Attributes**:
  - `id`: string
  - `name`: string
  - `skills`: array of strings
  - `rating`: number (e.g., 4.9)
  - `completedJobs`: number
  - `isVerified`: boolean
  - `walletBalance`: number

### 4. PlatformStatistics
- **Description**: Verified platform metrics displayed on the trust statistics section.
- **Attributes**:
  - `happyCustomers`: string/number (e.g., "50,000+")
  - `customerRating`: string/number (e.g., "4.9 / 5.0")
  - `securePayments`: string (e.g., "100% Secure")
  - `supportAvailability`: string (e.g., "24/7 Dedicated")
