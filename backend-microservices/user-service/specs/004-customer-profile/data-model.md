# Data Model: Customer Profile Management

**Feature Branch**: `004-customer-profile`

## Entities

### 1. User
- **Description**: Base authentication and identity entity.
- **Key Attributes**:
  - `id` (String / UUID): Primary key.
  - `phoneNumber` (String): Unique mobile number used for authentication.
  - `role` (UserType / String): e.g., `CUSTOMER`.
  - `status` (UserStatus / String): e.g., `ACTIVE`.
  - `isVerified` (Boolean): Mobile OTP verification status.

### 2. CustomerProfile
- **Description**: Detailed profile information for a customer.
- **Key Attributes**:
  - `id` (Long): Primary key.
  - `user` (User): OneToOne relationship to User entity (non-null, unique).
  - `fullName` (String): Customer's full name (required).
  - `profileImage` (String): URL or path to profile image (optional).
  - `email` (String): Customer email address (optional/nullable).
  - `gender` (String): Customer gender (optional/nullable).
  - `createdAt` (LocalDateTime): Timestamp when profile was created.
  - `updatedAt` (LocalDateTime): Timestamp when profile was last updated.
