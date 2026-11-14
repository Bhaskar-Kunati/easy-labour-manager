# Research & Technical Decisions: EasyTechnicianAccess (ETA)

**Feature Branch**: `001-easy-technician-access`

**Created**: 2026-11-11

## Research Topics & Decisions

### 1. Frontend Framework: Next.js App Router
- **Decision**: Use Next.js 14+ with App Router, TypeScript, and Tailwind CSS.
- **Rationale**: Provides exceptional performance through Server Components, robust client-side interactivity, built-in routing for customer and technician portals, and seamless integration with React ecosystem libraries.
- **Alternatives Considered**: Vite + React SPA (rejected due to lack of built-in server-side routing and SEO optimization).

### 2. State & Data Management: TanStack Query
- **Decision**: Use TanStack Query (React Query) for server state management.
- **Rationale**: Automatically handles caching, background refetching, pagination, mutation optimistic updates, and error retry logic when communicating with Spring Boot microservices via the API Gateway.
- **Alternatives Considered**: Redux Toolkit (rejected as overly verbose for server-state caching needs).

### 3. Form Validation: React Hook Form + Zod
- **Decision**: Use React Hook Form with Zod schema validation.
- **Rationale**: Delivers high-performance unmanaged form state with robust, type-safe validation schemas matching backend DTO constraints.
- **Alternatives Considered**: Formik + Yup (rejected due to heavier bundle size and less optimal TypeScript inference).

### 4. API Integration & Authentication
- **Decision**: Axios client routed through Spring Cloud API Gateway (`http://localhost:8080`) with interceptors for JWT bearer token injection.
- **Rationale**: Reuses existing backend auth contracts without modifying backend services, ensuring secure token propagation across microservices.
