# Feature Specification: EasyTechnicianAccess (ETA) Web Application

**Feature Branch**: `001-easy-technician-access`

**Created**: 2026-11-11

**Status**: Draft

**Input**: Build EasyTechnicianAccess (ETA), a modern, responsive web application for customers and technicians/workers using the existing Easy Labour Manager platform. PROJECT ARCHITECTURE: - Existing backend: Spring Boot microservices (Java 19+). - API Gateway: Spring Cloud Gateway on port 8080. - Services: auth-service, user-service, booking-service, catalog-service, payment-service, review-service, tracking-service, notification-service, and wallet-service. - Messaging: Apache Kafka. - Databases: PostgreSQL and MongoDB. - Existing Android application: /android-app using Kotlin and Jetpack Compose. - Local infrastructure: devops/docker/docker-compose.yml. - Production infrastructure: devops/k8s/. WEBSITE REQUIREMENTS: 1. Customer Portal: - Register and sign in. - JWT-based authentication and mobile OTP verification. - Manage personal profile and addresses. - Browse and search available services. - View service details and pricing. - Create, manage, and cancel bookings where supported. - Track assigned technicians and booking status. - View payment history. - Submit reviews and ratings. 2. Technician/Worker Portal: - Sign in and manage profile. - Manage skills and availability. - View assigned jobs. - Accept or update job status where supported. - View completed jobs. - View earnings and wallet information where supported. 3. Authentication: - Integrate with the existing auth-service through the API Gateway. - Support JWT authentication. - Support mobile OTP verification if the existing API provides it. - Implement protected routes and role-based access. 4. Frontend: - Use Next.js App Router, React, TypeScript, and Tailwind CSS. - Use shadcn/ui and Lucide icons. - Use TanStack Query for API data management. - Use React Hook Form and Zod for form validation. - Create a professional, modern, responsive UI. - Provide separate customer and technician dashboards. 5. BACKEND INTEGRATION RULES: - First inspect existing controllers, DTOs, API endpoints, gateway routes, authentication, and roles. - Reuse existing backend APIs. - Do not invent API endpoints or request/response structures. - Do not rebuild existing microservices. - Do not modify the existing Android application. - If an API is missing, document the gap separately instead of silently changing the backend. - Use environment variables for API configuration. Do not hardcode production secrets. 6. PROJECT IDENTITY: - Product name: EasyTechnicianAccess. - Brand: ETA. - Website: www.easytechnician.com. - Development API Gateway: http://localhost:8080. - Proposed production API domain: https://api.easytechnician.com.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Customer Portal & Booking Management (Priority: P1)

As a customer, I want to register, authenticate securely via JWT/OTP, browse available labor services, create bookings, track assigned technicians, view payment history, and submit reviews so that I can easily hire and manage service professionals.

**Why this priority**: Core value proposition for the customer-facing side of the platform, enabling service discovery, booking, and lifecycle management.

**Independent Test**: Can be fully tested by registering a customer account, browsing catalog services, creating a booking, viewing booking status, and submitting a review through the customer portal.

**Acceptance Scenarios**:

1. **Given** a visitor on the customer registration page, **When** they submit valid registration details, **Then** an account is created and they receive confirmation.
2. **Given** a registered customer, **When** they log in with valid credentials or complete mobile OTP verification, **Then** they receive a JWT token and are redirected to the customer dashboard.
3. **Given** an authenticated customer, **When** they browse the service catalog and select a service, **Then** they can view detailed pricing and create a service booking.
4. **Given** an active customer booking, **When** the technician is assigned, **Then** the customer can track booking status, view payment history, and submit a rating upon completion.

---

### User Story 2 - Technician/Worker Portal & Job Management (Priority: P2)

As a technician/worker, I want to sign in, manage my profile, update my skills and availability, view assigned jobs, accept/update job status, and view earnings and wallet information so that I can manage my work schedule and track my compensation.

**Why this priority**: Essential for service providers to receive, manage, and complete jobs assigned by customers.

**Independent Test**: Can be fully tested by logging into the technician portal, updating worker availability and skills, viewing assigned jobs, updating job status, and checking wallet earnings.

**Acceptance Scenarios**:

