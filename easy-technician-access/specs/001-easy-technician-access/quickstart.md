# Quickstart & Validation Guide: EasyTechnicianAccess (ETA)

**Feature Branch**: `001-easy-technician-access`

**Created**: 2026-11-11

## Prerequisites

1. **Backend Infrastructure**: Docker containers running (PostgreSQL, MongoDB, Kafka).
2. **Spring Boot Microservices**: API Gateway running on port `8080` along with required services (`auth-service`, `user-service`, `booking-service`, `catalog-service`, `payment-service`, `review-service`, `tracking-service`, `wallet-service`).

## Setup & Running the Frontend

1. Navigate to the frontend directory:
   ```bash
   cd easy-technician-access
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Configure environment variables (`.env.local`):
   ```env
   NEXT_PUBLIC_API_GATEWAY_URL=http://localhost:8080
   ```
4. Start the development server:
   ```bash
   npm run dev
   ```
5. Open `http://localhost:3000` in your browser.

## Validation Scenarios

1. **Customer Registration & Login**: Navigate to `/register`, create a customer account, verify via OTP, log in, and verify redirection to `/customer/dashboard`.
2. **Service Browsing & Booking**: Browse catalog items, select a service, create a booking, and verify appearance in `/customer/bookings`.
3. **Technician Portal**: Log in with technician credentials, verify assigned jobs at `/technician/jobs`, update job status, and check wallet earnings.
