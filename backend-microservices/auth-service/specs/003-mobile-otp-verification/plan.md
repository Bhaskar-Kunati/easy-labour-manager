# Implementation Plan: Production-Grade Customer Mobile OTP Verification

**Feature Branch**: `003-mobile-otp-verification`
**Component**: `auth-service`
**Status**: Approved

## Technical Context
- **Language / Framework**: Java 19, Spring Boot 3.2.0, Spring Data JPA
- **Database**: PostgreSQL (`el_db` on port 5433) via Spring Data JPA and Flyway migration V1 (`users` table, reusing `isVerified` column)
- **Architecture**: Reuses existing `AuthController`, `UserRepository`, `User`, `OtpProvider`, `MockOtpProvider`, and security/exception handling.
- **Testing**: JUnit 5, Mockito, Spring Boot Test

## Constitution Check
- Adheres to Java 19 and Spring Boot 3.2.0 conventions.
- Reuses existing `OtpProvider` abstraction and `User.isVerified` field without introducing new database tables or columns.
- Enforces deterministic HTTP status codes (200, 400, 503) and security requirements (no plaintext OTP storage or exposure).

## Phase 0: Research & Investigation (`research.md`)
- Decision: Use a thread-safe in-memory OTP session store (or map in `AuthController` / service layer) tied to `OtpProvider` to manage 6-digit OTP codes, generation timestamps, and 5-minute expiration tracking.
- Decision: Reuse `MockOtpProvider` for dispatching OTPs.
- Decision: Update `User.isVerified` to `true` upon successful OTP verification.

## Phase 1: Design & Contracts
- **Data Model (`data-model.md`)**: Reuses existing `User` entity (`isVerified` field) and defines transient OTP session state.
- **API Contracts (`contracts/otp-api.yaml`)**: OpenAPI specification for `POST /api/v1/auth/send-otp` and `POST /api/v1/auth/verify-otp`.
- **Quickstart Validation (`quickstart.md`)**: End-to-end verification steps using Java 19 and Gradle wrapper on port 8081.
