# Research & Technical Decisions: Customer Login

## Decisions & Rationale

1. **Authentication Mechanism & Existing Code Reuse**:
   - **Decision**: Reuse existing `JwtService` (`jjwt 0.11.5` with HS256), `PasswordEncoder`, and `UserRepository` in `auth-service`.
   - **Rationale**: The infrastructure is already implemented in `auth-service` (`JwtService.java`, `AuthController.java`, `User.java`), ensuring consistency without introducing duplicate libraries.

2. **Account Status Enforcement**:
   - **Decision**: Verify `UserStatus` (`ACTIVE`, `SUSPENDED`, `BLOCKED`) during login verification in `AuthController`.
   - **Rationale**: Ensures suspended or blocked users are denied authentication with an HTTP 401 response.

3. **Error Handling & Resilience**:
   - **Decision**: Return HTTP 401 for invalid credentials/inactive status and HTTP 503 for data store failure without exposing internal stack traces.
   - **Rationale**: Aligns with project error conventions and security standards.
