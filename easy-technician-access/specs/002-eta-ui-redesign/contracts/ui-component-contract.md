# UI Component & Integration Contract: ETA Web Application Redesign

**Feature**: `002-eta-ui-redesign`
**Created**: November 14, 2026

## 1. Homepage Header Contract
- **Component**: `Navbar` (`src/components/common/Navbar.tsx`)
- **Props/State**: User authentication state, location state ("Bengaluru").
- **Interactions**: Navigation links (Home, Services, For Technicians, About Us, Help), location change dropdown, Login / Sign In, Book a Service, Logout.

## 2. Hero Section Contract
- **Component**: `HeroSection` (`src/components/home/HeroSection.tsx`)
- **Props**: Search keyword, selected service category, location.
- **Interactions**: Submitting search query redirects to `/customer/catalog?search=query`.

## 3. Service Categories Grid Contract
- **Component**: `ServiceCatalogGrid` (`src/components/home/ServiceCatalogGrid.tsx`)
- **Data Source**: Existing catalog service endpoints via API Gateway (`http://localhost:8080/api/v1/catalog`).
- **Cards**: AC Repair & Service, Plumbing, Electrical, Carpentry & Furniture, Painting, Cleaning Services, Home Appliance Repair, Other Services.

## 4. Trust Statistics & App Promotion Contract
- **Components**: `TrustStatisticsSection`, `AppPromoBanner`
- **Data Source**: Verified backend statistics or approved static content.
- **Actions**: App download buttons linking to official app store release links (when active).
