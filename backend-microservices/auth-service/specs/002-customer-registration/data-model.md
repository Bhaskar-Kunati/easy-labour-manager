# Data Model: Customer Registration (`auth-service`)

## Entities

### User Entity (`com.easylabour.authservice.domain.model.User`)
- **Table**: `users` (PostgreSQL `el_db`)
- **Fields**:
  - `id`: `String` (UUID generated via `@GeneratedValue(strategy = GenerationType.UUID)`)
  - `mobileNumber`: `String` (Unique, Not Null)
  - `passwordHash`: `String` (Not Null, hashed using Spring Security `PasswordEncoder`)
  - `role`: `UserRole` Enum (`CUSTOMER`)
  - `status`: `UserStatus` Enum (`ACTIVE`)
  - `isVerified`: `boolean`
  - `createdAt`: `LocalDateTime`
  - `updatedAt`: `LocalDateTime`

### Registration Request DTO (Request Body)
- `mobileNumber`: `String` (Required, validated against supported mobile format)
- `password`: `String` (Required, never stored in plaintext)

### Registration Response DTO
- Success message representation (excluding password and password hash).
