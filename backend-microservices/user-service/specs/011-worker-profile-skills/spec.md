# Feature Specification: Worker Profile & Skill Management

**Feature Branch**: `011-worker-profile-skills`
**Status**: Draft

## User Scenarios & Testing

### User Story 1: Worker Profile Retrieval
As an authenticated registered worker, I want to view my profile details so that I can verify my platform representation (full name, profile image, experience years, working radius, availability status, verification status, rating, and total jobs).

- **Acceptance Scenario 1 (Successful Retrieval)**:
  - Given a registered worker is authenticated with a valid JWT.
  - When they send a `GET /api/v1/workers/profile` request.
  - Then the system returns HTTP 200 OK with their worker profile object containing full name, profile image, experience years, working radius, availability status, verification status, rating, and total jobs.
- **Acceptance Scenario 2 (Unauthorized Access)**:
  - Given an unauthenticated user or customer account.
  - When they attempt to access `GET /api/v1/workers/profile`.
  - Then the system returns HTTP 401 Unauthorized or HTTP 403 Forbidden.

### User Story 2: Worker Profile Update
As an authenticated registered worker, I want to update my profile information (full name, profile image, experience years, working radius) so that my profile remains current.

- **Acceptance Scenario 1 (Successful Update)**:
  - Given an authenticated worker.
  - When they send a `PUT /api/v1/workers/profile` request with updated editable fields (e.g., experience years, working radius, full name).
  - Then the system updates the profile successfully and returns HTTP 200 OK with the updated profile.
- **Acceptance Scenario 2 (Protected Fields Protection)**:
  - Given an authenticated worker.
  - When they attempt to modify system-controlled or admin-controlled fields (`verificationStatus`, `rating`, `totalJobs`, `availabilityStatus`) in the update payload.
  - Then the system ignores or rejects modifications to those protected fields.

### User Story 3: Worker Skill Management
As an authenticated registered worker, I want to add and remove service skills based on existing catalog categories so that customers know what services I offer.

- **Acceptance Scenario 1 (Add Skill Success)**:
  - Given an authenticated worker and an existing catalog service/category.
  - When they send a `POST /api/v1/workers/skills` request with a valid catalog service reference.
  - Then the system adds the skill and returns HTTP 200 OK/Created.
- **Acceptance Scenario 2 (Duplicate Skill Rejection)**:
  - Given a worker already has a specific skill.
  - When they attempt to add the same skill again (`POST /api/v1/workers/skills`).
  - Then the system rejects the request with HTTP 400 Bad Request.
- **Acceptance Scenario 3 (Remove Skill Success)**:
  - Given an authenticated worker owns a specific skill (`skillId`).
  - When they send a `DELETE /api/v1/workers/skills/{skillId}` request.
  - Then the system removes the skill and returns HTTP 200 OK/No Content.
- **Acceptance Scenario 4 (Cross-Worker Skill Deletion Rejection)**:
  - Given a worker attempts to delete a skill belonging to another worker.
  - When they send `DELETE /api/v1/workers/skills/{skillId}`.
  - Then the system rejects the request with HTTP 403 Forbidden / 404 Not Found.

## Functional Requirements

- **FR-001**: System MUST provide `GET /api/v1/workers/profile` to return the authenticated worker's existing profile.
- **FR-002**: Worker identity MUST be derived strictly from the authenticated JWT/SecurityContext (`SecurityContextHolder`).
- **FR-003**: System MUST provide `PUT /api/v1/workers/profile` allowing workers to update editable fields (`fullName`, `profileImage`, `experienceYears`, `workingRadius`).
- **FR-004**: System MUST protect admin/system-controlled fields (`verificationStatus`, `rating`, `totalJobs`, `availabilityStatus`) from being modified via the worker profile update endpoint.
- **FR-005**: System MUST provide `POST /api/v1/workers/skills` to associate an existing catalog service/category with the worker profile without duplication.
- **FR-006**: System MUST provide `DELETE /api/v1/workers/skills/{skillId}` allowing a worker to remove their own skill while preventing cross-worker skill manipulation.

## Success Criteria

- **SC-001**: Workers can retrieve and update their profile details successfully with sub-second response times.
- **SC-002**: 100% of unauthorized, customer, or cross-worker modification attempts are successfully blocked.
- **SC-003**: Duplicate skill associations and invalid catalog skill references are correctly rejected.
