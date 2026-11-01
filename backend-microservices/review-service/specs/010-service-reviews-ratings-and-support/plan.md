# Implementation Plan: Service Reviews, Ratings & Customer Support

**Branch**: `010-service-reviews-ratings-and-support` | **Date**: October 5, 2026 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `backend-microservices/review-service/specs/010-service-reviews-ratings-and-support/spec.md`

## Summary

Implement customer reviews, ratings (1–5), rating aggregation, and customer support ticketing within `review-service` (Spring Boot 3.2, Java 19, PostgreSQL, JPA/Hibernate, Spring Security JWT). Reviews are restricted to `COMPLETED` bookings with ownership verification, and support tickets provide complaint management for authenticated customers.

## Technical Context

**Language/Version**: Java 19

**Primary Dependencies**: Spring Boot 3.2, Spring Web, Spring Data JPA, Spring Security, PostgreSQL Driver, Lombok, Flyway

**Storage**: PostgreSQL relational database (`review-service` database / schema `V1__init_review_support_schema.sql`)

**Testing**: JUnit 5, Mockito, Spring Boot Test, MockMvc

**Target Platform**: Linux server / JVM 19

**Project Type**: Spring Boot Microservice (`review-service`)

**Performance Goals**: <200ms p95 response time for review submission and retrieval

**Constraints**: Strict authentication, JWT security context, booking ownership verification, `COMPLETED` booking status check for reviews

**Scale/Scope**: Microservice supporting MVP customer reviews and support tickets

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Test-First**: Unit and integration tests required for controllers, services, and repositories. (PASS)
- **Security & Authentication**: Customer identity derived strictly from JWT/security context; no trusting client-supplied IDs. (PASS)
- **Simplicity & Domain Ownership**: Owned entirely by `review-service` without modifying unrelated services. (PASS)

## Project Structure

### Documentation (this feature)

```text
backend-microservices/review-service/specs/010-service-reviews-ratings-and-support/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output
│   └── rest-api.yaml
└── tasks.md             # Phase 2 output (created by /speckit.tasks)
```

### Source Code (`backend-microservices/review-service`)

```text
backend-microservices/review-service/src/
├── main/java/com/easylabour/reviewservice/
│   ├── ReviewServiceApplication.java
│   ├── application/service/
│   │   ├── ReviewService.java
│   │   └── SupportService.java
│   ├── domain/
│   │   ├── model/
│   │   │   ├── Review.java
│   │   │   └── SupportTicket.java
│   │   └── repository/
│   │       ├── ReviewRepository.java
│   │       └── SupportTicketRepository.java
│   └── web/
│       └── controller/
│           ├── ReviewController.java
│           └── SupportController.java
└── resources/
    ├── application.yml
    └── db/migration/
        └── V1__init_review_support_schema.sql
```

**Structure Decision**: Existing Spring Boot microservice structure (`review-service`).
