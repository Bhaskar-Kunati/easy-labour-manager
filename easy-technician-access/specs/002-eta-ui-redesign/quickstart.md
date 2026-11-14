# Quickstart Validation Guide: ETA Web Application UI Redesign

**Feature**: `002-eta-ui-redesign`
**Created**: November 14, 2026

## Prerequisites
- Node.js 18+ and npm installed.
- Next.js development environment in `easy-technician-access`.
- Spring Boot backend services and API Gateway running on `localhost:8080` (optional for local frontend mock testing, required for full E2E integration).

## Setup & Installation
1. Navigate to frontend directory:
   ```powershell
   cd easy-technician-access
   ```
2. Install dependencies:
   ```powershell
   npm install
   ```

## Running the Application
1. Start the Next.js development server:
   ```powershell
   npm run dev
   ```
2. Open browser at `http://localhost:3000`.

## Validation Steps
1. **Homepage Visual Check**: Verify primary blue `#0866E9` color palette, dark navy headings, white backgrounds, rounded cards, and clean spacing matching reference `design-reference/eta-homepage-reference.png`.
2. **Header Navigation**: Verify ETA logo, links (Home, Services, For Technicians, About Us, Help), Bengaluru location selector, and Login / Book a Service buttons.
3. **Hero & Search**: Verify headline "Find Trusted Technicians Near You", search bar, and trust indicators.
4. **Service Cards**: Verify AC Repair, Plumbing, Electrical, Carpentry, Painting, Cleaning, Appliance Repair, and Other Services cards with icons.
5. **Why Choose ETA & How It Works**: Verify horizontal benefits and 3-step visual workflow.
6. **App Promo & Statistics**: Verify app promotion banner and trust statistics.
7. **Portals Consistency**: Verify customer and technician dashboards and pages adhere to the unified design system.
8. **Automated Tests & Build**:
   ```powershell
   npm test
   npm run build
   ```
