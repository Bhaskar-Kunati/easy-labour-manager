# Data Model: Push Notification & In-App Messaging Center (Feature 017)

## Entities

### 1. Notification (MongoDB Collection: `notifications`)
- `id` (String / ObjectId): Unique identifier.
- `userId` (String): Target user ID (Customer or Worker).
- `userType` (String): `CUSTOMER` or `WORKER`.
- `title` (String): Notification title.
- `message` (String): Body content.
- `type` (String): `BOOKING`, `PAYMENT`, `TRACKING`, or `ANNOUNCEMENT`.
- `referenceId` (String): Optional reference (e.g., booking ID, payment ID).
- `read` (Boolean): Read status (`false` by default).
- `createdAt` (Timestamp): Creation timestamp.

### 2. DeviceToken (MongoDB Collection: `device_tokens`)
- `id` (String / ObjectId): Unique identifier.
- `userId` (String): User ID.
- `token` (String): FCM Device Registration Token.
- `deviceType` (String): `ANDROID` or `WEB`.
- `updatedAt` (Timestamp): Last registration update.

### 3. Announcement (MongoDB Collection: `announcements`)
- `id` (String / ObjectId): Unique identifier.
- `adminId` (String): Creator admin ID.
- `title` (String): Announcement title.
- `message` (String): Announcement content.
- `targetGroup` (String): `ALL`, `CUSTOMERS`, or `WORKERS`.
- `createdAt` (Timestamp): Dispatch timestamp.
