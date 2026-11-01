# Common Library (`common-lib`) - Current State Specification

## 1. Purpose
`common-lib` is a shared Java library module in the `backend-microservices` multi-module project. It provides foundational data transfer objects (DTOs), enumerations, asynchronous event messaging contracts, and global exception handling mechanisms used across all microservices (`auth-service`, `user-service`, `booking-service`, `catalog-service`, `payment-service`, `review-service`, `tracking-service`, `notification-service`, and `api-gateway`).

## 2. Responsibilities
- Define shared domain DTOs (`BookingDto`, `PaymentDto`, `UserDto`) for inter-service communication and client contracts.
- Define shared event payloads (`BookingEvent`, `PaymentEvent`, `ReviewCreatedEvent`, `TrackingEvent`) for Apache Kafka messaging.
- Define common system enumerations (`UserRole`, `UserStatus`).
- Provide centralized global REST advice (`GlobalExceptionHandler`) and standard error handling exceptions (`ResourceNotFoundException`).

## 3. Project & Package Structure
- **Group ID**: `com.easylabour.common` (implied by package hierarchy)
- **Base Package**: `com.easylabour.common`
- **Sub-packages & Components**:
  - `com.easylabour.common.dto`:
    - `BookingDto`: Booking record representation (`id`, `customerId`, `customerName`, `labourId`, `labourName`, `serviceType`, `location`, `duration`, `availabilityWindow`, `bookingDate`, `status`).
    - `PaymentDto`: Payment record representation (`id`, `bookingId`, `amount`, `status`, `paymentDate`).
    - `UserDto`: User profile representation (`id`, `username`, `password`, `email`, `firstName`, `lastName`, `phoneNumber`, `skills`, `userType`, `averageRating`, `reviewCount`, `completedBookingsCount`, with fallback setter for legacy `role`).
  - `com.easylabour.common.enums`:
    - `UserRole`: `CUSTOMER`, `WORKER`, `ADMIN`.
    - `UserStatus`: `ACTIVE`, `INACTIVE`, `SUSPENDED`, `BLOCKED`.
  - `com.easylabour.common.event`:
    - `BookingEvent`: Kafka event for booking lifecycle updates.
    - `PaymentEvent`: Kafka event for payment status updates.
    - `ReviewCreatedEvent`: Kafka event for review creation.
    - `TrackingEvent`: Kafka event for location tracking updates.
  - `com.easylabour.common.exception`:
    - `ResourceNotFoundException`: Runtime exception for missing entities.
    - `GlobalExceptionHandler`: REST controller advice handling `ResourceNotFoundException` (HTTP 404) and general `Exception` (HTTP 500) with timestamped JSON error maps.

## 4. Dependencies & Build Configuration (`build.gradle`)
- **Plugin**: `java-library`
- **API Dependencies**:
  - `org.springframework.boot:spring-boot-starter-web`
  - `org.springframework.boot:spring-boot-starter-validation`
  - `com.fasterxml.jackson.core:jackson-databind`
- **Lombok**: Implicitly used across DTOs and events (via project-level configuration / annotation processing).
- **Jar Configuration**: `archiveClassifier = ''` (produces a standard unclassified jar consumed by other microservices via `implementation project(':common-lib')`).

## 5. Consumers & Dependent Services
All backend microservices in `backend-microservices` depend on `common-lib`:
- `api-gateway`
- `auth-service`
- `user-service`
- `booking-service`
- `catalog-service`
- `payment-service`
- `review-service`
- `tracking-service`
- `notification-service`

## 6. Tests
- Currently, there are no dedicated unit or integration test suites implemented inside `common-lib`.

## 7. Current Limitations & Ambiguities
- Error response body structure in `GlobalExceptionHandler` uses ad-hoc `HashMap<String, Object>` rather than a strongly typed error response DTO.
- Date-time formatting annotations (`@JsonFormat`) are only explicitly present on `BookingDto.bookingDate`, relying on default Jackson ObjectMapper configuration for other date fields across events and DTOs.
