# EasyTechnicianAccess (ETA) Frontend

Production-grade Next.js (App Router, TypeScript, Tailwind CSS) web application for customer labour booking and management.

## Quickstart Guide

1. **Prerequisites**:
   - Ensure backend microservices are running (API Gateway on `http://localhost:8080`).
   - Node.js 18+ installed.

2. **Installation & Running**:
   ```bash
   cd easy-technician-access
   npm install
   npm run dev
   ```
   Open [http://localhost:3000](http://localhost:3000) in your browser.

3. **Running Tests**:
   ```bash
   npm test
   ```

4. **Production Build**:
   ```bash
   npm run build
   ```

---

## Security Review: JWT Authentication & Token Handling

- **Current Implementation**: JWT tokens received from the Spring Boot Auth Service are stored securely in browser `localStorage` and attached via centralized Axios interceptors (`src/lib/api-client.ts`).
- **Backend Compatibility**: Existing Spring Boot microservices rely on stateless Bearer token authentication (`Authorization: Bearer <token>`). The backend does not currently issue `HttpOnly` / `Secure` cookies.
- **Migration Assessment**: Migrating to `HttpOnly` cookies would require modifying Spring Security configuration and auth-service controllers. Per project constraints ("Do not modify existing Spring Boot microservices"), localStorage token storage remains the optimal and compatible approach for the ETA MVP.
- **Best Practices**: Tokens are stored under namespaced keys (`eta_auth_token`), cleared on logout, and never logged or exposed in error messages.

---

## Production Deployment Guide (`www.easytechnician.com` & `api.easytechnician.com`)

### 1. Domain Configuration
- **Frontend App (`www.easytechnician.com`)**:
  - Deploy to Vercel, AWS Amplify, or a containerized Nginx/Node.js cluster.
  - Set environment variable:
    ```env
    NEXT_PUBLIC_API_GATEWAY_URL=https://api.easytechnician.com
    ```
- **API Gateway (`api.easytechnician.com`)**:
  - Configure Spring Cloud API Gateway behind Nginx / AWS ALB with TLS/SSL termination.
  - Ensure CORS policies allow requests originating from `https://www.easytechnician.com`.

### 2. Build & Deployment Pipeline
```bash
npm ci
npm run build
npm start
```
