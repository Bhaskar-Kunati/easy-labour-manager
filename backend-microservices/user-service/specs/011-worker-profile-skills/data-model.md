# Data Model: Worker Profile & Skill Management

**Branch**: `011-worker-profile-skills` | **Date**: October 6, 2026

## Entities

### 1. WorkerProfile
Represents a registered worker's professional profile on the platform.
- `id` (Long, PK) - Primary key
- `user` (User, OneToOne) - Associated user entity (`user_id`)
- `fullName` (String, Not Null) - Worker's full name
- `profileImage` (String, Nullable) - Profile image URL/path
- `experienceYears` (Integer, Nullable) - Years of professional experience
- `rating` (Double, Default 0.0) - Aggregate rating (System-controlled)
- `totalJobs` (Integer, Default 0) - Total completed jobs (System-controlled)
- `availabilityStatus` (Enum: `AVAILABLE`, `BUSY`, `OFFLINE`) - Availability status (Managed via availability API)
- `verificationStatus` (Enum: `PENDING`, `APPROVED`, `REJECTED`) - Verification status (Admin-controlled)
- `workingRadius` (Integer, Default 10) - Operating radius in km
- `createdAt` (LocalDateTime) - Creation timestamp
- `updatedAt` (LocalDateTime) - Last update timestamp

### 2. WorkerSkill
Represents a service skill/category associated with a worker profile.
- `id` (Long, PK) - Primary key
- `worker` (WorkerProfile, ManyToOne) - Associated worker profile (`worker_id`)
- `serviceCategoryId` (String, Not Null) - Catalog service category identifier
- `createdAt` (LocalDateTime) - Creation timestamp
- **Constraints**: Unique constraint on `(worker_id, service_category_id)` to prevent duplicate skills.