1. **Given** a technician logging into the portal, **When** authentication succeeds with worker credentials, **Then** they are directed to the technician dashboard.
2. **Given** an authenticated technician, **When** they view their profile, **Then** they can update their skills, service categories, and availability status.
3. **Given** pending job assignments, **When** a technician views assigned jobs, **Then** they can accept or update job status (e.g., in-progress, completed).
4. **Given** completed jobs, **When** the technician accesses the wallet section, **Then** they can view earnings and payment history.

---

### User Story 3 - Role-Based Access Control & Gateway Integration (Priority: P1)

As a system administrator, I want all frontend requests to be correctly routed through the API Gateway (port 8080) with JWT validation and role-based access control so that customer and technician routes remain secure and isolated.

**Why this priority**: Ensures security, proper role separation, and correct integration with existing Spring Boot microservices.

**Independent Test**: Can be tested by attempting to access protected customer or technician routes without a valid JWT or with incorrect roles, verifying proper redirection to login or unauthorized error handling.

**Acceptance Scenarios**:

1. **Given** an unauthenticated user, **When** they attempt to access protected customer or technician dashboard routes, **Then** they are redirected to the sign-in page.
2. **Given** an authenticated customer token, **When** they attempt to access technician-only routes, **Then** access is denied with an unauthorized response.
3. **Given** valid API requests from the frontend, **When** requests are routed through the API Gateway at `http://localhost:8080`, **Then** backend microservices process them using existing DTOs and endpoints without modification.

---

### Edge Cases

- What happens when backend microservices or the API Gateway are unreachable? The frontend displays a graceful connectivity error banner with retry options.
- What happens when an OTP verification code expires or is invalid? The UI prompts the user to request a new OTP code.
- What happens when a JWT token expires during an active session? The system attempts token refresh or redirects the user to sign in again securely.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST allow customers to register, sign in, and verify mobile numbers via OTP integrated with `auth-service`.
- **FR-002**: The system MUST provide customer profile and address management via `user-service`.
- **FR-003**: The system MUST allow browsing and searching of available services and pricing via `catalog-service`.
- **FR-004**: The system MUST enable customers to create, view, manage, and cancel bookings via `booking-service`.
- **FR-005**: The system MUST provide real-time tracking of assigned technicians and booking status via `tracking-service` and `booking-service`.
- **FR-006**: The system MUST display payment history and process payments via `payment-service`.
- **FR-007**: The system MUST allow customers to submit reviews and ratings via `review-service`.
- **FR-008**: The system MUST allow technicians to sign in, manage their profile, skills, and availability via `user-service` and `auth-service`.
- **FR-009**: The system MUST allow technicians to view assigned jobs, accept/update job status, and view completed jobs via `booking-service`.
- **FR-010**: The system MUST allow technicians to view earnings and wallet details via `wallet-service`.
- **FR-011**: The system MUST implement protected routes with role-based access control (Customer vs. Technician/Worker) using Next.js App Router.
- **FR-012**: The system MUST communicate with backend microservices exclusively through the API Gateway at `http://localhost:8080` using environment variables.

### Key Entities

- **Customer Profile**: Represents customer account details, contact info, and saved addresses.
- **Technician Profile**: Represents worker details, skills, service categories, and availability status.
- **Service Catalog**: Represents offered labor services, descriptions, and pricing tiers.
- **Booking**: Represents a scheduled or active service request linking a customer, assigned technician, status, and payment.
- **Wallet/Transaction**: Represents technician earnings, payouts, and customer payment records.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Customers can complete account registration and service booking creation in under 3 minutes.
- **SC-002**: 95% of API requests routed through the API Gateway successfully complete without gateway errors.
- **SC-003**: 90% of users successfully complete primary navigation and booking tasks on first attempt without guidance.
- **SC-004**: The responsive web application renders correctly and maintains usability across desktop, tablet, and mobile viewports.

## Assumptions

- Existing Spring Boot microservices and API Gateway are deployed and accessible locally at `http://localhost:8080`.
- Existing PostgreSQL and MongoDB databases contain necessary seed data for catalog services and user roles.
- The existing `auth-service` supports JWT issuance and mobile OTP verification endpoints as defined in its microservice specs.
- The web frontend will be developed using Next.js App Router, TypeScript, Tailwind CSS, shadcn/ui, TanStack Query, and React Hook Form with Zod.
