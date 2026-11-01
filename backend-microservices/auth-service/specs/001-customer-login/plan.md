# Implementation Plan: Customer Login with Mobile, Password, and JWT

**Feature Branch**: `001-customer-login`
**Component**: `auth-service`
**Status**: Approved

## Technical Context
- **Language / Framework**: Java 19, Spring Boot 3.2.0, Spring Security, Spring Data JPA
- **Database**: PostgreSQL (`el_db` on port 5433) via Spring Data JPA and Flyway
- **Authentication**: Spring Security, `PasswordEncoder`, and existing `JwtService` (jjwt 0.11.5 with HS256)
- **Testing**: JUnit 5, Mockito, Spring Boot Test

## Constitution Check
- Adheres to Java 19 and Spring Boot 3.2 conventions.
- Preserves microservice isolation (`auth-service` handles authentication independently).
- Reuses existing `JwtService`, `UserRepository`, `User` entity, and `common-lib` DTOs/enums (`UserRole`, `UserStatus`).

## Phase 0: Research & Investigation (`research.md`)
- Decision: Reuse existing `JwtService` and `PasswordEncoder` implementations in `auth-service`.
- Decision: Extend existing `AuthController.login` endpoint to fully enforce account status validation (`ACTIVE`, `SUSPENDED`, `BLOCKED`) and robust error handling.

## Phase 1: Design & Contracts
- **Data Model (`data-model.md`)**: Existing `User` entity (`id` as String UUID, `mobileNumber`, `passwordHash`, `status`, `role`).
- **API Contracts (`contracts/auth-api.yaml`)**: Actual endpoint `/api/v1/auth/login` (POST).
- **Quickstart Validation (`quickstart.md`)**: End-to-end verification steps using Java 19 and Gradle wrapper on port 8081.
