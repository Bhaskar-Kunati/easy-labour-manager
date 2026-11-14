# Technical Implementation Plan: EasyTechnicianAccess (ETA) Web Application

**Feature Branch**: `001-easy-technician-access`

**Created**: 2026-11-11

**Status**: Ready for Review

## Overview

EasyTechnicianAccess (ETA) is a modern, responsive web application built for customers and technicians/workers to interact with the existing Easy Labour Manager platform. It provides separate portals for customers and technicians, robust JWT authentication, mobile OTP verification, service discovery, real-time tracking, booking management, and wallet earnings visualization.

The frontend is built with Next.js App Router, TypeScript, Tailwind CSS, shadcn/ui components, TanStack Query, React Hook Form, and Zod, communicating exclusively through the Spring Cloud API Gateway at `http://localhost:8080`.

## Architectural Decisions

1. **Framework**: Next.js 14+ (App Router) for server-side rendering, static generation, and robust routing (customer vs. technician dashboards).
2. **State Management & Data Fetching**: TanStack Query (React Query) for asynchronous server state management, caching, and background synchronization.
3. **Form Validation**: React Hook Form combined with Zod schemas for robust, type-safe client-side validation.
4. **UI Styling & Components**: Tailwind CSS for styling, shadcn/ui for accessible UI primitives, and Lucide React for modern icons.
5. **API Integration Layer**: Centralized Axios client configured with base URL `http://localhost:8080` (or environment variable `NEXT_PUBLIC_API_GATEWAY_URL`), automatic JWT injection via request interceptors, and error handling for unauthorized/gateway failures.
6. **Role-Based Access Control (RBAC)**: Route protection middleware and layout wrappers distinguishing between `CUSTOMER` and `TECHNICIAN` roles based on JWT claims.

## Project Structure (`easy-technician-access/`)

```text
easy-technician-access/
├── src/
│   ├── app/
│   │   ├── (auth)/
│   │   │   ├── login/
│   │   │   └── register/
│   │   ├── customer/
│   │   │   ├── dashboard/
│   │   │   ├── bookings/
│   │   │   ├── catalog/
│   │   │   └── profile/
│   │   ├── technician/
│   │   │   ├── dashboard/
│   │   │   ├── jobs/
│   │   │   └── wallet/
│   │   ├── layout.tsx
│   │   └── page.tsx
│   ├── components/
│   │   ├── ui/ (shadcn components)
│   │   ├── customer/
│   │   ├── technician/
│   │   └── common/
│   ├── lib/
│   │   ├── api-client.ts
│   │   ├── auth.ts
│   │   └── utils.ts
│   ├── hooks/
│   └── types/
├── public/
├── specs/
│   └── 001-easy-technician-access/
│       ├── spec.md
│       ├── plan.md
│       ├── research.md
│       ├── data-model.md
│       ├── quickstart.md
│       └── checklists/
├── package.json
├── tsconfig.json
└── tailwind.config.js
```

## Integration Strategy with Spring Boot Microservices

- **API Gateway (`http://localhost:8080`)**: Single entry point for all microservice requests.
- **Auth Service (`/api/v1/auth/*`)**: Handles customer/worker registration, login, JWT token issuance, and OTP verification.
- **User Service (`/api/v1/users/*`)**: Manages customer profiles, addresses, and technician profiles/skills.
- **Catalog Service (`/api/v1/catalog/*`)**: Provides service listings, categories, and pricing.
- **Booking Service (`/api/v1/bookings/*`)**: Manages service bookings, scheduling, status updates, and technician job assignments.
- **Tracking Service (`/api/v1/tracking/*`)**: Provides real-time location tracking for assigned technicians.
- **Payment Service (`/api/v1/payments/*`)**: Processes payments and billing history.
- **Review Service (`/api/v1/reviews/*`)**: Handles ratings and reviews for completed jobs.
- **Wallet Service (`/api/v1/wallets/*`)**: Manages technician earnings and wallet balances.

## Testing Strategy

1. **Unit Testing**: Jest and React Testing Library for component testing, utility functions, and form validation schemas.
2. **API Integration Testing**: Mock Service Worker (MSW) or integration tests verifying API client calls against gateway endpoints.
3. **End-to-End (E2E) Testing**: Playwright tests covering customer booking flow and technician job assignment lifecycle.
4. **Responsive UI Testing**: Cross-viewport testing ensuring seamless experience across desktop, tablet, and mobile browsers.

## Deployment Strategy

- **Environment Configuration**: `.env.local` for local development pointing to `NEXT_PUBLIC_API_GATEWAY_URL=http://localhost:8080`. Production configuration pointing to `https://api.easytechnician.com`.
- **Build & Hosting**: Next.js optimized production build hosted on modern cloud platforms (Vercel, AWS Amplify, or Docker container).
- **Domain Mapping**: `www.easytechnician.com` for the web frontend and `api.easytechnician.com` for the API Gateway.
