# Tasks: EasyTechnicianAccess (ETA) Web Application

**Input**: Design documents from `easy-technician-access/specs/001-easy-technician-access/` (`spec.md`, `plan.md`, `research.md`, `data-model.md`)

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md

**Organization**: Tasks are grouped by user story and technical phases to enable independent implementation and testing.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., [US1], [US2], [US3])
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Initialize the Next.js project structure, dependencies, and configuration.

- [x] T001 Initialize Next.js project with App Router and TypeScript in `easy-technician-access/`
- [x] T002 [P] Install core dependencies (`tailwindcss`, `lucide-react`, `@tanstack/react-query`, `react-hook-form`, `zod`, `axios`) in `easy-technician-access/package.json`
- [x] T003 [P] Configure Tailwind CSS and typography in `easy-technician-access/tailwind.config.js`
- [x] T004 [P] Initialize shadcn/ui configuration and base UI primitives in `easy-technician-access/src/components/ui/`
- [x] T005 [P] Setup environment configuration with `NEXT_PUBLIC_API_GATEWAY_URL=http://localhost:8080` in `easy-technician-access/.env.local`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure (API client, authentication context, routing layouts) that MUST be complete before user stories can be implemented.

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T006 Implement centralized Axios API client with base URL and JWT interceptor in `easy-technician-access/src/lib/api-client.ts`
- [x] T007 [P] Implement authentication state management and token storage utility in `easy-technician-access/src/lib/auth.ts`
- [x] T008 [P] Implement TypeScript interfaces and types for users, profiles, services, and bookings in `easy-technician-access/src/types/index.ts`
- [x] T009 Create root layout with TanStack Query provider and toaster notifications in `easy-technician-access/src/app/layout.tsx`
- [x] T010 Implement navigation header and footer layout components in `easy-technician-access/src/components/common/Navbar.tsx` and `Footer.tsx`

**Checkpoint**: Foundation ready - user story implementation can now begin.

---

## Phase 3: User Story 1 - Customer Portal & Booking Management (Priority: P1) 🎯 MVP

**Goal**: Deliver customer registration, login/OTP verification, catalog browsing, booking creation, tracking, payment history, and reviews.

**Independent Test**: Can be fully tested by registering a customer account, browsing catalog services, creating a booking, tracking status, viewing payment history, and submitting a review.

### Implementation for User Story 1

- [x] T011 [P] [US1] Create customer registration and login pages with Zod validation in `easy-technician-access/src/app/(auth)/login/page.tsx` and `register/page.tsx`
- [x] T012 [P] [US1] Implement mobile OTP verification component and verification flow in `easy-technician-access/src/components/customer/OtpVerification.tsx`
- [x] T013 [US1] Implement customer dashboard home view displaying profile and quick stats in `easy-technician-access/src/app/customer/dashboard/page.tsx`
- [x] T014 [P] [US1] Implement service catalog search and listing component in `easy-technician-access/src/app/customer/catalog/page.tsx`
- [x] T015 [P] [US1] Implement service detail and pricing view in `easy-technician-access/src/components/customer/ServiceDetailModal.tsx`
- [x] T016 [US1] Implement booking creation and scheduling form with React Hook Form in `easy-technician-access/src/app/customer/bookings/new/page.tsx`
- [x] T017 [US1] Implement customer bookings list and cancellation management in `easy-technician-access/src/app/customer/bookings/page.tsx`
- [x] T018 [P] [US1] Implement booking tracking view for assigned technicians in `easy-technician-access/src/components/customer/BookingTrackingView.tsx`
- [x] T019 [P] [US1] Implement customer payment history and billing view in `easy-technician-access/src/app/customer/payments/page.tsx`
- [x] T020 [P] [US1] Implement review and rating submission form in `easy-technician-access/src/components/customer/ReviewSubmissionModal.tsx`

**Checkpoint**: At this point, User Story 1 (Customer Portal) should be fully functional and testable independently.

---

## Phase 4: User Story 2 - Technician/Worker Portal & Job Management (Priority: P2)

**Goal**: Deliver technician sign-in, profile/skills management, assigned jobs viewing, job status updates, completed jobs history, and earnings/wallet overview.

**Independent Test**: Can be fully tested by logging into the technician portal, managing profile and skills, accepting/updating assigned jobs, viewing completed jobs, and checking wallet earnings.

### Implementation for User Story 2

- [x] T021 [P] [US2] Implement technician sign-in and profile view in `easy-technician-access/src/app/technician/dashboard/page.tsx`
- [x] T022 [P] [US2] Implement technician skills and availability status management in `easy-technician-access/src/app/technician/profile/page.tsx`
- [x] T023 [US2] Implement assigned jobs listing and detail view in `easy-technician-access/src/app/technician/jobs/page.tsx`
- [x] T024 [US2] Implement job status update actions (accept, in-progress, complete) in `easy-technician-access/src/components/technician/JobStatusActions.tsx`
- [x] T025 [P] [US2] Implement completed jobs history view in `easy-technician-access/src/app/technician/jobs/history/page.tsx`
- [x] T026 [P] [US2] Implement technician wallet and earnings summary view in `easy-technician-access/src/app/technician/wallet/page.tsx`

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently.

---

## Phase 5: User Story 3 - Role-Based Access Control & Gateway Integration (Priority: P1)

**Goal**: Implement protected route middleware and role-based access control ensuring correct API Gateway integration (`http://localhost:8080`) and separation between customer and technician roles.

**Independent Test**: Tested by accessing protected customer or technician routes without valid JWT or incorrect roles, verifying proper redirection or unauthorized responses.

### Implementation for User Story 3

- [x] T027 [P] [US3] Implement Next.js route protection middleware verifying JWT token and user roles in `easy-technician-access/src/middleware.ts`
- [x] T028 [US3] Implement customer layout wrapper enforcing `CUSTOMER` role access in `easy-technician-access/src/app/customer/layout.tsx`
- [x] T029 [US3] Implement technician layout wrapper enforcing `TECHNICIAN` role access in `easy-technician-access/src/app/technician/layout.tsx`
- [x] T030 [US3] Implement global API error handling and gateway connectivity error banner in `easy-technician-access/src/components/common/ErrorBanner.tsx`

**Checkpoint**: Role-based access control and API gateway integration fully secured.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Documentation, testing, responsive UI verification, and deployment preparation.

- [x] T031 [P] Configure unit and component testing setup with Jest and React Testing Library in `easy-technician-access/jest.config.js`
- [x] T032 [P] Write unit tests for core validation schemas and API client in `easy-technician-access/src/__tests__/`
- [x] T033 Perform cross-viewport responsive UI testing across desktop, tablet, and mobile breakpoints
- [x] T034 Verify end-to-end quickstart execution following `quickstart.md`
- [x] T035 [P] Prepare production environment configuration and deployment guide for `www.easytechnician.com` and `api.easytechnician.com` in `easy-technician-access/README.md`
