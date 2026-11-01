# Quickstart: Worker Profile & Skill Management

**Branch**: `011-worker-profile-skills` | **Date**: October 6, 2026

## Overview
This guide provides validation scenarios for testing Worker Profile & Skill Management in `user-service`.

## Prerequisites
- Java 19 installed
- PostgreSQL running (via Docker Compose)
- Gradle wrapper in `backend-microservices`

## Setup & Running
1. Start infrastructure:
   ```powershell
   cd devops/docker
   docker-compose up -d
   ```
2. Run `user-service`:
   ```powershell
   cd backend-microservices
   ./gradlew :user-service:bootRun --jvm-args="-Xms128m -Xmx256m"
   ```

## Validation Scenarios

### 1. Retrieve Worker Profile
- **Endpoint**: `GET /api/v1/workers/profile`
- **Headers**: `Authorization: Bearer <worker_jwt_token>`
- **Expected Outcome**: HTTP 200 OK with worker profile details.

### 2. Update Worker Profile
- **Endpoint**: `PUT /api/v1/workers/profile`
- **Headers**: `Authorization: Bearer <worker_jwt_token>`
- **Body**:
  ```json
  {
    "fullName": "Jane Doe Updated",
    "experienceYears": 5,
    "workingRadius": 15
  }
  ```
- **Expected Outcome**: HTTP 200 OK with updated profile details.

### 3. Add Worker Skill
- **Endpoint**: `POST /api/v1/workers/skills`
- **Headers**: `Authorization: Bearer <worker_jwt_token>`
- **Body**:
  ```json
  {
    "serviceCategoryId": "plumbing-101"
  }
  ```
- **Expected Outcome**: HTTP 200 OK with created skill object.

### 4. Remove Worker Skill
- **Endpoint**: `DELETE /api/v1/workers/skills/1`
- **Headers**: `Authorization: Bearer <worker_jwt_token>`
- **Expected Outcome**: HTTP 200 OK / No Content.
