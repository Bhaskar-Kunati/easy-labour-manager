# Research: ETA Web Application UI Redesign

**Feature**: `002-eta-ui-redesign`
**Created**: November 14, 2026

## 1. Brand & Design System Alignment
- **Decision**: Adopt a professional blue-and-white color palette featuring primary blue `#0866E9` (Tailwind custom primary or inline styling where needed), dark navy headings (`text-gray-900` or `#0F172A`), white background surfaces, light blue section backgrounds (`bg-blue-50` or `#F0F6FF`), rounded cards (`rounded-2xl`), subtle shadows (`shadow-sm`, `shadow-md`), clean spacing, and modern typography.
- **Rationale**: Closely matches the reference screenshot (`design-reference/eta-homepage-reference.png`) while maintaining consistency with Tailwind CSS conventions.
- **Alternatives Considered**: Using default Tailwind blue-600 (`#2563EB`) — evaluated and found `#0866E9` is slightly richer and matches the exact brand requirement specified in the prompt.

## 2. Homepage Header & Navigation
- **Decision**: Implement a responsive header with the ETA logo on the left, navigation links (`Home`, `Services`, `For Technicians`, `About Us`, `Help`), a Bengaluru location selector with dropdown capability, and action buttons (`Login` / `Book a Service` / `Sign In` / `Register`). Include a mobile menu toggle for responsive viewports.
- **Rationale**: Satisfies the homepage header requirement (Requirement 2) and ensures mobile responsiveness.
- **Alternatives Considered**: Static desktop navbar without location selector — rejected due to explicit requirement for Bengaluru location selector.

## 3. Hero Section & Split Layout
- **Decision**: Create a responsive split-layout hero section with headline "Find Trusted Technicians Near You", supporting text "Book home services quickly, safely and affordably.", an interactive search bar with service category dropdown and Search button, trust indicators (Verified Professionals, Transparent Pricing, Secure Payments), and professional technician/customer imagery on the right.
- **Rationale**: Direct match to reference design and conversion-optimized landing page structure.

## 4. Service Catalog Grid & Icons
- **Decision**: Implement service cards with Lucide icons for AC Repair & Service, Plumbing, Electrical, Carpentry & Furniture, Painting, Cleaning Services, Home Appliance Repair, and Other Services, plus a "View All Services" button linking to `/customer/catalog`. Integrate with existing backend catalog services via Axios / React Query without inventing fake endpoints.
- **Rationale**: Leverages existing backend catalog data while delivering a polished card-based UI.

## 5. Why Choose ETA & How It Works Sections
- **Decision**: Create a horizontal benefits section ("Why Choose ETA") with Verified & Background Checked Technicians, Transparent Pricing, On-Time Service, and 24/7 Support. Add a 3-step visual "How It Works" workflow (Book a Service, Get a Professional, Relax & Pay).
- **Rationale**: Enhances user trust and clarifies the service journey.

## 6. App Promotion Banner & Trust Statistics
- **Decision**: Build a wide blue promotional banner ("Professional Home Services at Your Fingertips") with app store buttons. For trust statistics, inspection of backend services confirms no dedicated statistics endpoint (`/api/v1/statistics`) exists; therefore, the UI MUST use approved static marketing content or omit unavailable metrics. The system MUST never invent customer counts, ratings, payment figures, or platform statistics, and backend microservices must remain untouched.
- **Rationale**: Promotes multi-platform reach while strictly adhering to backend data preservation rules and avoiding invented data or fake endpoints.

## 7. Unified Design System Propagation
- **Decision**: Apply the same design system (navy headings, white backgrounds, light blue sections, rounded cards, subtle shadows) across customer pages (dashboard, catalog, booking, tracking, payment) and technician pages (dashboard, profile, jobs, job history, wallet) while preserving distinct user roles and workflows.
- **Rationale**: Ensures a cohesive, polished user experience across the entire web application without breaking existing JWT auth, RBAC, or API Gateway routing.
