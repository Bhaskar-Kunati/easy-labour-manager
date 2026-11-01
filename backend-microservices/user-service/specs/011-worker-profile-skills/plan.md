# Implementation Plan: Worker Profile & Skill Management

**Branch**: `011-worker-profile-skills` | **Date**: October 6, 2026 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `backend-microservices/user-service/specs/011-worker-profile-skills/spec.md`

## Summary

Implement worker profile retrieval and update (`GET/PUT /api/v1/workers/profile`) and worker skill management (`POST /api/v1/workers/skills`, `DELETE /api/v1/workers/skills/{skillId}`) in `user-service`. Reuse existing `WorkerProfile` and `WorkerSkill` domain models, securing endpoints with JWT/SecurityContext to ensure strict worker ownership and isolation.

## Technical Context

**Language/Version**: Java 19

**Primary Dependencies**: Spring Boot 3.2, Spring Web, Spring Security, Spring Data JPA, Lombok, PostgreSQL Driver

**Storage**: PostgreSQL (`el_db` database, tables `worker_profiles`, `worker_skills`)

**Testing**: JUnit 5, Spring Boot Test, MockMvc, Spring Security Test

**Target Platform**: JVM / Docker container

**Project Type**: Spring Boot Microservice (`user-service`)

**Performance Goals**: <200ms p95 latency for profile and skill operations

**Constraints**: Strict data isolation; zero trust of client-supplied worker/user IDs; protected administrative fields

**Scale/Scope**: Microservice endpoints inside `user-service`

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- Reuses existing architecture, libraries, and database entities: **PASS**
- Enforces strict security and JWT identity extraction: **PASS**
- No out-of-scope features (dispatch, payment, chat): **PASS**

## Project Structure

### Documentation (this feature)

```text
backend-microservices/user-service/specs/011-worker-profile-skills/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
└── contracts/           # Phase 1 output
    └── rest-api.yaml
```

### Source Code (repository root)

```text
backend-microservices/user-service/
├── src/
│   ├── main/java/com/easylabour/userservice/
│   │   ├── domain/model/
│   │   │   ├── WorkerProfile.java
│   │   │   └── WorkerSkill.java
│   │   ├── domain/repository/
│   │   │   ├── WorkerProfileRepository.java
│   │   │   └── WorkerSkillRepository.java
│   │   ├── application/service/
│   │   │   └── WorkerService.java
│   │   └── web/controller/
│   │       └── WorkerController.java
│   └── test/java/com/easylabour/userservice/
```

**Structure Decision**: Existing Spring Boot microservice structure (`user-service`).
