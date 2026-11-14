# Feature Specification: ETA Web Application UI Redesign

**Feature Branch**: `002-eta-ui-redesign`

**Created**: November 14, 2026

**Status**: Draft

**Input**: User description: Redesign the EasyTechnicianAccess (ETA) web application UI to closely match the uploaded reference screenshot at design-reference/eta-homepage-reference.png. DESIGN REQUIREMENTS: Brand ETA – Easy Technician Access, professional blue-and-white palette (#0866E9), dark navy headings, white backgrounds, light blue section backgrounds, rounded cards, subtle shadows, clean spacing, modern typography. Homepage header, hero section, services catalog grid, why choose ETA, how it works, app promotion banner, trust statistics, full design system propagation across customer and technician dashboards/pages, Next.js tech stack, backend integration preservation.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Explore Redesigned Homepage & Service Catalog (Priority: P1)

As a customer visiting easytechnician.com, I want to view a polished, professional homepage matching the ETA design system, with clear navigation, a compelling hero section ("Find Trusted Technicians Near You"), service cards with icons, benefits, how-it-works steps, and trust statistics so that I can easily discover and book home services.

**Why this priority**: P1 delivers the core public-facing visual identity and conversion funnel for customers landing on the web application.

**Independent Test**: Can be fully tested by navigating to the homepage, verifying the `#0866E9` primary blue palette, header navigation, hero search bar, service cards grid, and responsiveness across desktop, tablet, and mobile.

**Acceptance Scenarios**:

1. **Given** a user opens the home page, **When** the page loads, **Then** the header displays the ETA logo, navigation links (Home, Services, For Technicians, About Us, Help), Bengaluru location selector, and Login/Book a Service buttons.
2. **Given** the hero section is displayed, **When** the user views the split layout, **Then** it shows the headline "Find Trusted Technicians Near You", supporting text, search bar, trust indicators, and professional image on the right.
3. **Given** the services section is rendered, **When** users view AC Repair, Plumbing, Electrical, Carpentry, Painting, Cleaning, Appliance Repair, and Other Services, **Then** attractive service cards with icons and a "View All Services" button appear, backed by existing catalog APIs.

---

### User Story 2 - Consistent Design System Across Customer & Technician Pages (Priority: P2)

As a registered customer or technician, I want all internal dashboards, catalogs, booking flows, tracking pages, payment screens, technician profiles, job histories, and wallet pages to share the same polished blue-and-white design system, typography, rounded cards, and responsive layouts so that my entire experience feels cohesive and trustworthy.

**Why this priority**: P2 ensures that authenticated workflows and dashboards maintain visual consistency with the newly redesigned homepage.

**Independent Test**: Can be tested by logging in as a customer or technician, navigating through dashboards, bookings, wallet, and profile pages, and verifying consistent navbar, buttons, cards, forms, and color hierarchy.

**Acceptance Scenarios**:

1. **Given** an authenticated customer navigates their dashboard and booking pages, **When** pages render, **Then** they display consistent navy headings, white backgrounds, rounded cards, and subtle shadows matching the primary `#0866E9` theme.
2. **Given** an authenticated technician views their dashboard, job history, profile, and wallet pages, **When** pages render, **Then** they maintain a distinct technician persona while utilizing the unified ETA design tokens and responsive layout system.

---

### User Story 3 - Trust Statistics & App Promotion Banner (Priority: P1)

As a visitor exploring ETA, I want to see verified platform statistics (Happy Customers, Rating, Secure Payments, 24/7 Support) and a professional app download promotion banner so that I trust the platform's reliability and know mobile apps are available.

**Why this priority**: P1 reinforces trust conversion and multi-platform reach.

**Independent Test**: Can be tested by inspecting the statistics section for verified backend metrics (or approved static content if API data is pending) and verifying the blue app promotion banner with download buttons and interior imagery.

**Acceptance Scenarios**:

1. **Given** the homepage statistics section, **When** data loads, **Then** verified statistics for happy customers, ratings, secure payments, and support are displayed accurately without invented numbers.
2. **Given** the promotional banner section, **When** viewed on desktop and mobile, **Then** the wide blue banner displays "Professional Home Services at Your Fingertips" alongside responsive layout formatting.

### Edge Cases

- What happens when catalog backend data is temporarily unavailable? The UI gracefully falls back to cached or approved static category representations while logging the connection state.
- How does the UI handle mobile viewport constraints for navigation? The navigation collapses into a responsive mobile menu drawer with touch-friendly targets.
- What happens when location selection (Bengaluru) is modified? The location state updates across relevant service filtering components.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The web application MUST implement the ETA branding and design system featuring primary blue `#0866E9`, dark navy headings, white backgrounds, light blue section backgrounds, rounded cards, subtle shadows, and clean modern typography.
- **FR-002**: The homepage header MUST include the ETA logo, navigation (Home, Services, For Technicians, About Us, Help), Bengaluru location selector, Login and Book a Service action buttons, and responsive mobile navigation.
- **FR-003**: The hero section MUST provide a split layout with headline "Find Trusted Technicians Near You", supporting text, search bar, trust badges (Verified Professionals, Transparent Pricing, Secure Payments), and professional imagery.
- **FR-004**: The services section MUST display service cards with icons for AC Repair & Service, Plumbing, Electrical, Carpentry & Furniture, Painting, Cleaning Services, Home Appliance Repair, and Other Services, integrating with existing backend catalog APIs where available.
- **FR-005**: The platform MUST provide a horizontal benefits section ("Why Choose ETA") highlighting verified technicians, transparent pricing, on-time service, and 24/7 support.
- **FR-006**: The homepage MUST display a three-step "How It Works" visual workflow (Book a Service, Get a Professional, Relax & Pay).
- **FR-007**: The application MUST feature a wide blue promotional banner for app downloads ("Professional Home Services at Your Fingertips") with appropriate store links where applicable.
- **FR-008**: The trust statistics section MUST use approved static marketing content or omit unavailable statistics, as inspection of backend services confirms no dedicated statistics endpoint exists; the system MUST never invent customer counts, ratings, payment figures, or platform statistics.
- **FR-009**: The unified design system MUST be applied consistently across customer views (dashboard, catalog, booking, tracking, payment) and technician views (dashboard, profile, jobs, job history, wallet) while keeping roles distinct.
- **FR-010**: All Spring Boot microservices, API Gateway routes, JWT authentication, RBAC, and backend contracts MUST remain fully preserved without modifying backend endpoints or faking unsupported APIs.

### Key Entities

- **ServiceCategory**: Represents home service offerings (AC Repair, Plumbing, Electrical, etc.) with icons and metadata.
- **BookingEntity**: Represents service requests made by customers and fulfilled by technicians.
- **TechnicianProfile**: Represents background-checked professional profiles, ratings, and wallet balances.
- **PlatformStatistics**: Represents verified aggregate platform metrics (customer count, ratings, payments, support).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of homepage UI components and design tokens strictly adhere to the `#0866E9` color palette and visual hierarchy defined in the reference specifications.
- **SC-002**: Page load performance on desktop and mobile viewports maintains Lighthouse performance scores of 90+ with optimized images and semantic HTML.
- **SC-003**: 100% of existing customer and technician authentication, booking, and payment workflows function correctly without regression.
- **SC-004**: Responsive design layout verifies cleanly across desktop, tablet, and mobile device breakpoints without horizontal overflow or touch target clipping.

## Assumptions

- Existing Next.js app structure (`easy-technician-access`) serves as the base codebase for the UI redesign.
- Spring Boot backend services and API Gateway remain running on localhost:8080 during local testing.
- Icons and graphical assets are sourced from standard accessible icon libraries (e.g., Lucide React or Tailwind SVG icons) matching the reference design.
- Location selector defaults to Bengaluru with extensible location-switching hooks.
