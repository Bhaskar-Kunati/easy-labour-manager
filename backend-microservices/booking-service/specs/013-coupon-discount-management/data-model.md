# Data Model: Coupon / Discount Management & Admin APIs

**Feature**: `013-coupon-discount-management`

## Entities & Relationships

### 1. Coupon
- `id`: Long (Primary Key)
- `code`: String (Unique, non-null)
- `discountType`: Enum (`PERCENTAGE`, `FIXED`)
- `discountValue`: BigDecimal (non-null)
- `minOrderAmount`: BigDecimal
- `maxDiscount`: BigDecimal
- `startDate`: LocalDateTime
- `endDate`: LocalDateTime
- `usageLimit`: Integer (Global limit)
- `perCustomerLimit`: Integer
- `status`: Enum (`ACTIVE`, `INACTIVE`, `EXPIRED`)
- `createdAt`: LocalDateTime
- `updatedAt`: LocalDateTime

### 2. CouponUsage
- `id`: Long (Primary Key)
- `couponId`: Long (Foreign Key to Coupon)
- `userId`: String (Customer identifier)
- `bookingId`: Long (Foreign Key to Booking)
- `usedAt`: LocalDateTime
