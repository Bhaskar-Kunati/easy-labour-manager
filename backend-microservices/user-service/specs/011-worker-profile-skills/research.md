# Research: Worker Profile & Skill Management

**Branch**: `011-worker-profile-skills` | **Date**: October 6, 2026

## 1. Security Context & Worker Identity Extraction
- **Decision**: Extract authenticated user/worker principal from `SecurityContextHolder.getContext().getAuthentication().getName()` and resolve the corresponding `WorkerProfile` via `WorkerProfileRepository.findByUserId(userId)` or `findByUserPhoneNumber(...)`.
- **Rationale**: Ensures zero trust of client-supplied IDs and enforces strict ownership.
- **Alternatives Considered**: Accepting workerId in request path or body (rejected due to severe cross-worker security vulnerabilities).

## 2. Worker Profile Editable vs. Protected Fields
- **Decision**: Restrict `PUT /api/v1/workers/profile` to update only `fullName`, `profileImage`, `experienceYears`, and `workingRadius`. Ignore or reject attempts to modify `verificationStatus`, `rating`, `totalJobs`, or `availabilityStatus` through this endpoint.
- **Rationale**: Protects system and admin-controlled metrics from worker tampering.
- **Alternatives Considered**: Allowing full profile updates (rejected for security/compliance reasons).

## 3. Worker Skill Association & Catalog Validation
- **Decision**: Require `serviceCategoryId` (or `serviceId`) when adding a skill via `POST /api/v1/workers/skills`. Validate against existing catalog data or database constraints, and prevent duplicate associations.
- **Rationale**: Maintains data integrity and prevents invalid taxonomy entries.
- **Alternatives Considered**: Free-form string skills (rejected to maintain integration with catalog service taxonomy).
