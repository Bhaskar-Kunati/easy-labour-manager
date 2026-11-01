# Data Model: Worker Booking / Job Assignment & Management

**Feature**: `012-worker-job-assignment`

## Entities & Relationships

### 1. Booking (Existing Entity)
- `id`: Long (Primary Key)
- `bookingNumber`: String (Unique)
- `customerId`: String (Customer reference)
- `serviceId`: Long (Catalog service reference)
- `status`: BookingStatus (e.g. `PENDING`, `CONFIRMED`, `WORKER_ASSIGNED`, `SERVICE_COMPLETED`, etc.)
- `serviceDateTime`: LocalDateTime
- `assignedWorkerCount`: Integer

### 2. BookingWorker (Existing Entity)
- `id`: Long (Primary Key)
- `booking`: Booking (Many-to-One relationship)
- `workerId`: String (Worker reference from user-service)
- `status`: BookingWorkerStatus (e.g. `ACCEPTED`, `ON_THE_WAY`, `ARRIVED`, `SERVICE_STARTED`, `SERVICE_COMPLETED`)
- `assignedAt`: LocalDateTime
- `acceptedAt`: LocalDateTime
- `serviceCompletedAt`: LocalDateTime

### 3. WorkerProfile & WorkerSkill (User Service Entities)
- Reused from Feature 011 for eligibility verification (verification status, skills, availability).
