# Quickstart Guide: Coupon / Discount Management & Admin APIs

**Feature**: `013-coupon-discount-management`

## Overview
This guide outlines how to validate Feature 013 (Coupon Management and Checkout Integration) end-to-end.

## Validation Steps

1. **Admin Coupon Creation**:
   - Authenticate as an administrator (`ROLE_ADMIN`).
   - Send a `POST` request to `/api/v1/admin/coupons` with coupon details.
   - Verify `201 Created` response.

2. **Customer Booking with Coupon**:
   - Authenticate as a customer (`ROLE_CUSTOMER`).
   - Send a `POST` request to `/api/v1/bookings` including a valid `couponCode` in the booking request payload.
   - Verify server-side discount calculation and successful booking creation with discounted total.

3. **Security Verification**:
   - Attempt to access admin coupon endpoints as a customer or unauthenticated user and verify `403 Forbidden` / `401 Unauthorized` responses.
