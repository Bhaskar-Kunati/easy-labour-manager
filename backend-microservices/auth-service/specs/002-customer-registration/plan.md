# Implementation Plan: Customer Registration in Auth Service

**Feature Branch**: `002-customer-registration`
**Component**: `auth-service`
**Status**: Approved

## Technical Context
- **Language / Framework**: Java 19, Spring Boot 3.2.0, Spring Security (`PasswordEncoder`), Spring Data JPA
- **Database**: PostgreSQL (`el_db` on port 5433) via Spring Data JPA and Flyway migration V1 (`users` table)
- **Architecture**: Reuses existing `User` entity, `UserRepository`, `PasswordEncoder`, and existing `AuthController` registration endpoint (`POST /api/v1/auth/register`)
- **Testing**: JUnit 5, Mockito, Spring Boot Test

## Constitution Check
- Adheres to Java 19 and Spring Boot 3.2.0 conventions.
- Reuses existing `User` entity, repository, and password encoder.
- Enforces deterministic HTTP status codes (400, 409, 500, 503) and password security requirements (FR-004).

## Phase 0: Research & Investigation (`research.md`)
- Decision: Reuse existing `User` entity (`mobileNumber`, `passwordHash`, `role`, `status`) and `UserRepository`.
- Decision: Reuse existing `PasswordEncoder` (`BCryptPasswordEncoder` default in Spring Security) to securely hash passwords before persistence.
- Decision: Implement deterministic error handling and status code mapping for validation (400), conflicts (409), server errors (500), and database unavailability (503).

## Phase 1: Design & Contracts
- **Data Model (`data-model.md`)**: Existing `User` entity and DTO mapping.
- **API Contracts (`contracts/auth-register-api.yaml`)**: OpenAPI specification for `POST /api/v1/auth/register`.
- **Quickstart Validation (`quickstart.md`)**: End-to-end verification steps using Java 19 and Gradle wrapper on port 8081.
