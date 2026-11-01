# Research & Technical Decisions: Mobile OTP Verification

## Decisions & Rationale

1. **OTP Generation & Lifecycle**:
   - **Decision**: Generate 6-digit random numeric OTP codes with an explicit 5-minute expiration window.
   - **Rationale**: Complies with FR-005 and industry standard OTP security practices.

2. **OTP Storage & Security**:
   - **Decision**: Store active OTP codes and timestamps in a transient thread-safe service map linked to mobile numbers; never store plaintext OTPs in the `User` entity or database columns.
   - **Rationale**: Complies with FR-004 and security requirements prohibiting plaintext storage.

3. **Provider Abstraction Reuse**:
   - **Decision**: Reuse existing `OtpProvider` interface and `MockOtpProvider` implementation.
   - **Rationale**: Allows seamless future replacement with a real SMS gateway without changing controller contracts.

4. **Deterministic Error Handling**:
   - **Decision**: Return HTTP 400 Bad Request for malformed input, invalid OTP, expired OTP, and unknown mobile numbers (preventing user-enumeration), and HTTP 503 for provider/infrastructure failures.
   - **Rationale**: Complies with FR-006 and user-enumeration protection rules.
