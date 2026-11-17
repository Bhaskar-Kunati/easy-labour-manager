---
description: "Implementation task list for ETA Web Application UI Redesign"
---

# Tasks: ETA Web Application UI Redesign

**Input**: Design documents from `easy-technician-access/specs/002-eta-ui-redesign/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/ui-component-contract.md, quickstart.md

**Organization**: Tasks are grouped by user story and design system phases to enable independent implementation and testing.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Design System Configuration)

**Purpose**: Configure brand colors (`#0866E9`), typography, spacing, shadows, and base Tailwind CSS styles in the Next.js project.

- [x] T001 Update `tailwind.config.js` in `easy-technician-access/tailwind.config.js` to include primary blue `#0866E9`, custom rounded card radii (`rounded-2xl`), subtle shadows, and light blue section backgrounds.
- [x] T002 Update global styles in `easy-technician-access/src/app/globals.css` to define root typography, dark navy heading styles, and clean spacing.
- [x] T003 [P] Create shared design token constants or types in `easy-technician-access/src/types/index.ts` for service categories and platform statistics.

---

## Phase 2: Foundational (Shared UI Components)

**Purpose**: Core UI components and layout infrastructure that MUST be complete before user stories are implemented.

- [x] T004 Redesign shared navigation bar in `easy-technician-access/src/components/common/Navbar.tsx` to include ETA logo, navigation links (Home, Services, For Technicians, About Us, Help), Bengaluru location selector, and Login/Book a Service buttons.
- [x] T005 [P] Update footer component in `easy-technician-access/src/components/common/Footer.tsx` to match brand aesthetics and professional links.
- [x] T006 [P] Implement error banner and loading state components in `easy-technician-access/src/components/common/ErrorBanner.tsx` and loading indicators.
- [x] T007 Ensure root layout in `easy-technician-access/src/app/layout.tsx` wraps all providers, navbar, and footer correctly without breaking existing React Query or auth contexts.

**Checkpoint**: Foundation ready - homepage and portal user stories can now be implemented.

---

## Phase 3: User Story 1 - Explore Redesigned Homepage & Service Catalog (Priority: P1) 🎯 MVP

**Goal**: Deliver the redesigned public homepage matching the reference screenshot, featuring header navigation, split-layout hero section with search, service category grid with icons, why-choose-ETA, how-it-works, app promotion banner, and trust statistics.

**Independent Test**: Navigate to `http://localhost:3000/`, verify `#0866E9` primary palette, split-layout hero with search, 8 service category cards with icons, horizontal benefits, 3-step workflow, app promo banner, and trust stats.

### Implementation for User Story 1

- [x] T008 [P] [US1] Create Hero Section component in `easy-technician-access/src/components/home/HeroSection.tsx` with headline "Find Trusted Technicians Near You", support text, search bar, and trust badges.
- [x] T009 [P] [US1] Create Service Categories Grid component in `easy-technician-access/src/components/home/ServiceCatalogGrid.tsx` for AC Repair, Plumbing, Electrical, Carpentry, Painting, Cleaning, Appliance Repair, and Other Services with icons.
- [x] T010 [P] [US1] Create Why Choose ETA horizontal benefits component in `easy-technician-access/src/components/home/WhyChooseEta.tsx`.
- [x] T011 [P] [US1] Create How It Works 3-step visual workflow component in `easy-technician-access/src/components/home/HowItWorks.tsx`.
- [x] T012 [P] [US1] Create App Promotion Banner component in `easy-technician-access/src/components/home/AppPromoBanner.tsx` with download buttons and interior imagery.
- [x] T013 [P] [US1] Create Trust Statistics section component in `easy-technician-access/src/components/home/TrustStatistics.tsx` displaying verified platform metrics.
- [x] T014 [US1] Assemble and wire up all homepage sections in `easy-technician-access/src/app/page.tsx`.

**Checkpoint**: User Story 1 homepage is fully functional, polished, and independently testable.

---

## Phase 4: User Story 2 - Consistent Design System Across Customer & Technician Pages (Priority: P2)

**Goal**: Apply the unified blue-and-white design system consistently across all customer portals (dashboard, catalog, bookings, tracking, payments) and technician portals (dashboard, profile, jobs, job history, wallet) while preserving role separation and backend integration.

**Independent Test**: Log in as customer and technician, navigate dashboards, catalog, booking, tracking, wallet, and profile pages, and verify consistent styling and preserved API Gateway integration.

### Implementation for User Story 2

- [x] T015 [P] [US2] Update customer dashboard and catalog views in `easy-technician-access/src/app/customer/` with rounded cards, subtle shadows, and navy headings.
- [x] T016 [P] [US2] Update customer booking, tracking, and payment pages in `easy-technician-access/src/app/customer/` maintaining design system consistency.
- [x] T017 [P] [US2] Update technician dashboard, profile, and skills pages in `easy-technician-access/src/app/technician/` with distinct technician persona styling under the unified design system.
- [x] T018 [P] [US2] Update technician assigned jobs, job history, and wallet/earnings pages in `easy-technician-access/src/app/technician/`.
- [x] T019 [US2] Verify JWT auth, RBAC middleware in `easy-technician-access/src/middleware.ts`, and API Gateway communication for all portal routes.

**Checkpoint**: User Stories 1 and 2 are both fully functional with consistent design and preserved backend workflows.

---

## Phase 5: Polish & Quality Assurance

**Purpose**: Responsive testing, accessibility checks, unit tests, TypeScript validation, and production build verification.

- [x] T020 Run responsive testing across desktop, tablet, and mobile viewports for homepage and portals.
- [x] T021 Perform accessibility (ARIA labels, keyboard navigation) and SEO checks on public pages.
- [x] T022 Run and update Jest component test suites in `easy-technician-access/__tests__/`.
- [x] T023 Run TypeScript compiler (`npx tsc --noEmit`), lint check (`npm run lint`), and production build (`npm run build`) to ensure zero errors.
- [x] T024 Validate application against `quickstart.md` scenarios.
- [x] T025 Run Lighthouse performance audit against the production build in `easy-technician-access/` to measure Performance (target 90+), Accessibility, Best Practices, and SEO, record actual scores and failed audits, and document optimization recommendations for any unmet target.

---

## Dependencies & Execution Order

### Phase Dependencies
- **Phase 1 (Setup)**: No dependencies - starts immediately.
- **Phase 2 (Foundational)**: Depends on Phase 1 completion - blocks portal and homepage UI work.
- **Phase 3 (User Story 1)**: Depends on Phase 2 completion.
- **Phase 4 (User Story 2)**: Depends on Phase 2 completion (can run in parallel with Phase 3).
- **Phase 5 (Polish & QA)**: Depends on Phase 3 and Phase 4 completion.

### Parallel Opportunities
- T003, T005, T006 can run in parallel during Setup/Foundational.
- T008 through T013 can run in parallel.
- T015 through T018 can run in parallel.
