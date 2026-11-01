# Research & Technical Decisions: Customer Registration

## Decisions & Rationale

1. **Password Hashing & Security**:
   - **Decision**: Use Spring Security's `PasswordEncoder` to hash passwords prior to creating the `User` entity.
   - **Rationale**: Complies strictly with FR-004; prevents plaintext storage, logging, or response exposure.

2. **Duplicate Mobile Detection**:
   - **Decision**: Query `UserRepository.findByMobileNumber()` prior to saving and catch unique constraint violations or data integrity exceptions.
   - **Rationale**: Ensures duplicate mobile numbers are rejected with HTTP 409 Conflict (FR-003, FR-006).

3. **Role & Initial Status Assignment**:
   - **Decision**: Explicitly set `UserRole.CUSTOMER` and `UserStatus.ACTIVE` upon registration.
   - **Rationale**: Satisfies FR-005.

4. **Deterministic Error Handling**:
   - **Decision**: Map input validation failures to HTTP 400, duplicate registrations to HTTP 409, unexpected failures to HTTP 500, and database outages to HTTP 503.
   - **Rationale**: Satisfies deterministic error requirements in FR-006.
